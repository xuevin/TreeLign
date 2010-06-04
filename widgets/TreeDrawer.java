package widgets;

import java.util.ArrayList;

import newickParser.NewickParser;
import newickParser.TreeNode;

import treelign.Treelign;


public class TreeDrawer{
	private Treelign parent;
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private TreeNode rootNode;
	private boolean isPhylogram;
	private int yPositionTreeNode;
	private ArrayList<TreeNode> arrayListOfTerminalNodes;
	private final int DISTANCEFROMLEFTWALL;
	private int textHeight;
	private ArrayList<TreeNode> clickableNodes;
	private ArrayList<TreeNode> arrayOfActiveClickedNodes;
	
	public TreeDrawer (Treelign iParent,int iXPosition,int iYPosition,int iWidth, int iHeight, String pathNewick){
		parent = iParent;
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
		NewickParser newickParser= new NewickParser(pathNewick);
		rootNode = newickParser.getTreeRoot();
		arrayListOfTerminalNodes = getArrayListOfTerminalNodes();
		isPhylogram=true;
		yPositionTreeNode=yPosition+height;
		DISTANCEFROMLEFTWALL=10;
		textHeight = parent.getTextHeight();
		clickableNodes = new ArrayList<TreeNode>();
		arrayOfActiveClickedNodes = new ArrayList<TreeNode>();
	}
	public void adjustSize(int iXPosition, int iYPosition, int iWidth, int iHeight) {
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;	
	}
	public void draw() {
		parent.reset();
		parent.fill(255);
		parent.rect(xPosition,yPosition,width,height);
		drawTree();
		colorinBox();
	}
	
