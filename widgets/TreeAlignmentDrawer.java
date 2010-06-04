package widgets;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import newickParser.TreeNode;

import treelign.Treelign;
public class TreeAlignmentDrawer{
	private Treelign parent;
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	private int treeIndex;
	private HashMap<String,String> hashMap;
	private AlignmentDrawer alignmentDrawer;
	private ScrollBarDrawer scrollBarDrawer;
	private TreeDrawer treeDrawer;
	private String consistencyString;
	private int middle;
	private final int HEIGHTOFSCROLLBAR;
	private HashMap<TreeNode,String> matchedHashMap;
	private MatchingDrawer matchingDrawer;
	boolean pendingMatching;
	
	
	/**
	 * Instantiates a new tree alignment drawer. The alignments are constrained to the box.
	 * 
	 * @param iParent the parent PApplet
	 * @param iXPosition the x position of the top left corner of the TreeAlignment widget
	 * @param iYPosition the y position of the top left corner of the TreeAlignment widget
	 * @param iWidth the width of the TreeAlignment widget
	 * @param iHeight the height of the TreeAlignment widget
	 * @param iPathNewick the path to the newick file
	 * @param iPathClustalW the path to the clustalw file
	 */
	public TreeAlignmentDrawer(Treelign iParent,int iXPosition,int iYPosition,int iWidth, int iHeight, String iPathNewick, 
			String iPathClustalW){
		parent = iParent;
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
		middle=width/2;
		treeDrawer= new TreeDrawer(parent, 0, yPosition, width/2, height, iPathNewick);
		HEIGHTOFSCROLLBAR=20;
		alignmentDrawer= new AlignmentDrawer(parent, xPosition+width/2, yPosition+HEIGHTOFSCROLLBAR, width/2, height-HEIGHTOFSCROLLBAR, iPathClustalW);
		scrollBarDrawer= new ScrollBarDrawer(parent, xPosition+width/2, yPosition, width/2,HEIGHTOFSCROLLBAR,
				alignmentDrawer.getAlignmentLength(),alignmentDrawer.getAlignmentIndex(),alignmentDrawer.getNumberOfDisplayedCharacters());
		hashMap=alignmentDrawer.getHashMap();
		treeIndex=0;
		
		matchedHashMap = new HashMap<TreeNode,String>();
		matchingDrawer = new MatchingDrawer(parent,this, width/8, height/8, (int)(width*(0.75)), (int)(height*(0.75)));
		matchTreeWithAlignment(treeDrawer.getArrayListOfTerminalNodes(), alignmentDrawer.getHashMap());
		
		consistencyString=getStringOfConsistentIndex();
		preLoadIndex(treeIndex);
		
	}
	public void draw() {	
		if(pendingMatching){
			matchingDrawer.draw();
		}else{
			parent.reset();
			treeDrawer.draw();
			/*
			 * Notes the index at which the tree is at
			 */
			if(!treeDrawer.isPhylogram()){
				parent.text(treeIndex+1,100,100);
			}
			
			redrawAlignmentAndScrollBar();
		}
		
		
		
		
	}
	public void redrawAlignmentAndScrollBar(){
		alignmentDrawer.clearSpace();
		drawAlignment();
		scrollBarDrawer.updateValues(alignmentDrawer.getAlignmentLength(), alignmentDrawer.getAlignmentIndex(), alignmentDrawer.getNumberOfDisplayedCharacters());
		scrollBarDrawer.draw();
	}
	public void switchMode(){
		treeDrawer.switchMode();
	}
	public void adjustSize(int iXPosition, int iYPosition, int iWidth, int iHeight,int iMiddle) {
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
		treeDrawer.adjustSize(0, 0, iMiddle, height);
		alignmentDrawer.adjustSize(iMiddle, HEIGHTOFSCROLLBAR, width-iMiddle, height-HEIGHTOFSCROLLBAR);
		scrollBarDrawer.adjustSize(iMiddle,0,width-iMiddle,HEIGHTOFSCROLLBAR);
	}
	public void adjustSize(int iXPosition, int iYPosition, int iWidth, int iHeight) {
		xPosition = iXPosition;
		yPosition = iYPosition;
		width = iWidth;
		height = iHeight;
		treeDrawer.adjustSize(0, 0, middle, height);
		alignmentDrawer.adjustSize(middle, HEIGHTOFSCROLLBAR, width-middle, height-HEIGHTOFSCROLLBAR);
		scrollBarDrawer.adjustSize(middle,HEIGHTOFSCROLLBAR,width-middle,HEIGHTOFSCROLLBAR);
	}
	//Probably like a jump to type of thing
	public void setIndex(int columnValue){
		if(columnValue>=0 && columnValue<=alignmentDrawer.getAlignmentLength()){
			//treeIndex=columnValue;
			//preLoadIndex(treeIndex);
			alignmentDrawer.setAlignmentIndex(columnValue);
		}else{
			System.out.println("TOO FAR:" + columnValue);
		}
	}
	public void increaseTreeIndex(){
		if(treeIndex+1>=alignmentDrawer.getAlignmentLength()){
			
		}else{
			treeIndex++;
			preLoadIndex(treeIndex);	
		}
		
	}
	public void decreaseTreeIndex(){
		if(treeIndex-1<0){
			
		}else{
			treeIndex--;
			preLoadIndex(treeIndex);
		}
	}
	
