import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random; 

public class MineSweapPart extends JFrame
{
  private static final long serialVersionUID = 1L;
  private static final int WINDOW_HEIGHT = 760;
  private static final int WINDOW_WIDTH = 760;
  private static final int MINE_GRID_ROWS = 16;
  private static final int MINE_GRID_COLS = 16;
  private static final int TOTAL_MINES = 16;
  private static final int NO_MINES_IN_PERIMETER_GRID_VALUE = 0;
  private static final int ALL_MINES_IN_PERIMETER_GRID_VALUE = 8;
  private static final int IS_A_MINE_IN_GRID_VALUE = 9;
  
  private static int guessedMinesLeft = TOTAL_MINES;
  private static int actualMinesLeft = TOTAL_MINES;

  private static final String UNEXPOSED_FLAGGED_MINE_SYMBOL = "@";
  private static final String EXPOSED_MINE_SYMBOL = "M";
  
  // visual indication of an exposed MyJButton
  private static final Color CELL_EXPOSED_BACKGROUND_COLOR = Color.lightGray;
  // colors used when displaying the getStateStr() String
  private static final Color CELL_EXPOSED_FOREGROUND_COLOR_MAP[] = {Color.lightGray, Color.blue, Color.green, Color.cyan, Color.yellow, 
                                           Color.orange, Color.pink, Color.magenta, Color.red, Color.red};

  private boolean running = true;
  // holds the "number of mines in perimeter" value for each MyJButton 
  private int[][] mineGrid = new int[MINE_GRID_ROWS][MINE_GRID_COLS];

  
  public MineSweapPart()
  {
    this.setTitle("MineSweap                                                         " + 
                  MineSweapPart.guessedMinesLeft +" Mines left");
    this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    this.setResizable(false);
    this.setLayout(new GridLayout(MINE_GRID_ROWS, MINE_GRID_COLS, 0, 0));
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    this.createContents();
    // place MINES number of mines in mineGrid and adjust all of the "mines in perimeter" values
    this.setMines();
    this.setVisible(true);
  }

  public void createContents()
  {
    for (int gr = 0; gr < MINE_GRID_ROWS; ++gr)
    {  
      for (int gc = 0; gc < MINE_GRID_COLS; ++gc)
      {  
        // set sGrid[gr][gc] entry to 0 - no mines in it's perimeter
        this.mineGrid[gr][gc] = 0; 
        // create a MyJButton that will be at location (br, bc) in the GridLayout
        MyJButton but = new MyJButton("", gr, gc); 
        // register the event handler with this MyJbutton
        but.addActionListener(new MyListener());
        // add the MyJButton to the GridLayout collection
        this.add(but);
      }  
    }
  }
  
  // place MINES number of mines in mineGrid and adjust all of the "mines in perimeter" values
  private void setMines()
  {
      Random rand = new Random(); 
    for ( int i = 1; i <= TOTAL_MINES; i++ )
        {
            int randomrow = rand.nextInt( MINE_GRID_ROWS -1)+1;
            int randomcol = rand.nextInt( MINE_GRID_COLS -1)+1;
            
            // If square is already mined, do it again:
            while ( this.mineGrid[randomrow][randomcol] == IS_A_MINE_IN_GRID_VALUE )
            {
                randomrow = rand.nextInt( MINE_GRID_ROWS -1)+1;
                randomcol = rand.nextInt( MINE_GRID_COLS -1)+1;
            }
            
            this.mineGrid[randomrow][randomcol]=IS_A_MINE_IN_GRID_VALUE ;
        }
    for( int i=1; i<MINE_GRID_ROWS; i++ )
    {
        for( int j=1;j<MINE_GRID_COLS; j++ )
        {
            if (this.mineGrid[i][j] != IS_A_MINE_IN_GRID_VALUE)
            {
                int count=0;
                if (i+1<MINE_GRID_ROWS)
                {
                    if (this.mineGrid[i+1][j]== IS_A_MINE_IN_GRID_VALUE )
                        count++;
                    if (j-1!=0 && this.mineGrid[i+1][j-1]== IS_A_MINE_IN_GRID_VALUE )
                        count++;
                    if (j+1<MINE_GRID_COLS)
                    {
                        if (this.mineGrid[i+1][j+1]== IS_A_MINE_IN_GRID_VALUE )
                            count++;
                    }
                }
                if (j+1<MINE_GRID_COLS)
                {
                    if (this.mineGrid[i][j+1]== IS_A_MINE_IN_GRID_VALUE )
                        count++;
                    if (i-1!=0 && this.mineGrid[i-1][j+1]== IS_A_MINE_IN_GRID_VALUE )
                        count++;
                }


                if (i-1!=0 && j-1!=0 && this.mineGrid[i-1][j-1]== IS_A_MINE_IN_GRID_VALUE )
                    count++;
                if (i-1!=0 &&  this.mineGrid[i-1][j]== IS_A_MINE_IN_GRID_VALUE )
                    count++;

                if (j-1!=0 &&  this.mineGrid[i][j-1]== IS_A_MINE_IN_GRID_VALUE )
                    count++;
                this.mineGrid[i][j]=count;
            }
        }
    }
  }
  
