# inspired by http://dealmeida.net/journal/zen-of-parsing

import interpreter, re
from errors import UnexpectedEOL
from syntaxtree import Node

def parse(line):
  global p
  try:
    return p.parse(line)
  except NameError:
    p = Parser()
    parse(line)

class Parser:
  
  def __init__(self, grammarfile='grammar.ebnf'):
    self.pos = 0
    self.stack = []
    self.rules_list = [tuple(rule.split(' ::= ')) for rule in
                       open(grammarfile).read().split(' ;\n')
                       if rule and not rule.startswith('#')]
    self.rules_dict = {}
    for name, rule in self.rules_list:
      self.rules_dict[name] = rule
  
  def parse(self, code):
    self.code = code
    for name, rule in self.rules_list:
      self.match_rule(name, rule)
    del self.code
    return self.stack
  
  def match_rule(self, rule_name, rule):
    for rule_segment in rule.split(' , '):
      result = self.match_segment(rule_name, rule_segment)
      if result:
        self.stack.append(result)
  
  def match_segment(self, rule_name, rule_segment):
    remainder = self.code[self.pos:] # part of code remaining to be parsed
    # literal rule
    literal_match = re.match(r'^"(.*)"$',rule_segment)
    if literal_match:
      match = re.match('(%s)' % re.escape(literal_match.group(1)),remainder)
      self.pos += match.span()[1]
      return Token(rule_name,match.group(1))
    # regex rule
    regex_match = re.match(r'\? (.*) \?',rule_segment)
    if regex_match:
      print '(%s)' % regex_match.group(1)
      match = re.match('(%s)' % regex_match.group(1),remainder)
      self.pos += match.span()[1]
      return Token(rule_name,match.group(1))
    # or rule ("a | b | c")
    # FIXME: it thinks these are literal rules
    options = rule_segment.split(' | ')
    if len(options):
      for option in options:
        try:
          return match_segment(option)
        except MatchError:
          pass
      raise MatchError('couldn\'t match "%s" in rule %s' % (rule_segment, rule_name))
    # name of another rule
    return self.match_rule(rule_name,self.rules_dict[rule_name])
  

class Token:
  
  def __init__(self, token_type, value):
    self.type = token_type
    self.value = value
  
  def __repr__(self):
    return '<Token %s (%s)>' % (self.value, self.type)
  

class MatchError(Exception):
  pass