	public void shiftAlignmentRight() {
		alignmentDrawer.increaseAlignmentIndex();
		
	}
	public void shiftAlignmentLeft() {
		alignmentDrawer.decreaseAlignmentIndex();
		
	}
	public void exportConsistency(){
		try {
		      PrintStream out = new PrintStream(new FileOutputStream(
		          "OutFile.txt"));
		      for (int i = 0; i < consistencyString.length(); i++)
		        if(consistencyString.charAt(i)=='_'){
		        	out.println("1");
		        }else if(consistencyString.charAt(i)=='I'){
		        	out.println("-1");
		        }else if(consistencyString.charAt(i)=='U'){
		        	out.println("0");
		        }
		      out.close();
	
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
	      }
	}
	public void moveMiddle(int inputXPosition){
		middle=inputXPosition;
		adjustSize(xPosition, yPosition, width, height, inputXPosition);
	}
	public int getMiddle(){
		return middle;
	}
	public void reset(){
		middle=width/2;
		adjustSize(xPosition, yPosition, width, height, middle);
		alignmentDrawer.setAlignmentIndex(0);
	}
	public void handleClick(int mouseX, int mouseY){
		if(!pendingMatching){
			if(mouseX<middle){
				treeDrawer.handleClick();
				draw();
				
			}else{
				if(mouseY>HEIGHTOFSCROLLBAR){
					alignmentDrawer.handleClick(mouseX, mouseY);
					drawAlignment();	
				}else{
					scrollBarDrawer.handleClick();
					
					setIndex(scrollBarDrawer.getTextIndex());
					System.out.println(scrollBarDrawer.getTextIndex());
					redrawAlignmentAndScrollBar();
					
				}
				
			}	
		}else{
			matchingDrawer.handleClick();
		}
	}
	public void handleDrag(){
		if(!pendingMatching){
			scrollBarDrawer.handleDrag();
			setIndex(scrollBarDrawer.getTextIndex());
			redrawAlignmentAndScrollBar();	
		}else{
			matchingDrawer.draw();
		}
		
	}
	public void handleMouseReleased(){
		scrollBarDrawer.handleMouseReleased();
	}
	/**
	 * Draws the alignments and the markers by looking at the array list of terminal nodes.
	 */
	private void drawAlignment(){
		alignmentDrawer.clearSpace();
		parent.reset();
		//top Alignment is the y position of last alignment data. (bottom of the text) 
		int topAlignment=(int)(treeDrawer.getArrayListOfTerminalNodes().get(treeDrawer.getArrayListOfTerminalNodes().size()-1).getAbsoluteYPosition());
		
		// First, highlight the columns
		alignmentDrawer.highlightColumns(topAlignment-parent.getTextHeight());
		/*
		 * For each terminal node on the tree, use its coordinates and draw the alignments in the correct position.
		 * In addition, draw the markers which are slightly above the last element in the array
		 */
		for(TreeNode members :matchedHashMap.keySet()){
			alignmentDrawer.drawAlignment(members.getAbsoluteYPosition(), matchedHashMap.get(members), members.getColor());
		}
				
		alignmentDrawer.drawMarkers(topAlignment-2*parent.getTextHeight());
		alignmentDrawer.drawConsistency(topAlignment-parent.getTextHeight(),consistencyString);
		
	}
	
