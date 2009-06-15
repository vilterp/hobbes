terms = {}

def getterm(terms,term) {
  if terms.has_key?(term) {
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

(0 to 100).each(|term|{
  print(getterm(terms,term))
})
