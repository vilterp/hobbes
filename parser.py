import interpreter
from errors import UnexpectedEOL
from syntaxtree import Node

def parse(tokens):
  return Parser(tokens).parse()

class Parser:
  
  def __init__(self, tokens):
    self.tokens = tokens
    self.pos = 0
    self.stack = []
  
  def parse(self):
    if self.line():
      return self.stack[0]
    else:
      raise SyntaxError
  
  def line(self):
    if not self.statement():
      if not self.expression():
        return False
    return True
  
  def statement(self):
    if not self.name():
      return False
    if not self.next_is('symbol','='):
      return False
    else:
      self.read()
    if not self.expression():
      return False
    self.push_tree()
    return True
  
  def expression(self):
    if not self.term():
      return False
    while self.add_op():
      if not self.factor():
        return False
      self.push_tree()
    return True
  
  def term(self):
    if not self.factor():
      return False
    while self.mult_op():
      if not self.factor():
        return False
      self.push_tree()
    return True
  
  def factor(self):
    if self.name():
      return True
    if self.number():
      return True
    if self.next_is('symbol','('):
      self.advance()
      if not self.expression():
        return False
      if not self.next_is('symbol',')'):
        return False
    return False
  
  def name(self):
    if self.next_is('word'):
      self.read()
      return True
    else:
      return False
  
  def number(self):
    if self.next_is('number'):
      self.read()
      return True
    else:
      return False
  
  def add_op(self):
    if self.next_is('symbol','+') or self.next_is('symbol','-'):
      self.read()
      return True
    else:
      return False
  
  def mult_op(self):
    if self.next_is('symbol','*') or self.next_is('symbol','/'):
      self.read()
      return True
    else:
      return False
  
  def next_is(self, token_type, value=None):
    try:
      next = self.peek()
      if value is not None:
        return next.type == token_type and next.value == value
      else:
        return next.type == token_type
    except UnexpectedEOL:
      return False
  
  def peek(self, ahead=0):
    try:
      return self.tokens[self.pos]
    except IndexError:
      raise UnexpectedEOL
  
  def read(self):
    token = self.peek()
    self.advance()
    self.stack.append(token)
    return token
  
  def advance(self):
    self.pos += 1
  
  def rewind(self):
    self.pos -= 1
  
  def push_tree(self):
    right = self.stack.pop()
    value = self.stack.pop()
    left = self.stack.pop()
    self.stack.append(Node(value,left,right))
  
