# Numberlink solver (Flow free)  
![image](https://user-images.githubusercontent.com/41891935/105691720-2fcd8880-5f30-11eb-8f83-3cc95d9b41f9.png)  

Game: Numberlink by Nikoli  
Solver: SAT solver  
Lib: SAT4J  
Database: Flow free game  
  
I) About Numberlink by Nikoli:  
http://www.nikoli.co.jp/en/puzzles/numberlink.html  
  
II) SAT Converter:  

*Input file:  
first line contains 2 numbers M and N. M : number of rows, N : number of collumns.  
second line contains 1 number which is the largest number.  
next lines contain numberlink game size MxN.  

sample input file:  
5 5  
4  
0 0 0 3 0  
0 4 0 1 0  
0 0 2 0 0  
0 4 0 3 2  
0 1 0 0 0  
  

*Variables: 

          Xij,k for each cell  
         i : row, j : collumn, k : direction (LEFT = 1, RIGHT = 2, UP = 3, DOWN = 4)  
         M : number of rows, N : number of collumns  
         => number of X is MxNx4  
            
          Yij,v  
         v : value (cells are connected have the same value)  
         
***1) Rule for numbering cells:***   

  **Numbering cells have 1 and only 1 direction**  
  
  +) Have at least 1 direction: Xij,1 v Xij,2 v Xij,3 v Xij,4  
    > A num-cell has 1 of 4 direction  
  +) Have exact 1 direction: (Xij,1 -> -Xij,2) ^ (Xij,1 -> -Xij,3) ^ (Xij,1 -> -Xij,4) ^...  
    > When a direction happens, other directions are disabled  
    
  **Same number are connected**  
  
  +) Reflex:  
    for instance: When a cell directs to its left, the cell at the left direct to its right.  
    => Xij,1 -> Xi(j-1),2  
  +) Spreading:  
    When a cell directs to another cell, they have the same value  
    for instance: A cell which has value 7 and directs to its left, the left cell has the same value 7  
    => (Yij,7 ^ Xij,1) -> Yi(j-1),7  
    For the same reason we have:  
    When a cell doesn't have value 8, other connected cell don't have value 8  
    => (-Yij,8 ^ Xij,1) -> -Yi(j-1),8  
      
  **Connect to a num-cell**  
  
***2) Rule for blank cells:***  

  **Blank cells have 2 directions and exact 2 directions**  
  
  +) Have at least 2 direction: Xij,1 -> (Xij,2 v Xij,3 v Xij,4)  
    > A blank cell has 2 of 4 directions  
  +) Have exact 2 direction: -Xij,1 -> (-Xij,2 v -Xij,3 v -Xij,4)  
    > When 2 directions happen, other directions are disabled  
      
  **Cells have the same value are connected:**  
     
   Similar to "Same number are connected" rule for num-cell  
   
  **Limit boundary:**  
    
  The directions of cells at the edge to the outside are disabled  
