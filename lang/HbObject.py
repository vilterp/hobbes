import objectgraph
from objectgraph import true, false, nil, get_var, set_var

class HbObject:
    
  classname = 'Object'
  superclass = None
  
  def __init__(self):
    # give self id, insert into object graph
    self.id = objectgraph.next_id()
    objectgraph.set_obj(self)
    # initialize methods, data
    self.data = {}
    self.methods = {
      'responds_to': self.responds_to,
      'send': self.send,
      'class': self.get_class,
      'is_a?': self.is_a,
      'methods': self.methods,
      'clone': self.clone,
      '==': self.test_same_object,
      'nil?': self.is_nil,
      'to_bool': self.to_bool,
      'to_string': self.to_string,
    }
  
  def __repr__(self):
    """for debugging the interpreter"""
    return '<lang.Hb%s id=%d>' % (self.classname, self.id)
  
  def responds_to(self, methodname):
    if methodname in self.methods:
      return true()
    else:
      return false()
  
  def send(self, methodname, args):
    if methodname in self.methods:
      return self.methods[methodname](*args)
    elif methodname in self.data:
      return self.data[methodname]
    else:
      raise NameError('no method called %s.' % methodname) # TODO: call method_missing
  
  def get_class(self):
    return get_var(self.classname)
  
  def is_a(self, klass):
    if self.get_class() is klass:
      return true()
    elif self.classname == 'Object':
      return false()
    else:
      return self.get_class().get_superclass().is_a(klass)
  
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
  
  def test_same_object(self, other):
    if self.id is other.id:
      return true()
    else:
      return false()
  
  def is_nil(self):
    return false()
  
  def to_bool(self):
    return true()
  
  def to_string(self):
    from HbString import HbString
    return HbString('<%s id=%d>' % (self.classname, self.id))
  
