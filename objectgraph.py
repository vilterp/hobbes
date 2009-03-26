# TODO: make ObjectGraph into a class? An HbObject? should be exposed...

latest_id = 0
variables = {}
objects = {}

def get_obj(object_id):
  return objects[object_id][1]

def set_obj(obj):
  objects[obj.id] = [0, obj] # [number of references, object]

def get_obj(object_id):
  return objects[object_id][1]

def set_var(name, obj):
  if name in variables:
    garbage_collect(variables[name])
  variables[name] = obj.id
  objects[obj.id][0] += 1 # one more reference to it

def get_var(name):
  try:
    return get_obj(variables[name])
  except KeyError:
    raise NameError(name) # TODO: exception handling...

def references_to(object_id):
  return objects[object_id][0]

def next_id():
  global latest_id
  nextid = latest_id
  latest_id += 1
  return nextid

def garbage_collect(object_id=None):
  if object_id is not None:
    if references_to(object_id) is 0:
      del objects[object_id]
  else:
    for object_id in objects.keys:
      garbage_collect(object_id)

def boot():
  # initialize singletons
  from lang import HbTrue, HbFalse, HbNil
  set_var('true', HbTrue())
  set_var('false', HbFalse())
  set_var('nil', HbNil())
  # initialize class objects
  import lang
  for classname in dir(lang):
    if classname.startswith('Hb') and classname not in ['HbTrue','HbFalse','HbNil']:
      name = classname[2:] # Hb[Number]
      klass = getattr(lang, classname)
      set_var(name,lang.HbClass(name,klass.superclass))

def true():
  return get_var('true')

def false():
  return get_var('false')

def nil():
  return get_var('nil')
