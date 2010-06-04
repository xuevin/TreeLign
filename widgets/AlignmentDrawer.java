package widgets;

import java.util.ArrayList;
import java.util.HashMap;
import clustalWParser.ClustalWParser;
import treelign.Treelign;


public class AlignmentDrawer{
	private Treelign parent;
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	
	/** The hash map holds all of the alignment information gained from clustalw. */
	private HashMap<String,String> hashMap;
	private int textIndex;
	private int endPosition;
	private VerticalTextDrawer verticalTextDrawer;
	
	/** The array that describes which columns should be highlighted */
	private ArrayList<Integer> highlightArray;

	
	public AlignmentDrawer (Treelign iParent,int iXPosition,int iYPosition,int iWidth, int iHeight, String iPathClustalW){
		parent = iParent;
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
		ClustalWParser clustalWParser = new ClustalWParser(iPathClustalW);
		hashMap = clustalWParser.getHashMap();
		textIndex=0;
		endPosition=textIndex+(int)(width/parent.textWidth('a'));
		verticalTextDrawer= new VerticalTextDrawer(parent);
		highlightArray=new ArrayList<Integer>();
	}

	public void adjustSize(int iXPosition, int iYPosition, int iWidth, int iHeight) {
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
		if(textIndex+(int)(width/parent.textWidth('a'))>getAlignmentLength()){
				
		}else{
			endPosition=textIndex+(int)(width/parent.textWidth('a'));
			if(endPosition<0){
				endPosition=0;
			}
		}
	}
	
	/**
	 * Setup clears the screen with a fresh white background
	 */
	public void clearSpace() {
		parent.reset();
		parent.fill(255);
		parent.rect(xPosition,yPosition,width,height);		
	}
	
	/**
	 * Draw alignment.
	 * 
	 * @param iYPosition the y position where you would like this one alignment string to be drawn
	 * @param alignment this is the string that should be drawn
	 * @param color the color of the string for highlighting purposes
	 */
	public void drawAlignment(float iYPosition,String alignment,int color){
		
//		//This is the attempt to retrieve the info from the hashmap
//		String terminalNodeName = taxaName;
//		String alignmentString;
//		alignmentString = hashMap.get(terminalNodeName);
//		
//		//Try different variations
//		if(alignmentString==null){
//			terminalNodeName=terminalNodeName.replaceFirst("-", "");
//			alignmentString=hashMap.get(terminalNodeName);
//			if(alignmentString==null){
//				if(terminalNodeName.contains("_")){
//					terminalNodeName=terminalNodeName.substring(0,terminalNodeName.indexOf("_"));
//				}
//				alignmentString=hashMap.get(terminalNodeName);
//			}
//		}
		String alignmentString= alignment;
			
		//Grey Horizontal Shading
		if(color!=parent.color(0,0,0)){
			if(color!=0){
				parent.fill(0,80);
				parent.noStroke();
				parent.rect(xPosition+1, iYPosition-parent.getTextHeight()+5, (endPosition-textIndex)*parent.textWidth('a'),
						parent.getTextHeight());
				//TODO: This 5 is annoying	
			}
		}
		parent.fill(color);
		parent.text(alignmentString.substring(textIndex,endPosition),xPosition,iYPosition);
	}
	
	/**
	 * Draw markers. (This is the left and right numbers)
	 * 
	 * @param height the height at which the markers will be placed
	 */
	public void drawMarkers(int height){
		//The first initial number marker
		parent.fill(0);
		verticalTextDrawer.text(xPosition, height, ""+(textIndex+1));
		
		//This is the second number marker : The If/Else Prevents the text from going offscreen
		if(xPosition+(endPosition-textIndex-1)*parent.textWidth('a')<xPosition){
			
		}else{
			verticalTextDrawer.text((int) (xPosition+(endPosition-textIndex-1)*parent.textWidth('a')), height, ""+(endPosition));	
		}
	}
	
	/**
	 * Draw the string which has describes whether or not the alignment is consistent.
	 * 
	 * @param position the y position of the line
	 * @param consistencyString the string that holds whether or not the string is consistent
	 */
	public void drawConsistency(int position,String consistencyString){
		parent.text(consistencyString.substring(textIndex, endPosition),xPosition,position);
	}
	
	/**
	 * Gets the index of the first character which is visible to the user. 
	 * 
	 * @return the index of the first character which the user sees
	 */
	public int getAlignmentIndex(){
		return textIndex;
	}

	public HashMap<String,String> getHashMap(){
		return hashMap;
	}
	public void setAlignmentIndex(int newPosition){
		if(newPosition+(int)(width/parent.textWidth('a'))>getAlignmentLength()){
			textIndex=getAlignmentLength()-((int)(width/parent.textWidth('a')));
			endPosition=textIndex+(int)(width/parent.textWidth('a'));
		}else if(newPosition<0){
			textIndex=0;
			endPosition=textIndex+(int)(width/parent.textWidth('a'));
		}else{
			textIndex=newPosition;
			endPosition=textIndex+(int)(width/parent.textWidth('a'));	
		}
	}
	public void increaseAlignmentIndex(){
		setAlignmentIndex(textIndex+1);
	}
	public void decreaseAlignmentIndex(){
		setAlignmentIndex(textIndex-1);
	}
	
	/**
	 * Gets the alignment length.
	 * 
	 * @return the alignment length
	 */
	public int getAlignmentLength(){
		return hashMap.values().iterator().next().length();
	}
	
	/**
	 * Handle click performs an activity if the mouse clicks within the parameters.
	 * Specifically, this one handles adding and removing values into the highlightArray
	 * 
	 * @param mouseX the mouseX position
	 * @param mouseY the mouseY position
	 */
	public void handleClick(int mouseX, int mouseY){
	
		int columnClicked = (int)((mouseX-xPosition)/parent.textWidth('a'))+textIndex;
		if(highlightArray.contains(columnClicked)){
			highlightArray.remove((Integer)columnClicked);
		}else{
			highlightArray.add((Integer)columnClicked);
		}
	
	}
	/**
	 * Highlights the columns based on the top of the alignment information. Should be called before 
	 * any of the alignments are drawn because highlighting goes underneath 
	 * 
	 * @param iYposition the y position at which the rectangle begins
	 */
	public void highlightColumns(int iYposition){
		parent.fill(100);
		for(int columnNumber: highlightArray){
			if((columnNumber-textIndex)>=0){
				parent.rect(xPosition+parent.textWidth('a')*(columnNumber-textIndex), iYposition ,parent.textWidth('a'), height-iYposition);	
			}
		}
	}
	public int getNumberOfDisplayedCharacters(){
		return endPosition-textIndex;
	}

}