def fib(term) {
  if term == 0 or term == 1 {
    return 1
  } else {
    return fib(term-1) + fib(term-2)
  }
}

(0 to 19).each(|term|{
  print(fib(term))
})
