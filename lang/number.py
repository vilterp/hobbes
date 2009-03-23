from hobbesobject import HobbesObject
import objectgraph

class Number(HobbesObject):
  
  def __init__(self, value):
    HobbesObject.__init__(self)
    self.data['value'] = float(value)
    self.methods.update({
      '+': self.add,
      '-': self.subtract,
      '*': self.multiply,
      '/': self.divide
    })
  
  def add(self, other):
    return Number(self.data['value'] + other.data['value'])
  
  def subtract(self, other):
    return Number(self.data['value'] - other.data['value'])
  
  def multiply(self, other):
    return Number(self.data['value'] * other.data['value'])
  
  def divide(self, other):
    return Number(self.data['value'] / other.data['value'])
  
  # TODO: greater than, less than, etc
