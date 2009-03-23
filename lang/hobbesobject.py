import objectgraph

class HobbesObject:
  
  def __init__(self):
    self.id = objectgraph.get_next_id()
    self.methods = {'clone': self.clone, 'send': self.send}
    self.data = {}
  
  def clone(self):
    o = HobbesObject()
    o.methods = self.methods
    o.data = self.data
    return o
  
  def send(self, method, args):
    pass # TODO: implement send
  