	/**
	 * Loads up the data for consistency for a specific column. 
	 * First, it clears the data that was held on the tree. Then it fills in the data recursively 
	 * starting from the root. Finally, it resolves the data.
	 * 
	 * @param columnValue the column in the alignment that the user wants to look at
	 */
	private void preLoadIndex(int columnValue){
		clearExistingConsistencyValue(treeDrawer.getRootNode());
		fillInConsistencyValues(treeDrawer.getRootNode(),columnValue);//Fills starting from root
		resolveConsistencey(treeDrawer.getRootNode());
		//parent.text(columnValue+1,300,100);
	}
	
	/**
	 * Clears the existing consistency values in the tree
	 * 
	 * @param inputTreeNode is the TreeNode that holds information about the node
	 */
	private void clearExistingConsistencyValue(TreeNode inputTreeNode) {
		if(!inputTreeNode.isTerminal()){//If not terminal
			for(TreeNode child: inputTreeNode.getArrayListOfChildren()){
					clearExistingConsistencyValue(child);
			}
			inputTreeNode.getConsistencyValueSet().clear();
		}else{
			inputTreeNode.getConsistencyValueSet().clear();
		}
		
	}
	private void fillInConsistencyValues(TreeNode inputTreeNode,int column){ 
		if(!inputTreeNode.isTerminal()){//If not a terminal node
			for(TreeNode child: inputTreeNode.getArrayListOfChildren()){
				fillInConsistencyValues(child,column);
			}
			if(inputTreeNode.getParentNode()!=null && inputTreeNode.getParentNode().getParentNode()!=null){
				//This is when we go up a tree, the sets are combined.
				inputTreeNode.getParentNode().getConsistencyValueSet().addAll(inputTreeNode.getConsistencyValueSet());	
			}else{//This is root
			}

		}else{//Terminal Node
			String alignmentString=matchedHashMap.get(inputTreeNode);
			
			if(alignmentString!=null){
				inputTreeNode.getConsistencyValueSet().add(alignmentString.charAt(column)); 
				if(inputTreeNode.getParentNode()!=null && inputTreeNode.getParentNode().getParentNode()!=null){
					inputTreeNode.getParentNode().getConsistencyValueSet().add(alignmentString.charAt(column));	
				}
			}
			
		}
		
	}
	//Resolves Consistency by taking in the most frequent of the children
	//Should start with root
	private void resolveConsistencey(TreeNode inputTreeNode){
		if(inputTreeNode.getConsistencyValueSet().size()!=1){
			
			HashSet<Character> tempChild = new HashSet<Character>(); 
			HashSet<Character> intersectionSet = new HashSet<Character>();
			
			//This step should only be performed on root, afterward, it cascades the correct value down.
			if(inputTreeNode.getParentNode()==null){	
				//Comparing multiple children for something common. Find intersection of children and set the current node to that value
				for(TreeNode members: inputTreeNode.getArrayListOfChildren()){
					tempChild.addAll(members.getConsistencyValueSet());
					for(TreeNode members2: inputTreeNode.getArrayListOfChildren()){
						if(members!=members2){
							tempChild.retainAll(members2.getConsistencyValueSet());	
						}
					}
					intersectionSet.addAll(tempChild);
					tempChild.clear();	
				}
				inputTreeNode.getConsistencyValueSet().clear();
				inputTreeNode.getConsistencyValueSet().addAll(intersectionSet);
			}
			
			//If the parent node is not resolved, fill it back with the union of the children and exit
			if(inputTreeNode.getParentNode()==null&&inputTreeNode.getConsistencyValueSet().size()!=1){
				for(TreeNode children:inputTreeNode.getArrayListOfChildren()){
					inputTreeNode.getConsistencyValueSet().addAll(children.getConsistencyValueSet());
				}
				return;
			}
			//If the current node does not have a consistency of 1, look at the parent to determine its identity.
			if(inputTreeNode.getConsistencyValueSet().size()!=1 && inputTreeNode.getParentNode()!=null){
				inputTreeNode.getConsistencyValueSet().clear();
				inputTreeNode.getConsistencyValueSet().addAll(inputTreeNode.getParentNode().getConsistencyValueSet());
			}
			
			//Continue on with the children
			for(TreeNode child: inputTreeNode.getArrayListOfChildren()){
				if(child.getConsistencyValueSet().size()!=1){
					resolveConsistencey(child);
				}
			}
		}
		
	}
	

