#!/usr/bin/hobbes
class Board {
  def init(values) {
    @values = values
  }
  def poss(r,c) {
    (((1 to 9).toSet - self.taken_in_col(c)) - self.taken_in_row(r)) - self.taken_in_box(r,c)
  }
  def taken_in_col(c) {
    taken = new Set
    for row in 0 to 8 {
      taken.add(@values[row][c]) if self.occupied?(row,c)
    }
    taken
  }
  def taken_in_row(r) {
    taken = new Set
    for col in 0 to 8 {
      taken.add(@values[r][col]) if self.occupied?(r,col)
    }
    taken
  }
  def taken_in_box(r,c) {
    taken = new Set
    box_top = r - r % 3
    box_left = c - c % 3
    for r in box_top to box_top + 2 {
      for c in box_left to box_left + 2 {
        taken.add(@values[r][c]) if self.occupied?(r,c)
      }
    }
    taken
  }
  def get_next_empty(startrow,startcol) {
    for r in startrow to 8 {
      col = startcol if r == startrow else 0
      for c in col to 8 {
        if not self.occupied?(r,c) {
          return [r,c]
        }
      }
    }
    nil
  }
  def occupied?(r,c) {
    return @values[r][c].toBool
  }
  def clear(r,c) {
    @values[r][c] = 0
  }
  def solve_cell(r,c) {
    for n in self.poss(r,c) {
      @values[r][c] = n
      next_empty = self.get_next_empty(r,c)
      if next_empty is nil {
        return true
      } elif self.solve_cell(next_empty[0],next_empty[1])  {
        return true
      } else {
        self.clear(r,c)
      }
    }
    false
  }
  def solve {
    e = self.get_next_empty(0,0)
    self.solve_cell(e[0],e[1]) # me want splat args
  }
  def show {
    ans = []
    for r in 0 to 8 {
      for c in 0 to 8 {
        ans.add(@values[r][c].toString)
        ans.add('|') if c == 2 or c == 5
      }  
      ans.add('\n')
      ans.add(('-' * 9) + '\n') if r == 2 or r == 5
    }
    ans.join
  }
}

input = ",6,,,7,8,,5,
,3,9,,5,1,,6,4
,,7,,,,,,8
,5,,,8,,,,
,8,1,,9,,3,2,
,,,,4,,,8,
3,,,,,,2,,
6,1,,4,2,,5,7,
,4,,7,3,,,1,"
values = []
for row in input.split('\n') {
  values.add(row.split(',').map(|x|{
    0 if x.empty? else x.toNumber
  }))
}
board = new Board(values)
print(board) if board.solve else print('not solvable')
