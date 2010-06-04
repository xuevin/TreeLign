package widgets;

import java.util.ArrayList;
import java.util.Set;

import newickParser.TreeNode;


import treelign.Treelign;

public class MatchingDrawer {
	private Treelign parent;
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private Set<String> setOfAlignmentNames;
	private ArrayList<TreeNode> listOfFailedNodes;
	private ArrayList<ClickedItem> listOfClickedItems;
	private TreeAlignmentDrawer caller;

	public MatchingDrawer(Treelign iParent, TreeAlignmentDrawer iCaller,int iXPosition,int iYPosition,int iWidth, int iHeight){
		parent = iParent;
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
		listOfClickedItems = new ArrayList<ClickedItem>();
		caller = iCaller;
	}
	public void tryToMatch(Set<String> inputSetOfAlignmentNames,ArrayList<TreeNode> inputListOfFailedNodes ){
		setOfAlignmentNames=inputSetOfAlignmentNames; 
		listOfFailedNodes = inputListOfFailedNodes; 
	}
	public void draw(){
		parent.reset();
		parent.fill(255);
		parent.rect(xPosition,yPosition,width,height);
		parent.fill(0);
		if(listOfFailedNodes.size()!=0){
			for(int i =0; i < listOfFailedNodes.size();i++){
				parent.text(listOfFailedNodes.get(i).getLabel(), xPosition,yPosition+((i+1)*parent.getTextHeight()));
			}
			for(int i =0; i < setOfAlignmentNames.size()-1;i++){
				parent.text(setOfAlignmentNames.toArray()[i].toString(),(width/2)+xPosition,yPosition+((i+1)*parent.getTextHeight()));
			}
			for(ClickedItem item : listOfClickedItems){
				parent.rect(item.getXPosition(), item.getYPosition(), item.getWidth(), item.getHeight());
			}
		}else {
			caller.setPendingMatching(false);
		}
		
		
	}
	public void handleClick() {
		draw();
		ClickedItem selected = null;
		int row = (parent.mouseY-yPosition)/parent.getTextHeight();
		if(parent.mouseX<(parent.width/2)){
			if(row<listOfFailedNodes.size()){
				System.out.println(listOfFailedNodes.get(row));
				selected= new ClickedItem((listOfFailedNodes.get(row)),true,xPosition, yPosition+row*parent.getTextHeight(), (int) parent.textWidth(listOfFailedNodes.get(row).getLabel()), parent.getTextHeight());
			}
		}else if(parent.mouseX>((width/2)+xPosition)){
			if(row<setOfAlignmentNames.size()){
				System.out.println(setOfAlignmentNames.toArray()[row].toString());
				selected= new ClickedItem((String) (setOfAlignmentNames.toArray()[row]),false,((width/2)+xPosition), yPosition+row*parent.getTextHeight(), (int) parent.textWidth(setOfAlignmentNames.toArray()[row].toString()), parent.getTextHeight());
			}
		}
		
		
		if(selected!=null){//First Click
			if(listOfClickedItems.isEmpty()){
				listOfClickedItems.add(selected);
				draw();
			}else{
				if((selected.isFailedNode())!=(listOfClickedItems.get(0).isFailedNode())){
					
					listOfClickedItems.add(selected);
					if(selected.isFailedNode()){
						listOfFailedNodes.remove(selected.getNode());
						caller.addtoMatchedHashMap(selected.getNode(),listOfClickedItems.get(0).getName());
					}else{
						listOfFailedNodes.remove(listOfClickedItems.get(0).getNode());
						caller.addtoMatchedHashMap(listOfClickedItems.get(0).getNode(), selected.getName());
					}
					
					
					listOfClickedItems.clear();
					draw();
				}else{	
					if(selected.getName()==listOfClickedItems.get(0).getName()){
						listOfClickedItems.remove(0);
						draw();
					}else{
						
					}
				}
				
			}
		}
	}
}
class ClickedItem{
	private TreeNode node;
	private boolean isFailedNode;
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private String name;
	
	ClickedItem(TreeNode inputTreeNode, boolean inputIsFailedNode, int inputXPosition, int inputYPosition, int inputWidth, int inputHeight){
		isFailedNode=inputIsFailedNode;
		node = inputTreeNode;
		xPosition=inputXPosition;
		yPosition=inputYPosition;
		width = inputWidth;
		height = inputHeight;
		name = node.getLabel();
		
	}
	public ClickedItem(String alignmentName, boolean inputIsFailedNode, int inputXPosition, int inputYPosition, int inputWidth, int inputHeight) {
		isFailedNode=inputIsFailedNode;
		xPosition=inputXPosition;
		yPosition=inputYPosition;
		width = inputWidth;
		height = inputHeight;
		name = alignmentName;
	
	}
	public boolean isFailedNode(){
		return isFailedNode;
	}
	public TreeNode getNode(){
		return node;
	}
	public int getXPosition(){
		return xPosition;
	}
	public int getYPosition(){
		return yPosition;
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public String getName(){
		return name;
	}
}
