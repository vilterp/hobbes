import objectgraph

class SyntaxTree:
  
  def __init__(self, root=None):
    self.root = root
  
  def __repr__(self):
    if self.empty():
      return '<SyntaxTree (empty)>'
    else:
      return '<SyntaxTree %s>' % str(self.root)
  
  def empty(self):
    return self.root is None
  
  def evaluate(self):
    if self.empty():
      return None
    else:
      return self.root.evaluate()
  

class Node:
  
  def __init__(self, value, left=None, right=None):
    self.value = value
    self.left = left
    self.right = right
  
  def __repr__(self):
    ans = "("
    if self.has_left():
      ans += str(self.left)
    ans += "[%s]" % str(self.value)
    if self.has_right():
      ans += str(self.right)
    return ans + ')'
  
  def has_left(self):
    return self.left is not None
  
  def has_right(self):
    return self.right is not None
  
  def evaluate(self):
    if self.value.type == 'variable':
      return objectgraph.get_var(self.value.string)
    elif self.value.string in ['+','-','*','/']:
      return eval("self.left.evaluate() %s self.right.evaluate()" % self.value)
    else:
      return self.value.value
  
