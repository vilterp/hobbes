import parser, tokenizer, objectgraph

def interpret(line):
  if line.strip().startswith('#'): # comment
    return
  else:
    tree = parser.parse(tokenizer.tokenize(line))
    if tree.value.value == '=':
      objectgraph.set_var(tree.left.value.value,tree.right.evaluate())
    else:
      return tree.evaluate()
