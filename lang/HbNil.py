from HbObject import HbObject
from objectgraph import true

class HbNil(HbObject):
  
  def __init__(self):
    HbObject.__init__(self)
  
  def is_nil(self):
    return true()
  
