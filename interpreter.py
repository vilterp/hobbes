import parser, tokenizer, objectgraph

objectgraph.boot()

def interpret(line):
  if line.strip().startswith('#'): # comment
    return
  else:
    tree = parser.parse(tokenizer.tokenize(line))
    if tree.root.value.string == '=':
      objectgraph.set_var(tree.root.left.value.value,tree.root.right.evaluate())
    else:
      return tree.evaluate()
