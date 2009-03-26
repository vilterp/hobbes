# FIXME: too much code
# FIXME: hangs on unclosed string, regexp
# FIXME: store line number, file, char span in token

def tokenize(expression):
  return Tokenizer(expression).tokenize()

class Tokenizer:
  
  def __init__(self, exp):
    self.pos = 0
    self.exp = exp
    self.buf = ''
  
  def tokenize(self):
    tokens = []
    while self.pos < len(self.exp):
      char = self.peek()
      if char == "\t":
        tokens.append(Token('tab'))
      elif char == ' ':
        if self.peek(1) == ' ': # two spaces
          return tokens.append(Token('tab'))
        else: # just one space
          self.pos += 1
          continue
      elif char == '#': # comment
        break
      elif str.isdigit(char):
        tokens.append(self.get_number())
      elif char == '"' or char == "'":
        self.advance()
        tokens.append(self.get_string(char))
        self.advance()
      elif char == '/': # FIXME: how to distinguish between division & regexp?
        self.advance()
        tokens.append(self.get_regexp())
        self.advance()
      elif str.isalpha(char):
        tokens.append(self.get_word())
      else:
        tokens.append(self.get_symbol())
    return tokens
  
  def get_number(self):
    while str.isdigit(self.peek()):
      self.read()
    return Token('number',self.get_buffer())
  
  def get_string(self, start_char):
    while self.peek() != start_char: # TODO: allow escaping quotes
      self.read()
    return Token('string',self.get_buffer())
  
  def get_regexp(self):
    while self.peek() != '/':
      self.read()
    return Token('regexp',self.get_buffer())
  
  def get_word(self):
    self.read()
    while str.isalnum(self.peek()):
      self.read()
    return Token('word',self.get_buffer())
  
  def get_symbol(self):
    char = self.read()
    if char == '.':
      if self.peek() == '.' and self.peek(1) == '.': # '...' (inclusive range)
        self.read()
        self.read()
        return Token('symbol',self.get_buffer())
      elif self.peek() == '.': # '..' (range)
        self.read()
        return Token('symbol',self.get_buffer())
      else: # '.'
        return Token('symbol',self.get_buffer())
    elif char == '=':
      if self.peek() == '=': # '=='
        self.read()
        return Token('symbol',self.get_buffer())
      else:
        return Token('symbol',self.get_buffer())
    elif char == '<':
      if self.peek() == '<': # <<
        self.read()
        return Token('symbol',self.get_buffer())
      elif self.peek() == '=': # <=
        self.read()
        return Token('symbol',self.get_buffer())
      else: # just <
        return Token('symbol',self.get_buffer())
    elif char == '>':
      if self.peek() == '=': # >=
        self.read()
        return Token('symbol',self.get_buffer())
      else: # just <
        return Token('symbol',self.get_buffer())
    elif char in '+-/*':
      if self.peek() == '=': # +=, /=, etc
        self.read()
        return Token('symbol',self.get_buffer())
      elif (char == '+' and self.peek() == '+') or (char == '-' and self.peek() == '-'):
        # ++ or --
        self.read()
        return Token('symbol',self.get_buffer())
      else:
        return Token('symbol',self.get_buffer())
    elif char == '!':
      if self.peek() == '=': # !=
        self.read()
        return Token('symbol',self.get_buffer())
      else:
        raise SyntaxError
    elif char in '()[]:':
      return Token('symbol',self.get_buffer())
    else:
      raise SyntaxError
  
  def peek(self, ahead=0):
    try:
      return self.exp[self.pos+ahead]
    except IndexError:
      return ''
  
  def advance(self):
    self.pos += 1
  
  def read(self):
    char = self.peek()
    self.buf += char
    self.advance()
    return char
  
  def get_buffer(self):
    buf = self.buf
    self.buf = ''
    return buf
  

class Token:
  
  def __init__(self, token_type, value=None):
    self.type = token_type
    self.value = value
  
  def __repr__(self):
    return "<Token %s (%s)>" % (self.value, self.type)
  
