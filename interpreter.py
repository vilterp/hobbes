import parser, tokenizer

object_id = 0
variables = {}
objects = {}

def interpret(line):
  if line.strip().startswith('#'): # comment
    return
  else:
    tree = parser.parse(tokenizer.tokenize(line))
    if tree.root.value.string == '=':
      set_var(tree.root.left.value.value,tree.root.right.evaluate())
    else:
      return tree.evaluate()

def set_var(name, value):
  global object_id, variables, objects
  objects[object_id] = value
  variables[name] = object_id
  object_id += 1

def get_var(name):
  global object_id, variables
  try:
    return objects[variables[name]]
  except KeyError:
    raise NameError(name) # TODO: exception handling...
