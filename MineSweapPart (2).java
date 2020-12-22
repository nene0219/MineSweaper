import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MineSweapPart extends JFrame {
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
	private static final Color CELL_EXPOSED_FOREGROUND_COLOR_MAP[] = { Color.lightGray, Color.blue, Color.green,
			Color.cyan, Color.yellow, Color.orange, Color.pink, Color.magenta, Color.red, Color.red };

	private boolean running = true;
	// holds the "number of mines in perimeter" value for each MyJButton
	private int[][] mineGrid = new int[MINE_GRID_ROWS][MINE_GRID_COLS];

	public MineSweapPart() {
		this.setTitle("MineSweap                                                         "
				+ MineSweapPart.guessedMinesLeft + " Mines left");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setResizable(false);
		this.setLayout(new GridLayout(MINE_GRID_ROWS, MINE_GRID_COLS, 0, 0));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.createContents();
		// place MINES number of mines in mineGrid and adjust all of the "mines in
		// perimeter" values
		this.setMines();
		this.setVisible(true);
	}

	public void createContents() {
		for (int gr = 0; gr < MINE_GRID_ROWS; ++gr) {
			for (int gc = 0; gc < MINE_GRID_COLS; ++gc) {
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

	// place MINES number of mines in mineGrid and adjust all of the "mines in
	// perimeter" values
	private void setMines() {
		// your code here ...
		int mCnt = 0;
		while (mCnt < MineSweapPart.TOTAL_MINES) {
			int r = (int) (Math.random() * MineSweapPart.MINE_GRID_ROWS);
			int c = (int) (Math.random() * MineSweapPart.MINE_GRID_COLS);
  			if (this.mineGrid[r][c] != 9) {
				this.mineGrid[r][c] = 9;

				for (int gr = -1; gr < 2; ++gr)
					for (int gc = -1; gc < 2; ++gc)
						if (!(gr == 0 && gc == 0) && (r + gr >= 0 && r + gr < MineSweapPart.MINE_GRID_ROWS)
								&& (c + gc >= 0 && c + gc < MineSweapPart.MINE_GRID_COLS))
							if (this.mineGrid[r + gr][c + gc] != 9)
								++this.mineGrid[r + gr][c + gc];
				++mCnt;

			}
		}
	}

	private String getGridValueStr(int row, int col) {
		// no mines in this MyJbutton's perimeter
		if (this.mineGrid[row][col] == NO_MINES_IN_PERIMETER_GRID_VALUE)
			return "";
		// 1 to 8 mines in this MyJButton's perimeter
		else if (this.mineGrid[row][col] > NO_MINES_IN_PERIMETER_GRID_VALUE
				&& this.mineGrid[row][col] <= ALL_MINES_IN_PERIMETER_GRID_VALUE)
			return "" + this.mineGrid[row][col];
		// this MyJButton in a mine
		else // this.mineGrid[row][col] = IS_A_MINE_IN_GRID_VALUE
			return MineSweapPart.EXPOSED_MINE_SYMBOL;
	}

	// nested private class
	private class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (running) {
				// used to determine if ctrl or alt key was pressed at the time of mouse action
				int mod = event.getModifiers();
				MyJButton mjb = (MyJButton) event.getSource();
				// is the MyJbutton that the mouse action occurred in flagged
				boolean flagged = mjb.getText().equals(MineSweapPart.UNEXPOSED_FLAGGED_MINE_SYMBOL);
				// is the MyJbutton that the mouse action occurred in already exposed
				boolean exposed = mjb.getBackground().equals(CELL_EXPOSED_BACKGROUND_COLOR);
				// flag a cell : ctrl + left click
				if (!flagged && !exposed && (mod & ActionEvent.CTRL_MASK) != 0) {
					mjb.setText(MineSweapPart.UNEXPOSED_FLAGGED_MINE_SYMBOL);
					--MineSweapPart.guessedMinesLeft;
					// if the MyJbutton that the mouse action occurred in is a mine
					if (mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE) {
						// what else do you need to adjust?
						// could the game be over?
						MineSweapPart.actualMinesLeft--;
						if (MineSweapPart.actualMinesLeft==0) {
			        		running=false;
			        		setTitle("Mine Field Cleared");
			        		return;
				        }
					}
					setTitle("MineSweap                                                         "
							+ MineSweapPart.guessedMinesLeft + " Mines left");
				}
				// unflag a cell : alt + left click
				else if (flagged && !exposed && (mod & ActionEvent.ALT_MASK) != 0) {
					mjb.setText("");
					++MineSweapPart.guessedMinesLeft;
					// if the MyJbutton that the mouse action occurred in is a mine
					if (mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE) {
						// what else do you need to adjust?
						// could the game be over?
					}
					setTitle("MineSweap                                                         "
							+ MineSweapPart.guessedMinesLeft + " Mines left");
				}
				// expose a cell : left click
				else if (!flagged && !exposed) {
					exposeCell(mjb);
				}
			}
		}

		public void exposeCell(MyJButton mjb) {
			if (!running)
				return;

			// expose this MyJButton
			mjb.setBackground(CELL_EXPOSED_BACKGROUND_COLOR);
			mjb.setForeground(CELL_EXPOSED_FOREGROUND_COLOR_MAP[mineGrid[mjb.ROW][mjb.COL]]);
			mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));

			// if the MyJButton that was just exposed is a mine
			if (mineGrid[mjb.ROW][mjb.COL] == IS_A_MINE_IN_GRID_VALUE) {
				// what else do you need to adjust?
				// could the game be over?
				running=false;
				setTitle("MineSweap YOU LOST!");
				for(int i = 0; i < MINE_GRID_ROWS * MINE_GRID_COLS; i++) {
					MyJButton jb = (MyJButton)mjb.getParent().getComponent(i);
					if(mineGrid[i / MINE_GRID_ROWS][i % MINE_GRID_COLS] == IS_A_MINE_IN_GRID_VALUE) {
						jb.setForeground(Color.RED);
						jb.setText(MineSweapPart.EXPOSED_MINE_SYMBOL);
					}
				}
				return;
			}

			// if the MyJButton that was just exposed has no mines in its perimeter
			if (mineGrid[mjb.ROW][mjb.COL] == NO_MINES_IN_PERIMETER_GRID_VALUE) {
				for(int row = mjb.ROW-1; row <= mjb.ROW+1; row++) {
					if(row < 0 || row >= MINE_GRID_ROWS)
					    continue;
					for(int col = mjb.COL-1; col <= mjb.COL+1; col++) {
						if(col < 0 || col >= MINE_GRID_COLS)
						      continue;
						
						String value = getGridValueStr(row, col);
						if(mineGrid[row][col] == IS_A_MINE_IN_GRID_VALUE || !value.isEmpty() && !"123456789".contains(getGridValueStr(row, col))) {
						    continue;
						} else {
							getGridValueStr(row,col);
							MyJButton jbn = (MyJButton) mjb.getParent().getComponent(row * MINE_GRID_COLS + col);
							boolean flagged = jbn.getText().equals(MineSweapPart.UNEXPOSED_FLAGGED_MINE_SYMBOL);
							// is the MyJbutton that the mouse action occurred in already exposed
							boolean exposed = jbn.getBackground().equals(CELL_EXPOSED_BACKGROUND_COLOR);
							if(!flagged && !exposed) {
								exposeCell(jbn);
							}
						}
					}
				}
			}
		}
	}
	// nested private class

	public static void main(String[] args) {
		new MineSweapPart();
	}

}
