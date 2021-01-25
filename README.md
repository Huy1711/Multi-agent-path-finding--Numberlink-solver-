# Numberlink solver (Flow free)  
Game: Numberlink by Nikoli  
Solver: SAT solver  
Lib: SAT4J  
Database: Flow free game  
  
I) About Numberlink by Nikoli:  
http://www.nikoli.co.jp/en/puzzles/numberlink.html  
  
II) SAT Converter:  
Variables: 

          Xij,k for each cell  
         i : row, j : collumn, k : direction (LEFT = 1, RIGHT = 2, UP = 3, DOWN = 4)  
         M : number of rows, N : number of collumns  
         => number of X is MxNx4  
            
          Yij,v  
         v : value (cells are connected have the same value)  
         
***1) Rule for numbering cells:***   

  /t**Numbering cells have 1 and only 1 direction**  
  
  +) Have at least 1 direction:  
    xij,1 v xij,2 v xij,3 v xij,4  
    > A num-cell have 1 of 4 direction  
  +) Have exact 1 direction: 
    (xij,1 -> -xij,2) ^ (xij,1 -> -xij,3) ^ (xij,1 -> -xij,4) ^...  
    > When a direction happens, other directions are disabled  
    
  /t**Same number are connected**  
  
  +) Reflex:  
    for instance: When a cell direct to its left, the cell at the left direct to its right.  
    => Xij,1 -> Xi(j-1),2  
  +) Spreading:  
    When a cell direct to another cell, they have the same value  
    for instance: A cell which has value 7 and directs to its left, the left cell has the same value 7  
    => (Yij,7 ^ Xij,1) -> Yi(j-1),7  
    For the same reason we have:  
    When a cell doesn't have value 8, other connected cell don't have value 8  
    => (-Yij,8 ^ Xij,1) -> -Yi(j-1),8  
      
  /t**Connect to a num-cell**  
  
***2) Rule for blank cells:***  

  /t**Blank cells have 2 directions and exact 2 directions**  
  
  +) Have at least 2 direction:  
    xij,1 -> (xij,2 v xij,3 v xij,4)  
    > A blank cell has 2 of 4 directions  
  +) Have exact 2 direction: 
    -xij,1 -> (-xij,2 v -xij,3 v -xij,4)  
    > When 2 directions happen, other directions are disabled  
      
  /t**Cells have the same value are connected:**  
     
   Similar to "Same number are connected" rule for num-cell  
   
  /t**Limit boundary:**  
    
  The directions of cells at the edge to the outside are disabled  
