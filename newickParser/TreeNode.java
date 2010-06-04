package newickParser;

import java.util.ArrayList;
import java.util.HashSet;

public class TreeNode{
	private TreeNode parentNode;
	private ArrayList<TreeNode> arrayListOfChildren;
	private String label;
	private float length;
	private String strain;
	private float xUnscaledPosition;
	private float yPosition;//scaled
	private float xPosition;//scaled
	private int color;
	private float xStartPosition;
	private int howFarFromTerminal;
	private HashSet<Character> consistencyValueSet;
	
	public TreeNode(String root){
		parentNode = null;
		arrayListOfChildren = null;
		label = root;
		length = 0;
		strain = "";
		xUnscaledPosition = 0;
		yPosition = 0;
		xPosition = 0;
		color = 0;
		xStartPosition = 0;
		howFarFromTerminal = 0;
		consistencyValueSet = new HashSet<Character>();
		
	}
	public TreeNode(float input_length, String input_label,TreeNode input_Parent,String input_Strain){
		arrayListOfChildren = null;
		xUnscaledPosition = 0;
		yPosition = 0;
		xPosition = 0;
		color = 0;
		xStartPosition = 0;
		howFarFromTerminal = 0;
		consistencyValueSet = new HashSet<Character>();
		label = input_label;
		length = input_length;
		parentNode= input_Parent;
		strain=input_Strain;
	}
	public void addChildNode(float length,String label,TreeNode input_Parent,String input_Strain){
		if (arrayListOfChildren==null){
			arrayListOfChildren = new ArrayList<TreeNode>();
			TreeNode temp = new TreeNode(length,label,input_Parent,input_Strain);
			arrayListOfChildren.add(temp);
		}else{
			arrayListOfChildren.add(new TreeNode(length,label,input_Parent,input_Strain));
		}
	}
	public TreeNode getParentNode(){
		return parentNode;
	}
	public ArrayList<TreeNode> getArrayListOfChildren(){
		return arrayListOfChildren;
	}
	public String getLabel(){
		return label;
	}
	public float getLength(){
		return length;
	}
	public String getStrain(){
		return strain;
	}
	@Deprecated 
	public void printRecursivly(){
		
		if(arrayListOfChildren!=null){
			for(int x=0;x<arrayListOfChildren.size();x++){
				arrayListOfChildren.get(x).printRecursivly();
			}
		}
		
		System.out.print(getLength()+"\t|");
		System.out.print(getLabel()+"\t|");
		System.out.print(getStrain()+"\t|");
		if(getParentNode()!=null){
			System.out.print(getParentNode().getLabel()+"\n");
		}else{
			System.out.print("\n");	
		}	
		
	}
	public int getNumberOfTerminalNodes(){
		int countOfTerminalNodes=0;
		if(arrayListOfChildren!=null){
			for(int x=0;x<arrayListOfChildren.size();x++){
				if(arrayListOfChildren.get(x).getArrayListOfChildren()==null){
					countOfTerminalNodes++;
				}else{
					countOfTerminalNodes += arrayListOfChildren.get(x).getNumberOfTerminalNodes();
				}
			}
		}
		return countOfTerminalNodes;
	}
	public float getUnscaledXPosition(){			
		TreeNode tempTree = this;
		float sumToTree=0;
		while(tempTree.getParentNode()!=null){
			sumToTree+=tempTree.getLength();
			tempTree=tempTree.getParentNode();
		}
		xUnscaledPosition = sumToTree;
		return xUnscaledPosition;		
	}
	public void setAbsoluteXPosition(float inputXPosition){
		xPosition=inputXPosition;
	}
	public float getAbsouluteXPosition(){
		return xPosition;
	}
	public void setAbsoluteYPosition(float inputPosition){
		yPosition = inputPosition;
	}
	public float getAbsoluteYPosition(){
		if(arrayListOfChildren==null){
			return yPosition;
		}else{
			float sum=0;
			for(int i =0;i<arrayListOfChildren.size();i++){
				sum+=arrayListOfChildren.get(i).getAbsoluteYPosition();
			}
			return sum/arrayListOfChildren.size();
		}
		
	}
	
	//Gets the lowest sibling
	@Deprecated
	public TreeNode getLowestSibling(){
		TreeNode temp;
		if(parentNode!=null){
			 temp = parentNode;
		}else{
			temp=this;
		}
		while(temp.getArrayListOfChildren()!=null){
			temp=temp.getArrayListOfChildren().get(0);
		}
		return temp;
	}
	public int getColor(){
		return color;
	}
	public void setColor(int inputColor){
		color = inputColor;
		
	}
	public boolean isTerminal(){
		if(arrayListOfChildren==null){
			return true;
		}else{
			return false;
		}
	}
	public HashSet<Character> getConsistencyValueSet() {
		return consistencyValueSet;
	}
	public void setXStartPosition(float inputXStartPosition){
		xStartPosition=inputXStartPosition;
	}
	public float getXStartPosition(){
		return xStartPosition;
	}
	public int getNumberOfGenerations(){
		int value =0;
		TreeNode temp = this;
		while(temp.getParentNode()!=null){
			temp=temp.getParentNode();
			value++;
		}
		return value;
	}
	
	/**
	 * Gets the how far from terminal node.
	 * 
	 * @return the how far from terminal node
	 * ie 	1 means that it is a terminal node
	 * 		2 means that it's child is a terminal node
	 */
	public int getHowFarFromTerminalNode(){
		if(howFarFromTerminal==0){
			howFarFromTerminal=calculateHeight();	
		}	
		return howFarFromTerminal;
		
	}
	
	/**
	 * Calculate height of the tree
	 * ie. 	1 means that it is a terminal
	 * 		2 means that it's child is a terminal node
	 * 
	 * @return returns an int that describes what is the height from the terminal node
	 */
	private int calculateHeight(){
		if(isTerminal()){
			return 1;
		}else{
			int max =0;
			for(TreeNode child:arrayListOfChildren){
				if(child.getHowFarFromTerminalNode()>max){
					max=child.getHowFarFromTerminalNode();	
				}
			}
			max+=1;
			howFarFromTerminal=max;
			return max;
		}
	}
}
