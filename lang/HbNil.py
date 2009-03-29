from HbObject import HbObject
from objectgraph import true, false

class HbNil(HbObject):
  
  classname = 'NilClass'
  superclass = 'Object'
  
  def __init__(self):
    HbObject.__init__(self)
  
  def is_nil(self):
    return true()
  
  def to_bool(self):
    return false()
  
  def to_string(self):
    from HbString import HbString
    return HbString('nil')
  
