import re

token_patterns = {
  'assignment': '=',
  'variable': r'^([a-zA-Z_][a-zA-Z0-9_]{,})',
  
  'number': r'(\d{,}\.{,1}\d{,})',
  'string': r'\"([a-zA-Z0-9]+)\"', # TODO: more characters
  
  'openparen': r'\(',
  'closeparen': r'\)',
  
  'add': r'\+',
  'subtract': '-',
  'multiply': r'\*',
  'divide': r'/'
}

def tokenize(expression):
  # TODO: real tokenizer
  tokens = []
  for token in expression.split(' '):
    for name, pattern in token_patterns.iteritems():
      match = re.search(pattern,token)
      if match and match.span() != (0,0):
        t = Token(name,match.group())
        if len(match.groups()):
          t.value = match.group(1)
          if name == 'number':
            t.value = float(t.value) # TODO: use number class
        if name in ['number','string']:
          t.is_literal = True
        tokens.append(t)
        break
  return tokens

class Token:
  
  def __init__(self, token_type, string, value=None):
    self.type = token_type
    self.string = string
    self.value = value
  
  def __repr__(self):
    # return "<Token %s (%s)>" % (self.string, self.type)
    return self.string
  
