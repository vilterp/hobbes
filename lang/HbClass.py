from HbObject import HbObject
from objectgraph import get_var, nil

class HbClass(HbObject):
  
  classname = 'Class'
  superclass = 'Object'
  
  def __init__(self, name='Class', superclass='Object'):
    HbObject.__init__(self)
    self.name = name
    self.superclass = superclass
    self.methods.update({
      'new': self.new,
      'superclass': self.get_superclass,
    })
  
  def __repr__(self):
    return '<lang.HbClass name=%s id=%d>' % (self.name, self.id)
  
  def new(self, *args, **kwargs):
    # from Hb{self.name} import Hb{self.name}
    import lang
    klass = getattr(lang,'Hb'+self.name)
    return klass(*args, **kwargs)
  
  def get_superclass(self):
    if self.classname == 'Object':
      return nil()
    else:
      return get_var(self.superclass)
  