  private String getGridValueStr(int row, int col)
  {
    // no mines in this MyJbutton's perimeter
    if ( this.mineGrid[row][col] == NO_MINES_IN_PERIMETER_GRID_VALUE )
      return "";
    // 1 to 8 mines in this MyJButton's perimeter
    else if ( this.mineGrid[row][col] > NO_MINES_IN_PERIMETER_GRID_VALUE && 
              this.mineGrid[row][col] <= ALL_MINES_IN_PERIMETER_GRID_VALUE )
      return "" + this.mineGrid[row][col];
    // this MyJButton in a mine
    else // this.mineGrid[row][col] = IS_A_MINE_IN_GRID_VALUE
      return MineSweapPart.EXPOSED_MINE_SYMBOL;
  }



  // nested private class
  private class MyListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent event)
    {
      if ( running )
      {
        // used to determine if ctrl or alt key was pressed at the time of mouse action
        int mod = event.getModifiers();
        MyJButton mjb = (MyJButton)event.getSource();
        // is the MyJbutton that the mouse action occurred in flagged
        boolean flagged = mjb.getText().equals(MineSweapPart.UNEXPOSED_FLAGGED_MINE_SYMBOL);
        // is the MyJbutton that the mouse action occurred in already exposed
        boolean exposed = mjb.getBackground().equals(CELL_EXPOSED_BACKGROUND_COLOR);
        // flag a cell : ctrl + left click
        if ( !flagged && !exposed && (mod & ActionEvent.CTRL_MASK) != 0 )
        {
          mjb.setText(MineSweapPart.UNEXPOSED_FLAGGED_MINE_SYMBOL);
          --MineSweapPart.guessedMinesLeft;
          // if the MyJbutton that the mouse action occurred in is a mine
          if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE )
          {
            // what else do you need to adjust?
            // could the game be over?
          }
          setTitle("MineSweap                                                         " + 
                   MineSweapPart.guessedMinesLeft +" Mines left");
        }
        // unflag a cell : alt + left click
        else if ( flagged && !exposed && (mod & ActionEvent.ALT_MASK) != 0 )
        {
          mjb.setText("");
          ++MineSweapPart.guessedMinesLeft;
          // if the MyJbutton that the mouse action occurred in is a mine
          if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE )
          {
            // what else do you need to adjust?
            // could the game be over?
          }
          setTitle("MineSweap                                                         " + 
                    MineSweapPart.guessedMinesLeft +" Mines left");
        }
        // expose a cell : left click
        else if ( !flagged && !exposed )
        {
          exposeCell(mjb);
        }  
      }
    }
    
    public void clearNoMines(MyJButton mjb)
    {
        if(!mjb.getBackground().equals(CELL_EXPOSED_BACKGROUND_COLOR) && mineGrid[mjb.ROW][mjb.COL] == NO_MINES_IN_PERIMETER_GRID_VALUE)
        {
            mjb.setBackground(CELL_EXPOSED_BACKGROUND_COLOR);
            mjb.setForeground(CELL_EXPOSED_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
            mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));
            for (int p=-1;p<=1;p++) {
            for (int q=-1;q<=1;q++) {
                MyJButton jbn = (MyJButton)mjb.getParent().getComponent((mjb.ROW+p)*MINE_GRID_COLS+mjb.COL+q);
                clearNoMines(jbn);
                }
            }
        }
    }
    
    public void exposeCell(MyJButton mjb)
    {
      if ( !running )
        return;
      
      // expose this MyJButton 
      mjb.setBackground(CELL_EXPOSED_BACKGROUND_COLOR);
      mjb.setForeground(CELL_EXPOSED_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
      mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));
      
      // if the MyJButton that was just exposed is a mine
      if ( mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE )
      {  
        // what else do you need to adjust?
        // could the game be over?
        return;
      }
      
      // if the MyJButton that was just exposed has no mines in its perimeter
      if ( mineGrid[mjb.ROW][mjb.COL] == NO_MINES_IN_PERIMETER_GRID_VALUE )
      {
        clearNoMines(mjb);
      }
    }
  }
  // nested private class


  public static void main(String[] args)
  {
    new MineSweapPart();
  }

}
