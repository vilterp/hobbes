def fib(term) {
  if term is 0 or term is 1 {
    return 1
  } else {
    return fib(term-1) + fib(term-2)
  }
}

term = 0
while term is not 10 {
  print(fib(term))
  term = term + 1
}
