from HbObject import HbObject
from objectgraph import true, false, nil

class HbNumber(HbObject):
  
  def __init__(self, value):
    HbObject.__init__(self)
    self.value = value
    self.methods.update({
      'is?': self.test_is,
      '<=>': self.compare_to,
      '>': self.test_greater_than,
      '<': self.test_less_than,
      'abs': self.absolute_value
    })
  
  def test_is(self, other):
    if self.value == other.value: # FIXME: will '==' work for floats?
      return true()
    else:
      return false()
  
  def compare_to(self, other):
    return HbNumber(cmp(self.value, other.value))
  
  def test_greater_than(self, other):
    if compare_to(self, other).value is 1:
      return true()
    else:
      return false()
  
  def test_less_than(self, other):
    if compare_to(self, other).value is -1:
      return true()
    else:
      return false()
  
  def absolute_value(self):
    if self.value < 0:
      return HbNumber(self.value * -1)
    else:
      return HbNumber(self.value) # self.clone()?
  
