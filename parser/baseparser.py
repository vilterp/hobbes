# inspired by http://dealmeida.net/journal/zen-of-parsing

import re

class BaseParser:
  
  def __init__(self, grammarfile='./parser/grammar.ebnf'):
    """loads rules from grammarfile. parse() will parse with these rules."""
    self.grammarfile = grammarfile
    self.pos = 0
    self.anythingmatched = False
    self.rules_list = [(rule.split(' ::= ')[0],
                       self._parse_rule(rule.split(' ::= ')[1]))
                       for rule in open(grammarfile).read().split(' ;\n')
                       if rule and not rule.startswith('#')]
    self.rules_dict = dict(self.rules_list)    
  
  def _parse_rule(self, rule):
    pos = 0
    tokens = []
    while pos < len(rule):
      remainder = rule[pos:]
      if remainder == ' ;' or remainder == ' ;\n':
        break
      if remainder.startswith(' , '):
        pos += 3
        continue
      re_match = re.match(r'\? (.*) \?',remainder)
      if re_match:
        tokens.append(('(%s)' % re_match.group(1),'regexp'))
        pos += re_match.span()[1]
        continue
      literal_match = re.match(r'"(.*)"',remainder)
      if literal_match:
        tokens.append((literal_match.group(1),'literal'))
        pos += literal_match.span()[1]
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
      # else: another rule
      r = remainder.split(' , ')[0]
      tokens.append((r,'rule'))
      pos += len(r)
    return tokens
  
  def parse(self, code):
    """try to match code against each rule in the grammar,
       as listed in the grammar file, breaking on the first one that works.
    """
    self.code = code
    for name, rule in self.rules_list:
      try:
        return self._match_rule(name, rule)
      except MatchError:
        pass
    if not self.anythingmatched:
      raise EBNFError('No grammar rule matched "%s"' % code)
    del self.code
  
  def _match_rule(self, name, rule):
    # call func in here
    results = self._match_segments(rule)
    getattr(self,name,self.default)(*results)
  
  def _match_segments(self, segments):
    results = []
    for segment in segments:
      results.append(self._match_segment(segment))
    return results
  
  def _match_segment(self, rule):
    print rule
    pattern = rule[0]
    ruletype = rule[1]
    remainder = self.code[self.pos:]
    if ruletype == 'literal':
      if remainder.startswith(pattern):
        return pattern # ?
      else:
        raise MatchError
    elif ruletype == 'regexp':
      match = re.match(pattern,remainder)
      if match:
        return match.group(1)
      else:
        raise MatchError
    elif ruletype == 'repeat':
      results = []
      while 1:
        try:
          results.append(self._match_segment(pattern))
        except MatchError:
          break
      return results
    elif ruletype == 'optional':
      try:
        return self._match_segment(pattern)
      except MatchError:
        pass
    elif ruletype == 'options':
      for option in pattern:
        try:
          return self._match_segment(option)
        except MatchError:
          pass
        raise MatchError
    elif ruletype == 'rule':
      return self._match_segments(pattern) # ?
  

class Token:
  
  def __init__(self, token_type, value):
    self.type = token_type
    self.value = value
  
  def __repr__(self):
    return '<Token %s (%s)>' % (self.value, self.type)
  

class MatchError(Exception):
  pass

class EBNFError(Exception):
  pass
