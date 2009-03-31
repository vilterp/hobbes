# inspired by http://dealmeida.net/journal/zen-of-parsing

import re

class BaseParser:
  
  def __init__(self, grammarfile='./parser/grammar.ebnf'):
    """loads rules from grammarfile. parse() will parse with these rules."""
    self.grammarfile = grammarfile
    self.pos = 0
    self.anythingmatched = False
    self.rules_list = [tuple(rule.split(' ::= ')) for rule in
                       open(grammarfile).read().split(' ;\n')
                       if rule and not rule.startswith('#')]
    self.rules_dict = {}
    try:
      for name, rule in self.rules_list:
        self.rules_dict[name] = rule
    except ValueError:
      raise EBNFError('Probably forgot a semicolon.')
  
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
  
  def _match_rule(self, rule_name, rule):
    """match_segment for each segment in rule, appending the resultant
       Token to self.stack. If a segment fails to match, it means
       that this rule has failed, and the MatchError thrown by _match_segment
       will bubble up to parse, which will try the next rule.
    """
    stack = []
    for rule_segment in rule.split(' , '):
      result = self._match_segment(rule_name, rule_segment)
      if result:
        if isinstance(result,list):
          for token in result:
            stack.append(token)
        else:
          stack.append(result)
    return getattr(self,rule_name,self.default)(*stack)
  
  def _match_segment(self, rule_name, rule_segment):
    """try to match rule_segment against the remainder
       of the text to be parsed. If it works, return a token (type=rule_name);
       otherwise throw a MatchError.
    """
    self._strip_whitespace()
    remainder = self.code[self.pos:] # part of code remaining to be parsed
    # repeat rule
    if re.match(r'\{ .* \}', rule_segment):
      match = re.match(r'\{ (.*) \}',remainder).group(1)
      while 1:
        try:
          self.match_rule(rule_name, match)
        except MatchError:
          break
    # or rule ("a | b | c")
    options = rule_segment.split(' | ')
    if len(options) > 1:
      for option in options:
        try:
          return self._match_segment(rule_name, option)
        except MatchError:
          pass
      raise MatchError
    # literal rule
    literal_match = re.match(r'^"(.*)"$',rule_segment)
    if literal_match:
      match = re.match('(%s)' % re.escape(literal_match.group(1)),remainder)
      if match:
        self.pos += match.span()[1]
        return Token(rule_name,match.group(1))
      else:
        raise MatchError
    # regex rule
    regex_match = re.match(r'\? (.*) \?',rule_segment)
    if regex_match:
      match = re.match('(%s)' % regex_match.group(1),remainder)
      if match:
        self.pos += match.span()[1]
        return Token(rule_name,match.group(1))
      else:
        raise MatchError
    # name of another rule
    self._match_rule(rule_segment,self.rules_dict[rule_segment])
  
  def _strip_whitespace(self):
    whitespace = re.match(r'\s*',self.code[self.pos:])
    if whitespace:
      self.pos += whitespace.span()[1]
  
  def default(self, *args):
    pass
  

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
