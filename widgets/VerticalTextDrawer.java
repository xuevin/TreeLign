package widgets;

import treelign.Treelign;


public class VerticalTextDrawer {
	private Treelign parent;
	private int textHeight;
	
	/**
	 * Instantiates a new vertical text drawer.
	 * 
	 * @param parentPApplet the parent PApplet
	 */
	public VerticalTextDrawer(Treelign parentPApplet){
		parent = parentPApplet;
		textHeight=parentPApplet.getTextHeight();
	}
	
	/**
	 * Draws Vertical Text given the lower left hand corner. Text never goes greater than the x position.
	 * 
	 * @param positionX the X Position of the lower left corner
	 * @param positionY the Y Position of the lower left corner
	 * @param text the text that should be displayed
	 */
	public void text(int positionX,int positionY, String text){
		parent.reset();
		for(int i =0; i<text.length();i++){
			parent.text(text.charAt(text.length()-1-i),positionX,(positionY-(i*textHeight)));
			
		}
	}

}
