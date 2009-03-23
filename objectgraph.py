object_id = 0
variables = {}
objects = {}

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
