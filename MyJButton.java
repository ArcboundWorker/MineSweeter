import javax.swing.*;
public class MyJButton extends JButton
{
	private static final long serialVersionUID = 2L;
	public final int row;
	public final int col;
	public MyJButton(String text, int row, int col)
	{
		super(text);
		this.row = row;
		this.col = col;
	}
}