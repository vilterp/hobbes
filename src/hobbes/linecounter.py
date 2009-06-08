import sys, os

def countdir(path):
  total = 0
  for entry in os.listdir(path):
    if not entry.startswith('.'):
      if os.path.isdir(entry):
        total += countdir(path + '/' + entry)
      else:
        total += countfile(path + '/' + entry)
  print path, ':', total
  return total

def countfile(path):
  lines = 0
  for line in open(path):
    if line.strip() != '':
      lines += 1
  print path, ':', lines
  return lines

if __name__ == '__main__':
  print(countdir('.'))
