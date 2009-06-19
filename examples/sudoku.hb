class Board {
  def init(values) {
    @values = values
  }
  def get_poss(r,c) {
    self.get_col_poss(r,c) - self.get_row_pos(r,c) - self.get_box_poss(r,c)
  }
  def get_col_poss(r,c) {
    poss = (1 to 9).toSet
    for i in 0 to 8 {
      poss.remove(@values[r][i])
    }
    poss
  }
  def get_row_poss(r,c) {
    poss = (1 to 9).toSet
    for i in 0 to 8 {
      poss.remove(@values[i][r])
    }
    poss
  }
  def get_box_poss(r,c) {
    poss = (1 to 9).toSet
    box_top = r - r % 3
    box_left = c - c % 3
    for r in box_top to box_top + 3 {
      for c in box_left to box_left + 3 {
        poss.remove(@values[r][c])
      }
    }
    poss
  }
  def get_next_empty {
    for r in 0 to 8 {
      for c in 0 to 8 {
        return [r,c] unless self.occupied?(r,c)
      }
    }
    nil
  }
  def occupied?(r,c) {
    return @values[r][c].toBool
  }
  def clear(r,c) {
    @values[r][c] = nil
  }
  def solve_cell(r,c) {
    for n in self.get_poss(r,c) {
      @values[r][c] = n
      next_empty = self.get_next_empty
      return true if next_empty is nil
      return true if self.solve_cell(next[0],next[1]) else self.clear(r,c)
    }
    return false
  }
  def solve {
    e = self.get_next_empty
    self.solve_cell(e[0],e[1])
  }
}

input = ",6,,,7,8,,5,,
,3,9,,5,1,,6,4,
,,7,,,,,,8,
,5,,,8,,,,,
,8,1,,9,,3,2,,
,,,,4,,,8,,
3,,,,,,2,,,
6,1,,4,2,,5,7,,
,4,,7,3,,,1,,"
board = []
for row in input.split('\n') {
  board.add(row.split(','))
}
for line in board {
  print(line.toString + ' : ' + line.length)
}
