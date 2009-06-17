class Array {
  def init(values=[]) {
    @values = values
  }
  def [](index) {
    @values[index]
  }
  def []=(index,value) {
    @values[index] = value
  }
  def add(value) {
    @values.add(value)
  }
  def []del(index) {
    del @values[index]
  }
  def length {
    @values.length
  }
  def toString {
    '<Array ' + @values.toString + '>'
  }
}

a = new Array
(1 to 10).each(|x|{
  a.add(x)
})
print(a)
