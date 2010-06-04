package widgets;

import treelign.Treelign;

public class RectButtonDrawer {
	private Treelign parent;
	private int xCoordinate;
	private int yCoordinate;
	private int width;
	private int height;
	
	public RectButtonDrawer(Treelign iParent,int iXCoordinate, int iYCoordinate, int iWidth, int iHeight){
		xCoordinate=iXCoordinate;
		yCoordinate=iYCoordinate;
		width=iWidth;
		height=iHeight;
		parent = iParent;
	}
	public void draw(){
		parent.rect(xCoordinate,yCoordinate,width,height);
	}
	public void setNewPosition(int iXCoordinate, int iYCoordinate){
		xCoordinate=iXCoordinate;
		yCoordinate=iYCoordinate;
	}
	public int getXCoordinate(){
		return xCoordinate;
	}
	public int getYCoordinate(){
		return yCoordinate;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public boolean mouseIsInTheZone(){
		if(	parent.mouseX<(xCoordinate+width) &&//Right Side 
			parent.mouseX>(xCoordinate) &&//Left Side
		 	parent.mouseY>yCoordinate &&//Top Side
		 	parent.mouseY<yCoordinate+height){//Bottom Side)
			return true;
		}else{
			return false;
		}
		
	}

}