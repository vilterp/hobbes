from HbObject import HbObject

class HbFalse(HbObject):
  
  classname = 'FalseClass'
  superclass = 'Object'
  
  def __init__(self):
    HbObject.__init__(self)
    self.methods.update({
      
    })
  
  def to_bool(self):
    return self
  
  def to_string(self):
    from HbString import HbString
    return HbString('false')
  
