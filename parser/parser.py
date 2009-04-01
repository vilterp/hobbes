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
    self.push_tree()
  
  def default(self, token=None):
    if token:
      self.stack.append(token)
  
  def push_tree(self):
    right = self.stack.pop()
    value = self.stack.pop()
    left = self.stack.pop()
    self.stack.append(Node(value,left,right))
  
