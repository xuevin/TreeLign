package newickParser;
import java.io.*;
import java.util.Stack;


public class NewickParser {
	TreeNode treeRoot;
	public NewickParser(String path){
		treeRoot=null;	
		try{
			parse(path);
		}catch(IOException exception){
			System.out.println("There was a problem parsing the newick file");
		}
	}
	
	public TreeNode getTreeRoot(){
		return treeRoot;
	}

	private void parse(String path) throws IOException{
		
		TreeNode root = null;
		TreeNode treeNode = null;
		FileReader fileReader;
		Stack<Character> stack = new Stack<Character>();
		try {//temp_tree.newick
			fileReader = new FileReader(path);
			BufferedReader buffer = new BufferedReader(fileReader);
			
			String string;
			try {
				//Read the first line of the newick line
				string = buffer.readLine();	
				for(int x=0;x<string.length();x++){
					
					stack.push(string.charAt(x));
				}
				//System.out.println(string);
				
			} catch (IOException e) {
				System.out.println("IO Exception");
				e.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		}
		
		
		float newickLength =0;
		String label="";
		String strain="";
		
		while(!stack.empty()){
			//System.out.println(stack.peek().charValue());
			
			//If the char is a semicolon, then it pops it off
			if(stack.peek()==';'||stack.peek()==' '|| stack.peek()==']'){
				stack.pop();
			//If the char is a ')' and root is null, it makes a new root and sets the current root  
			}else if(stack.peek()=='['){
				strain=label;
				label="";
				stack.pop();
				//Here is where it may go wrong ^^^^
			}else if(stack.peek()==')'){
				if (treeNode==null){
					root = new TreeNode(label);
					label = "";
					stack.pop();
					treeNode = root;
					//If the root is not null, it adds a new node to it and now the current is the 
					//node just made
				}else{
					//if(newickLength!=0){
						treeNode.addChildNode(newickLength, label, treeNode,strain);
					//}
					//the last one entered should be the last node...
					treeNode=treeNode.getArrayListOfChildren().get(treeNode.getArrayListOfChildren().size()-1);
					label="";
					strain="";
					newickLength=0;
					stack.pop();
				}
			}else if(stack.peek()=='('){
				if(newickLength!=0){
					treeNode.addChildNode(newickLength, label, treeNode,strain);	
				}
				label="";
				strain="";
				newickLength=0;
				treeNode=treeNode.getParentNode();
				
				stack.pop();
				//For Parsing numbers
			}else if (stack.peek()==':'){
				newickLength= Float.parseFloat(label);
				label="";
				stack.pop();
				//For parsing commas
			}else if (stack.peek()==','){
				if(newickLength!=0){
					treeNode.addChildNode(newickLength, label, treeNode,strain);
				}
				label="";
				strain="";
				newickLength=0;
				stack.pop();
			}else{
				String temp = "";
				temp += stack.pop();
				temp += label; 
				label=temp;
			}
		}
		treeRoot = root;
		if(treeRoot==null){
			throw new IOException();
		}
	}
	

}