	/**
	 * Gets the string which tells which column is consistent or not
	 * 
	 * @return the string of consistent index
	 */
	private String getStringOfConsistentIndex(){
		String stringOfConsistentIndex ="";
		int lengthOfAlignment=hashMap.values().iterator().next().length(); 
		
		ArrayList<String> list = new ArrayList<String>();
		HashSet<String> nonRedundantList = new HashSet<String>();
		for(int x =0;x<lengthOfAlignment;x++){
			clearExistingConsistencyValue(treeDrawer.getRootNode());
			fillInConsistencyValues(treeDrawer.getRootNode(),x);//Fills starting from root
			resolveConsistencey(treeDrawer.getRootNode());
	
			check(treeDrawer.getRootNode(),list);
			//System.out.print(x);
			//System.out.println(list);
			
			nonRedundantList.addAll(list);
			
			if(treeDrawer.getRootNode().getConsistencyValueSet().size()!=1){
				stringOfConsistentIndex+="U";
			}
			else if (nonRedundantList.size()==list.size()){
				stringOfConsistentIndex+="_";
			}else{
				stringOfConsistentIndex+="I";
			}
			nonRedundantList.clear();
			list.clear();
		}
		
		return stringOfConsistentIndex;
	}
	private void check(TreeNode inputTree,ArrayList<String> list){
		if(!inputTree.isTerminal()){
			for(TreeNode children:inputTree.getArrayListOfChildren()){
				check(children,list);
			}
			//If there is a difference, add it to list
			if(inputTree.getParentNode()!=null &&!inputTree.getParentNode().getConsistencyValueSet().equals(inputTree.getConsistencyValueSet())&&
					inputTree.getConsistencyValueSet().size()!=0 && treeDrawer.getRootNode().getConsistencyValueSet().size()==1){
				list.add(inputTree.getParentNode().getConsistencyValueSet().toString() + ">"
						+ inputTree.getConsistencyValueSet().toString());
			}else{
			}
		}else{
			if(inputTree.getParentNode()!=null&&!inputTree.getParentNode().getConsistencyValueSet().equals(inputTree.getConsistencyValueSet())&&
					inputTree.getConsistencyValueSet().size()!=0 && treeDrawer.getRootNode().getConsistencyValueSet().size()==1){
				list.add(inputTree.getParentNode().getConsistencyValueSet().toString() + ">"
						+ inputTree.getConsistencyValueSet().toString());
			}else{
			}
		}
	}
	private void matchTreeWithAlignment(ArrayList<TreeNode> iTreeNode,HashMap<String,String> inputHash){
		ArrayList<TreeNode> listOfFailedNodes=new ArrayList<TreeNode>();
		
		for(TreeNode node:iTreeNode){
			//Try to get the alignment info
			String terminalNodeName=node.getLabel().toLowerCase();
			
			//This is the attempt to retrieve the info from the hashmap
			String alignmentString;
			alignmentString = inputHash.get(terminalNodeName);
			
			//Try different variations
			if(alignmentString==null){
				terminalNodeName=terminalNodeName.replaceFirst("-", "");
				alignmentString=inputHash.get(terminalNodeName);
				if(alignmentString==null){
					if(terminalNodeName.contains("_")){
						terminalNodeName=terminalNodeName.substring(0,terminalNodeName.indexOf("_"));
					}
					alignmentString=inputHash.get(terminalNodeName);
				}
			}
			if(alignmentString==null){
				System.out.println("There was a failure to match " + node.getLabel() + " to an alignment");
				//TODO THis is where the MatchedWidget would go
				pendingMatching = true;
				listOfFailedNodes.add(node);
//				for(String name:inputHash.keySet()){
//					System.out.println(name);
//				}
				
				//matchedHashMap.put(node, null);
				
			}else{
				matchedHashMap.put(node, alignmentString);
			}
		}
		matchingDrawer.tryToMatch(inputHash.keySet(), listOfFailedNodes);
				
	}
	protected void setPendingMatching(boolean bool){
		pendingMatching=bool;
		draw();
	}
	protected void addtoMatchedHashMap(TreeNode node, String alignmentName){
		if(alignmentName!=null){
			if(alignmentName!="key"){
				matchedHashMap.put(node, hashMap.get(alignmentName));	
			}
				
		}else{
			
		}
			
	}
}