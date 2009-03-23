latest_id = 0
variables = {}
objects = {}

def set_var(name, value):
  global variables, objects
  new_id = get_next_id()
  objects[new_id] = value
  variables[name] = new_id

def get_var(name):
  global variables
  try:
    return get_object(variables[name])
  except KeyError:
    raise NameError(name) # TODO: exception handling...

def get_object(object_id):
  return objects[object_id]

def get_next_id():
  new_id = latest_id
  latest_id += 1
  return new_id
