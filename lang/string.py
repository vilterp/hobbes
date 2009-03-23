from hobbesobject import HobbesObject
import objectgraph

class String(HobbesObject):
  
  def __init__(self, value):
    HobbesObject.__init__(self)
    self.data['value'] = value
    self.methods.extend({
      'empty?': self.empty,
      '[]': self.get_char
    })
  
  def empty(self):
    return self.data['value'].strip() == ''
  
  def get_char(self, index):
    return self.data['value'][index]
  
  # TODO: get range
  # TODO: replace pattern
