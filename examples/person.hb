class Person {
  def init(name="Anonymous Coward") {
    @name = name
  }
  def name {@name}
  def toString {
    "<Person name=" + @name + "@" + self.hash_code.toString + ">"
  }
  def say_hi {
    print('Hi, my name is ' + @name)
  }
}
while(true) {
  input = get_input('name: ')
  p = new Person(input) unless input.empty? else new Person
  print(p)
  p.say_hi
}
