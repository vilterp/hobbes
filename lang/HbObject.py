import objectgraph
from objectgraph import true, false, nil

class HbObject:
  
  def __init__(self):
    # give self id, insert into object graph
    self.id = objectgraph.next_id()
    objectgraph.set_obj(self)
    # initialize methods, data
    self.data = {}
    self.methods = {
      'send': self.send,
      'class': self.get_class,
      'is_a?': self.is_a,
      'methods': self.methods,
      'clone': self.clone,
      '==': self.is_same_object,
      'emtpy?': self.empty,
    }
  
  def send(methodname, args):
    if methodname in self.methods:
      return self.methods[methodname](*args)
    elif methodname in self.data:
      return self.data[methodname]
    else:
      raise NameError('no method called %s.' % methodname) # TODO: call method_missing
  
  def get_class(self):
    pass
  
  def is_a(self):
    pass
  
  def methods(self):
    from HbArray import HbArray
    # FIXME: I would just import HbArray at the top, but that makes it go around in circles
    return HbArray(self.methods.keys())
    pass
  
  def clone(self):
    o = HbObject()
    o.data = self.data
    o.methods = self.methods
    return o
  
  def is_same_object(self, other):
    if self.id is other.id:
      return objectgraph.true
    else:
      return objectgraph.false
  
  def empty(self):
    if len(self.data.keys()) is 0:
      return objectgraph.true
    else:
      return objectgraph.false
  
