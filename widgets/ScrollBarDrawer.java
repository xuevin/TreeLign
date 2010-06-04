package widgets;

import treelign.Treelign;

public class ScrollBarDrawer {
	private Treelign parent;
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private int lengthOfAlignment;
	private int textIndex;
	private int displayedLength;
	private int sliderXPosition;
	private int sliderWidth;
	private boolean locked;
	
	public ScrollBarDrawer(Treelign iParent,int iXPosition,int iYPosition,int iWidth, int iHeight,
			int iLengthOfAlignment, int iTextIndex, int iDisplayLength){
		parent = iParent;
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
		lengthOfAlignment = iLengthOfAlignment;
		textIndex = iTextIndex;
		displayedLength = iDisplayLength;
		recaulateSlider();
		
	}
	public void updateValues(int iLengthOfAlignment, int iTextIndex, int iDisplayLength){
		lengthOfAlignment = iLengthOfAlignment;
		textIndex = iTextIndex;
		displayedLength = iDisplayLength;
	}
	public void draw(){
		parent.reset();
		parent.fill(255);
		parent.rect(xPosition,yPosition,width,height);
		//Slider
		parent.fill(0);
		recaulateSlider();
		parent.rect(sliderXPosition, yPosition, sliderWidth, height);
	}
	
	/**
	 * Handle click performs an activity if the mouse clicks within the parameters.
	 * 
	 * @param mouseX the mouseX position
	 * @param mouseY the mouseY position
	 */
	public void handleClick(){
		
		
		//If the click is in the slider, dont do anything
		if(parent.mouseX>sliderXPosition && parent.mouseX < sliderXPosition+sliderWidth){
			System.out.println("Inside");
		}else if (parent.mouseX<xPosition+sliderWidth){
			System.out.println("LEFT Side");
			textIndex=0;
		}else if (parent.mouseX>((xPosition+width)-sliderWidth)){
			System.out.println("Right Side");
			textIndex=lengthOfAlignment-1;
		}else{
			System.out.println("Somewhere Else");
			textIndex=(int) ((((float)parent.mouseX-xPosition)/width)*lengthOfAlignment) - displayedLength/2;
		}
		
	}
	public int getTextIndex(){
		return textIndex;
	}
	public void adjustSize(int iXPosition, int iYPosition, int iWidth, int iHeight) {
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
	}
	public void handleDrag() {
		float ratio=(float)(parent.mouseX-xPosition)/(width);
		if(ratio<0){
			ratio=0;
		}
		
	    if(parent.mousePressed && over()) {
	    	locked = true;
	    }else if(parent.mousePressed && 
	    		parent.mouseX > xPosition && 
	    		parent.mouseX < xPosition+width &&
	    	    parent.mouseY > yPosition && 
	    	    parent.mouseY < yPosition+height){
	    	
	    	textIndex=(int) (lengthOfAlignment*ratio)-displayedLength/2;
	    }
	
	    if(locked){
	    	
			System.out.println(parent.mouseX);
			System.out.println(parent.pmouseX);

			//int dif = (parent.mouseX-parent.pmouseX);
			//textIndex+=dif;
			textIndex=(int) (lengthOfAlignment*ratio)-displayedLength/2;
		}

		
	}
	private void recaulateSlider() {
		sliderXPosition=(int) ((((float)textIndex/lengthOfAlignment)*width ) +xPosition);
		sliderWidth=(int) (((float)displayedLength/lengthOfAlignment)*width);
		
	}
	private boolean over() {
		if(parent.mouseX > sliderXPosition && parent.mouseX < sliderXPosition+sliderWidth &&
	    parent.mouseY > yPosition && parent.mouseY < yPosition+height) {
			return true;
	    } else {
	    	return false;
	    }
	}
	public void handleMouseReleased() {
		locked=false;
		
	}

	
	

}
