/**
 * MineSweeter (a MineSweeper knockoff)
 * @author Andrew Thompson
 * @author Help from Robert Sorensen
 * @version 1.0, 12/06/17
 **/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class MineSweeter extends JFrame
{
	//Gameplay Parameters
	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 760;
	private static final int WIDTH = 760;
	private static final int ROWS = 16;
	private static final int COLS = 16;
	private static final int MINES = 16;
	
	private static int minesLeft = MineSweeter.MINES;
	private static int actualMinesLeft = MineSweeter.MINES;
	private static final String FLAGGED = "@";
	private static final String MINE = "M";
	// visual indication of an exposed MyJButton
	private static final Color expColor = Color.lightGray;
	// colors used when displaying the getStateStr() String
	private static final Color colorMap[] = {Color.lightGray, Color.blue, Color.green, Color.cyan, Color.yellow,
			Color.orange, Color.pink, Color.magenta, Color.red, Color.red};
	private boolean running = true;
	// holds the "number of mines in perimeter" value for each MyJButton
	private int[][] sGrid = new int[ROWS][COLS];
	
	//holds a reference to the jbutton at each location
	//useful in making exposeCell be recursive
	private MyJButton[][] jGrid = new MyJButton[ROWS][COLS];
	
	//init the GUI
	public MineSweeter()
	{
		this.setTitle("MineSweap " +
				MineSweeter.minesLeft +" Mines left");
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setLayout(new GridLayout(ROWS, COLS, 0, 0));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.createContents();
		// place MINES number of mines in sGrid and adjust all of the "mines in perimeter" values
		this.setMines();
		this.setVisible(true);
	}
	
	/**
	*	Creates a grid of buttons inside the GUI
	*/
	public void createContents()
	{
		for (int br = 0; br < ROWS; ++br)
		{
			for (int bc = 0; bc < COLS; ++bc)
			{
				// set sGrid[br][bc] entry to 0 - no mines in it’s perimeter
				sGrid[br][bc] = 0;
				// create a MyJButton that will be at location (br, bc) in the GridLayout
				MyJButton but = new MyJButton("", br, bc);
				jGrid[br][bc] = but;
				// register the event handler with this MyJbutton
				but.addActionListener(new MyListener());
				// add the MyJButton to the GridLayout collection
				this.add(but);
			}
		}
	}
	/**
	*	A nested private class for controlling what each button does.
	*/
	private class MyListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if ( running )
			{
				// used to detrmine if ctrl or alt key was pressed at the time of mouse action
				int mod = event.getModifiers();
				MyJButton jb = (MyJButton)event.getSource();
				// is the MyJbutton that the mouse action occurred in flagged
				boolean flagged = jb.getText().equals(MineSweeter.FLAGGED);
				// is the MyJbutton that the mouse action occurred in already exposed
				boolean exposed = jb.getBackground().equals(expColor);
				// flag a cell : ctrl + left click
				if ( !flagged && !exposed && (mod & ActionEvent.CTRL_MASK) != 0 )
				{
					jb.setText(MineSweeter.FLAGGED);
					--MineSweeter.minesLeft;
					// if the MyJbutton that the mouse action occurred in is a mine
					if ( sGrid[jb.row][jb.col] == 9 )
					{
						--MineSweeter.actualMinesLeft;
						if(MineSweeter.actualMinesLeft == 0) {
							//make victory sound?
							setTitle("MineSweet " + "A WINNER IS YOU");
							//no clicking
							for(MyJButton[] clickOff: jGrid) {
								for(MyJButton noM1 : clickOff) {
									noM1.setEnabled(false);
								}
							}
							return;
							//TODO: maybe wait, then make confetti sprites appear and make happy noise
						}
					}
					setTitle("MineSweet " +
							MineSweeter.minesLeft + " Mines left");
				}
				// un-flag a cell : alt + left click
				else if ( flagged && !exposed && (mod & ActionEvent.ALT_MASK) != 0 )
				{
					jb.setText("");
					++MineSweeter.minesLeft;
					// if the MyJbutton that the mouse action occurred in is a mine
					if ( sGrid[jb.row][jb.col] == 9 )
					{
						++MineSweeter.actualMinesLeft;
					}
					setTitle("MineSweet " +
							MineSweeter.minesLeft +" Mines left");
				}
				// expose a cell : left click
				else if ( !flagged && !exposed)
				{
					exposeCell(jb);
				}
			}
		}
		
		/**
		*	What happens when a user clicks a cell.
		*/
		public void exposeCell(MyJButton jb)
		{
			if ( !running )
				return;
			// expose this MyJButton
			jb.setBackground(expColor);
			jb.setForeground(colorMap[sGrid[jb.row][jb.col]]);
			jb.setText(getStateStr(jb.row, jb.col));
			// if the MyJButton that was just exposed is a mine
			if ( sGrid[jb.row][jb.col] == 9 )
			{
				// what else do you need to adjust?
					//say you lose at top
				actualMinesLeft--;
				setTitle("MineSweet " + MineSweeter.actualMinesLeft  +" <-- this many mines were left. YOU LOSE!");
				// could the game be over?
					//expose all other mines
					for(int i = 0; i < ROWS; i++) {
						for(int j = 0; j < COLS; j++) {
							if(sGrid[i][j] == 9) {
								jGrid[i][j].setBackground(expColor);
								jGrid[i][j].setForeground(colorMap[sGrid[i][j]]);
								jGrid[i][j].setText(getStateStr(i, j));
							}
						}
					}
					//no clicking
					for(MyJButton[] clickOff: jGrid) {
						for(MyJButton noM1 : clickOff) {
							noM1.setEnabled(false);
						}
					}
					//TODO: maybe wait, then make explosion sprites appear and make noise
				return;
			}
			// if the MyJButton that was just exposed has no mines in its perimeter
			if ( sGrid[jb.row][jb.col] == 0 )
			{
				jb.setEnabled(false);
				for(int i = -1; i <= 1; i++) {
					for(int j = -1; j <= 1; j++) {
						if(	//it is not the current square [BOOTLEG EDITION]
							(2*i+j!=0) &&
							//it is not out of bounds
							jb.row+i >= 0 && jb.row+i < ROWS && jb.col+j >= 0 && jb.col+j < COLS &&
							//it is not already revealed
							jGrid[jb.row+i][jb.col+j].isEnabled()
							) {
							this.exposeCell(jGrid[jb.row+i][jb.col+j]);
						}
					}
				}
				return;
			}
		}
	}
	
	/**
	*	The driver that will start the program...
	*/
	public static void main(String[] args)
	{
		new MineSweeter();
	}
	//************************************************************************************************
	// place MINES number of mines in sGrid and adjust all of the "mine's in perimeter" values
	private void setMines()
	{
		int rngX = (int)(Math.random() * ROWS);
		int rngY = (int)(Math.random() * COLS);

		for(int i = 0; i < MINES; i++) {
			while(this.sGrid[rngX][rngY] == 9) {
				rngX = (int)(Math.random() * ROWS);
				rngY = (int)(Math.random() * COLS);
			}
			
			this.sGrid[rngX][rngY] = 9;
			
			//inc surroundings
			mineHelper(rngX-1, rngY+1);
			mineHelper(rngX  , rngY+1);
			mineHelper(rngX+1, rngY+1);
			mineHelper(rngX-1, rngY);
			mineHelper(rngX  , rngY);
			mineHelper(rngX+1, rngY);
			mineHelper(rngX-1, rngY-1);
			mineHelper(rngX  , rngY-1);
			mineHelper(rngX+1, rngY-1);

		}
	}
	
	/**
	*	Checks the values passed in to ensure that the square is inside the grid and also not already a mine
	*	then it is incremented
	*/
	private void mineHelper(int rngX, int rngY) {
		if(rngX >= 0 && rngX < ROWS && rngY >= 0 && rngY < COLS && this.sGrid[rngX][rngY]!=9) {
			this.sGrid[rngX][rngY]++;
		}
	}
	
	/**
	*	Another helper function for what to display in a cell once clicked
	*	@return The appropriate string to show once a cell is clicked
	*/
	private String getStateStr(int row, int col)
	{
		// no mines in this MyJbutton’s perimeter
		if ( this.sGrid[row][col] == 0 )
			return "";
		// 1 to 8 mines in this MyJButton’s perimeter
		else if ( this.sGrid[row][col] > 0 && this.sGrid[row][col] < 9 )
			return "" + this.sGrid[row][col];
		// this MyJButton in a mine
		else
			return MineSweeter.MINE;
	}
}