	/**
	 * Gets the array list of terminal nodes. If it is null, then fill it in by running recursive fill.
	 * 
	 * @return the array list of terminal nodes
	 */
	public ArrayList<TreeNode> getArrayListOfTerminalNodes(){
		if (arrayListOfTerminalNodes==null){
			arrayListOfTerminalNodes = new ArrayList<TreeNode>();
			System.out.println("Fill in Array List of Terminal Nodes Only Once");
			recursiveFillInArrayListOfTerminalNodes(rootNode);
		}
		
		return arrayListOfTerminalNodes;
	}
	/**
	 * Recursively goes through the nodes to fill in the list of terminal nodes.
	 * 
	 * @param node this is the Root Node
	 */
	private void recursiveFillInArrayListOfTerminalNodes(TreeNode node){
		if(!node.isTerminal()){ // If not a terminal node
			for(int x=0;x<node.getArrayListOfChildren().size();x++){
				recursiveFillInArrayListOfTerminalNodes(node.getArrayListOfChildren().get(x));
			}
		}else{
			arrayListOfTerminalNodes.add(node);
		}
	}
	
	
	public void switchMode(){
		isPhylogram=!isPhylogram;
	}
	private void drawTree(){
		
		parent.fill(255);
		parent.rect(xPosition, yPosition, width, height);
		yPositionTreeNode=yPosition+height;
		parent.fill(0);
		
		//this array clears because the positions of the nodes change. 
		clickableNodes.clear();
		//Phylogram or Cladogram
		if(!isPhylogram){//Cladogram
			cladogram(rootNode);
			
		}else{//Phylogram
			phylogram(rootNode);	
		}
		
	}
	//	Draws a phylogram
	private void phylogram(TreeNode inputTree){
		/*
		 * The algorithm behind this....
		 * 
		 * First list all of the terminal nodes and assign them a y position
		 * Then it draws the line and the label
		 * Then as it ends its loop, it comes up and draws the line of the parent, using the yPositionTreeNodes it just received		 * 
		 */
		inputTree.setAbsoluteXPosition(inputTree.getUnscaledXPosition()*width+DISTANCEFROMLEFTWALL);
		inputTree.setXStartPosition((inputTree.getUnscaledXPosition()-inputTree.getLength())*width+DISTANCEFROMLEFTWALL);
		parent.fill(inputTree.getColor());
		
		
		if(!inputTree.isTerminal()){ // If not a terminal node
			for(int x=0;x<inputTree.getArrayListOfChildren().size();x++){
				phylogram(inputTree.getArrayListOfChildren().get(x));
			}
			parent.line(inputTree.getXStartPosition(),inputTree.getAbsoluteYPosition()-textHeight/2,inputTree.getAbsouluteXPosition(),inputTree.getAbsoluteYPosition()-textHeight/2);
			
			/*
			 * Draws boxes at the internal nodes
			 * 
			 * parent.rectMode(PConstants.CENTER);
			 * parent.rect(inputTree.getXPosition(), inputTree.getYPosition()-textHeight/2, 10, 10);
			 */
			
			
			//verticalLines
			//Draw a line from the first node to the last node
			float first = inputTree.getArrayListOfChildren().get(0).getAbsoluteYPosition();
			float last = inputTree.getArrayListOfChildren().get(inputTree.getArrayListOfChildren().size()-1).getAbsoluteYPosition();
			parent.line(inputTree.getAbsouluteXPosition(),last-textHeight/2,inputTree.getAbsouluteXPosition(),first-textHeight/2);	
		}else{
			//Sets position so that each terminal node is equally spaced
			yPositionTreeNode=yPositionTreeNode-textHeight;
			inputTree.setAbsoluteYPosition(yPositionTreeNode);
			//The terminal node text
			parent.text(inputTree.getLabel().replace('_', ' '),inputTree.getAbsouluteXPosition(),inputTree.getAbsoluteYPosition());	
			
			//This Creates the length which extends from the node
			parent.line(inputTree.getXStartPosition(),inputTree.getAbsoluteYPosition()-textHeight/2,inputTree.getAbsouluteXPosition(),inputTree.getAbsoluteYPosition()-textHeight/2);
			parent.noFill();
			//parent.stroke(inputTree.getColor());
			//Makes a box around the terminal node
			//parent.rect(endScaledNode, inputTree.getYPosition()-textHeight/2, parent.textWidth(inputTree.getLabel()), textHeight);
			parent.stroke(0);
		}
	}
	//Draws a cladogram
	private void cladogram(TreeNode inputTree){
		inputTree.setAbsoluteXPosition(width+5-(inputTree.getHowFarFromTerminalNode()*(width/11)));
		
		//Sets the X start position of the node
		if(inputTree.getParentNode()!=null){
			inputTree.setXStartPosition(width+5-(inputTree.getParentNode().getHowFarFromTerminalNode())*(width/11));	
		}else{
			inputTree.setXStartPosition(width+5-(inputTree.getHowFarFromTerminalNode())*(width/11));
		}
		
		
		if(!inputTree.isTerminal()){ // If not a terminal node
			for(TreeNode children: inputTree.getArrayListOfChildren()){
				cladogram(children);
				/*Indicates a change with >>>
				if(!children.getConsistencyValueSet().equals(inputTree.getConsistencyValueSet())){
					//Don't change terminal nodes if they are empty and terminal
					if(children.isTerminal()&&children.getConsistencyValueSet().size()==0){
						
					}else{
						parent.text(">>>",children.getXPosition()-parent.textWidth(">>>"),children.getYPosition());	
					}
				}
				*/	
			}
			
			colorNodeLine(inputTree);
			
			//draws the line extending from the node going toward the left
			parent.line(inputTree.getXStartPosition(),inputTree.getAbsoluteYPosition()-textHeight/2,inputTree.getAbsouluteXPosition(),inputTree.getAbsoluteYPosition()-textHeight/2);
			
			//Draws boxes at the internal nodes
			parent.fill(255);
			parent.rect(inputTree.getAbsouluteXPosition()-5, (inputTree.getAbsoluteYPosition()-textHeight/2)-5, 10, 10);
			
			clickableNodes.add(inputTree);
			//new RectButtonChecker((int)inputTree.getXPosition()-5,(int)(inputTree.getYPosition()-textHeight/2)-5,10,10)
			
			
			//verticalLines
			//Draw a line from the first node to the last node
			float first = inputTree.getArrayListOfChildren().get(0).getAbsoluteYPosition();
			float last = inputTree.getArrayListOfChildren().get(inputTree.getArrayListOfChildren().size()-1).getAbsoluteYPosition();
			parent.line(inputTree.getAbsouluteXPosition(),last-textHeight/2,inputTree.getAbsouluteXPosition(),first-textHeight/2);	
		}else{
			//Sets position so that each terminal node is equally spaced
			yPositionTreeNode=yPositionTreeNode-textHeight; 
			inputTree.setAbsoluteYPosition(yPositionTreeNode);//Only applies to terminal nodes
			
			colorNodeLine(inputTree);
		
			//This Creates the length which extends from the node
			parent.line(inputTree.getXStartPosition(),inputTree.getAbsoluteYPosition()-textHeight/2,inputTree.getAbsouluteXPosition(),inputTree.getAbsoluteYPosition()-textHeight/2);
			parent.noFill();
			//parent.stroke(inputTree.getColor());
			//Makes a box around the terminal node
			//parent.rect(endScaledNode, inputTree.getYPosition()-textHeight/2, parent.textWidth(inputTree.getLabel()), textHeight);
			parent.stroke(0);
		}
		
		parent.reset();
		//This prints the Nucleotide which belongs at this tree	
		
		if(inputTree.isTerminal()){//For terminal nodes
			//Color the letter of the node
			if(inputTree.getParentNode()!=null&&!inputTree.getParentNode().getConsistencyValueSet().equals(inputTree.getConsistencyValueSet())&&
					inputTree.getConsistencyValueSet().size()!=0 && rootNode.getConsistencyValueSet().size()==1){
				parent.fill(parent.color(205,0,0));
			}
			parent.text(inputTree.getConsistencyValueSet().toString(),inputTree.getAbsouluteXPosition(),inputTree.getAbsoluteYPosition());
			parent.fill(0);
		}else{//For nonterminal nodes
			//Color the letter of the node
			if(inputTree.getParentNode()!=null&&!inputTree.getParentNode().getConsistencyValueSet().equals(inputTree.getConsistencyValueSet()) 
					&&rootNode.getConsistencyValueSet().size()==1){
				parent.fill(parent.color(205,0,0));
			}
			parent.text(inputTree.getConsistencyValueSet().toString(),inputTree.getAbsouluteXPosition()-parent.textWidth('A')*3,inputTree.getAbsoluteYPosition());
			parent.fill(0);
		}
	}
	
