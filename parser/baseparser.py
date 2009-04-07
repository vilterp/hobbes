# inspired by http://dealmeida.net/journal/zen-of-parsing

import re

class BaseParser:
  
  def __init__(self, grammarfile='./parser/grammar.ebnf'):
    """loads rules from grammarfile. parse() will parse with these rules."""
    self.grammarfile = grammarfile
    self.pos = 0
    self.rule_names = []
    self.rules = {}
    # read rules from grammar, allowing for comments and
    # multiline rules terminated by ;'s
    if not open(grammarfile).read().endswith('\n'):
      raise EBNFError('grammar file must end with newline.')
    rules = []
    currentrule = ""
    for line in open(grammarfile):
      if line.startswith('#'):
        continue
      elif line.endswith(' ;\n'):
        currentrule += line[:-3]
        rules.append(currentrule)
        currentrule = ""
      else:
        currentrule += line[:-1]
    # parse & store each
    for rule in rules:
      name, pattern = rule.split(' = ')
      self.rule_names.append(name)
      self.rules[name] = self._parse_rule(pattern)
  
  def parse(self, code):
    """try to match code against each rule in the grammar,
       as listed in the grammar file, breaking on the first one that works.
    """
    self.code = code
    for rule_name in self.rule_names:
      try:
        self._match_rule(rule_name)
        del self.code
        return
      except MatchError:
        pass
    raise EBNFError('No grammar rule matched "%s"' % code)
  
  def _parse_rule(self, rule):
    """identify type of each rule segment"""
    pos = 0
    tokens = []
    while pos < len(rule):
      remainder = rule[pos:]
      if remainder.startswith(' , '):
        pos += 3
        continue
      repeat_match = re.match(r'{ (.*) }',remainder)
      if repeat_match:
        tokens.append((self._parse_rule(repeat_match.group(1)),'repeat'))
        pos += repeat_match.span()[1]
        continue
      optional_match = re.match(r'\( (.*) \)',remainder)
      if optional_match:
        tokens.append((self._parse_rule(optional_match.group(1)),'optional'))
        pos += optional_match.span()[1]
        continue
      or_match = re.match(r'\[ (.*) \]',remainder)
      if or_match:
        segment = or_match.group(1)
        options = [self._parse_rule(rule_seg) for rule_seg in segment.split(' | ')]
        tokens.append((options,'options'))
        pos += or_match.span()[1]
        continue
      re_match = re.match(r'\? (.*) \?',remainder)
      if re_match:
        tokens.append(('(%s)' % re_match.group(1),'regexp'))
        pos += re_match.span()[1]
        continue
      literal_match = re.match(r'"(.+)"',remainder)
      if literal_match:
        tokens.append((literal_match.group(1),'literal'))
        pos += literal_match.span()[1]
        continue
      # else: another rule
      r = remainder.split(' , ')[0]
      tokens.append((r,'rule'))
      pos += len(r)
    return tokens
  
  def _match_rule(self, rule_name):
    print 'trying', rule_name
    try:
      results = self._match_segments(self.rules[rule_name])
      print 'calling', rule_name, 'with', results
      try:
        getattr(self,rule_name)(*results)
      except AttributeError:
        self.default(rule_name,*results)
      print self.stack
    except KeyError:
      raise EBNFError('No rule called "%s"' % rule_name)
  
  def _match_segments(self, segments):
    results = []
    for segment in segments:
      result = self._match_segment(segment)
      if result:
        if isinstance(result,list):
          results.extend(result)
        else:
          results.append(result)
    return results
  
  def _match_segment(self, segment):
    pattern = segment[0]
    ruletype = segment[1]
    remainder = self.code[self.pos:]
    if ruletype == 'literal':
      if remainder.startswith(pattern):
        self.pos += len(pattern)
        return pattern
      else:
        raise MatchError
    elif ruletype == 'regexp':
      match = re.match(pattern,remainder)
      if match:
        group = match.group(1)
        self.pos += len(group)
        return group
      else:
        raise MatchError
    elif ruletype == 'repeat':
      results = []
      while 1:
        try:
          results.extend(self._match_segments(pattern))
        except MatchError:
          break
      return results
    elif ruletype == 'optional':
      try:
        return self._match_segments(pattern)
      except MatchError:
        pass
    elif ruletype == 'options':
      for option in pattern:
        try:
          return self._match_segments(option)
        except MatchError:
          continue
        raise MatchError
    elif ruletype == 'rule':
      self._match_rule(pattern)
  
  def default(self, *args):
    pass
  

class MatchError(Exception):
  pass

class EBNFError(Exception):
  pass
