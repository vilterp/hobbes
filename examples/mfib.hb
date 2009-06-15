terms = {}

def getterm(terms,term) {
  if terms.contains_key?(term) {
    return terms[term]
  } else {
    if term == 0 or term == 1 {
      terms[term] = 1
    } else {
      terms[term] = getterm(terms,term-1) + getterm(terms,term-2)
    }
    return terms[term]
  }
}

num_terms = 100
term = 0
until term is num_terms {
  print(getterm(terms,term))
  term = term+1
}