	/**
	 * Gets the TreeNode which is root
	 * 
	 * @return TreeNode root
	 */
	public TreeNode getRootNode(){
		return rootNode;
	}
	public boolean isPhylogram(){
		return isPhylogram;
	}
	public void handleClick(){
		//	Go through a list of all the click coordinates
		for(TreeNode node:clickableNodes){
			//	If the mouse was within one of the click coordinates then add it to the
			//	list of active click coordinates.
			if(	parent.mouseX<(node.getAbsouluteXPosition()+5) &&//Right Side 
				parent.mouseX>(node.getAbsouluteXPosition()-5) &&//Left Side
				//The -textHeight/2 is to get to the center of the line.
				parent.mouseY<((node.getAbsoluteYPosition()-textHeight/2)+5) &&//Top Side 
				parent.mouseY>((node.getAbsoluteYPosition()-5-textHeight/2))){//Bottom Side)
				
				if(arrayOfActiveClickedNodes.contains(node)){
					arrayOfActiveClickedNodes.remove(node);			
				}else{
					arrayOfActiveClickedNodes.add(node);	
				}
			}
		}
	}
	
	/**
	 * ColorNodeLine is a method that colors in the line which extends from the node toward the left
	 * This method uses the ConsistencyValueSet which is preloaded by TreeAlignmentDrawer. 
	 * 
	 * @param iTreeNode the Node that needs to be colored
	 */
	private void colorNodeLine(TreeNode iTreeNode){
		if(iTreeNode.getParentNode()!=null &&!iTreeNode.getParentNode().getConsistencyValueSet().equals(iTreeNode.getConsistencyValueSet())&&
			iTreeNode.getConsistencyValueSet().size()!=0 && rootNode.getConsistencyValueSet().size()==1){
			parent.stroke(parent.color(255,0,0));
		}else if(parentIsAnActiveClickedNode(iTreeNode)){
			
			parent.stroke(parent.color(30,30,158));
		}else{
			parent.stroke(0);
		}
		
	}
	private void colorinBox(){
		parent.fill(0);
		for(TreeNode node:arrayOfActiveClickedNodes){
			parent.rect(node.getAbsouluteXPosition()-5, (node.getAbsoluteYPosition()-textHeight/2)-5, 10, 10);	
		}		
	}
	
	/**
	 * This method returns whether or not the node in question has a parent who is in the array of 
	 * actively clicked nodes
	 * 
	 * @param inputNode the node that should be tested
	 * 
	 * @return true, if one of the parent nodes is in the array of actively clicked nodes.
	 */
	private boolean parentIsAnActiveClickedNode(TreeNode inputNode){
		TreeNode temp = inputNode;
		while(temp!=null){
			if(arrayOfActiveClickedNodes.contains(temp)){
				return true;
			}else{
				temp=temp.getParentNode();
			}
		}
		return false;
		
	}
	

}