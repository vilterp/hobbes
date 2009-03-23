from hobbesobject import HobbesObject
import objectgraph

class Array(HobbesObject):
  
  def __init__(self, value):
    HobbesObject.__init__(self)
    self.data['values'] = [] # TODO: parse values out from value
    self.methods.update({
      '[]': self.get,
      '[]=': self.set,
      '<<': self.append,
      'empty?': self.empty
    })
  
  def empty(self):
    return len(self.data[values]) == 0
  
  def get(self, index):
    object_id = self.data['values'][index]
    return objectgraph.get_object(object_id)
  
  def set(self, index, value):
    self.data['values'][index] = value.id
  
  def append(self, value):
    self.data['values'].append(value.id)
  
