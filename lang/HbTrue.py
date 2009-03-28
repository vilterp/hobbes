from HbObject import HbObject

class HbTrue(HbObject):
  
  classname = 'TrueClass'
  superclass = 'Object'
  
  def __init__(self):
    HbObject.__init__(self)
    self.methods.update({
      
    })
  
  def to_bool(self):
    return self
  
