import syntaxtree, interpreter

def parse(tokens):
  """takes array of tokens, returns syntax tree"""
  tree = syntaxtree.SyntaxTree()
  if len(tokens) > 1 and tokens[0].type == 'variable' and tokens[1].type == 'assignment':
      left = syntaxtree.Node(tokens[0])
      right = parse_expression(tokens[2:])
      root = syntaxtree.Node(tokens[1],left,right)
      tree.root = root
      return tree
  else:
    tree.root = parse_expression(tokens)
    return tree

def parse_expression(tokens):
  """takes array of tokens, returns expression tree"""
  if tokens[0].string == '(' and tokens[-1].string == ')':
    return parse_expression(tokens[1:-1])
  elif len(tokens) is 1:
    return syntaxtree.Node(tokens[0])
  else:
    depth = 0
    for pos in range(len(tokens)):
      token = tokens[pos]
      if token.string == '(':
        depth += 1
      elif token.string == ')':
        depth -= 1
      if depth < 0:
        raise SyntaxError("too many )'s")
      elif token.string in ['+','-','/','*'] and depth == 0:
        left = parse_expression(tokens[:pos])
        right = parse_expression(tokens[pos+1:])
        return syntaxtree.Node(token,left,right)
