from syntaxtree import Node
from baseparser import BaseParser

def parse(line):
  global p
  try:
    return p.parse(line)
  except NameError:
    p = Parser()
    parse(line)

class Parser(BaseParser):
  
  def __init__(self):
    BaseParser.__init__(self)
    self.stack = []
  
  def parse(self, line):
    BaseParser.parse(self,line)
    return self.stack
  
  def expression(self):
    if len(self.stack) is 3:
      self.push_tree()
    elif len(self.stack) is 1:
      token = self.stack.pop()
      self.stack.append(float(token.value))
  
  def operator(self, value):
    self.stack.append(Token(value,'operator'))
  
  def number(self, value):
    self.stack.append(Token(float(value),'number'))
  
  def default(self, *args):
    for arg in args:
      self.stack.append(arg)
  
  def push_tree(self):
    right = self.stack.pop()
    value = self.stack.pop()
    left = self.stack.pop()
    self.stack.append(Node(value,left,right))
  

class Token:
  
  def __init__(self, value, token_type):
    self.type = token_type
    self.value = value
  
  def __repr__(self):
    return '<Token %s (%s)>' % (self.value, self.type)
  