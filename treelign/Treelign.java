package treelign;

import processing.core.*;
import widgets.TreeAlignmentDrawer;


public class Treelign extends PApplet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PFont font;
	private TreeAlignmentDrawer treeAlignment;
	String pathNewick;
	String pathClustalW;
	boolean loadScreen;
	int whoHasControl; 	//	Int represents what has control
						// 	0 = No One
						// 	1 = middleDragger
						//	2 = treeAlignmentDrawer
	
	public void setup(){
		online=true;
		//Some Defaults for the Applet
		font = loadFont("data/AndaleMono-20.vlw");
		textFont(font);
		textSize(20);
		fill(0);
		size(900, 600);
		strokeCap(PApplet.SQUARE);
		background(255);
		pathNewick =null;
		pathClustalW =null;
		whoHasControl=0;
		
		//If you want to turn the load screen on, make loadScreen true.
		loadScreen=true;
		drawLoading();

	}
	public void draw(){
		//Draw method for PApplets
	}
	public void reset(){
		fill(0);
		stroke(0);
		rectMode(CORNER);
		textAlign(LEFT);
		strokeWeight(1);
		textSize(20);
		strokeCap(PApplet.SQUARE);
		
	}
	public int getTextHeight(){
		return font.size;
	}
	public void mouseClicked(){
		//Mouse listener when condition is for load screen
		if(loadScreen==true){
			if(	mouseX<((width/4)+100) &&//Right Side 
				mouseX>((width/4)-100) &&//Left Side
			 	mouseY>50 &&//Top Side
			 	mouseY<150){//Bottom Side
				pathNewick=selectInput();
			}else if(mouseX<((3*width/4)+100) &&//Right Side 
				mouseX>((3*width/4)-100) &&//Left Side
			 	mouseY>50 &&//Top Side
			 	mouseY<150){//Bottom Side
				pathClustalW=selectInput();
			}
			if(pathNewick!=null && pathClustalW!=null){
				treeAlignment= new TreeAlignmentDrawer(this, 0, 0, width, height, pathNewick, pathClustalW);
				treeAlignment.draw();
				loadScreen=false;
			}
		}else{//Mouse condition for when condition is not in load screen
			treeAlignment.handleClick(mouseX, mouseY);
		}

		//System.out.println(frameCount);


	}
	public void mouseReleased(){
		if(treeAlignment!=null){
			treeAlignment.handleMouseReleased();	
		}
		
		whoHasControl=0;
	}
	public void mouseDragged(){
		if(loadScreen==false){
			if(whoHasControl==0 && pmouseX>treeAlignment.getMiddle()-5 && pmouseX < treeAlignment.getMiddle()+5){
				whoHasControl=1;
			}
			
			if(whoHasControl==1){
				//Check to make sure that the middle stays within boundaries
				if(treeAlignment.getMiddle()+(mouseX-pmouseX)+10<width){
					treeAlignment.moveMiddle(treeAlignment.getMiddle()+(mouseX-pmouseX));
					treeAlignment.draw();
				}
			}else{
				whoHasControl=2;
				treeAlignment.handleDrag();
			}
		}
		
	}
	public void keyPressed(){
		
		if(loadScreen==false){
			if (key == PConstants.CODED) {
				if (keyCode == PConstants.DOWN){
			    	treeAlignment.decreaseTreeIndex();
			    	treeAlignment.draw();
			    }else if(keyCode ==PConstants.UP){
			    	treeAlignment.increaseTreeIndex();
			    	treeAlignment.draw();
			    }else if(keyCode==PConstants.ALT){
					treeAlignment.switchMode();
					treeAlignment.draw();
			    }else if(keyCode==PConstants.LEFT){
					treeAlignment.shiftAlignmentLeft();
					treeAlignment.redrawAlignmentAndScrollBar();
			    }else if(keyCode==PConstants.RIGHT){
			    	treeAlignment.shiftAlignmentRight();
			    	treeAlignment.redrawAlignmentAndScrollBar();
			    }
			}else{
				if(key=='e'){
					treeAlignment.exportConsistency();
				}else if(key=='j'){
					treeAlignment.setIndex(1300);
					treeAlignment.draw();
				}else if(key=='5'){
					 size(1000,800);
					 //frame.setSize(1000,800);
					 treeAlignment.adjustSize(0, 0, width, height);
					 treeAlignment.draw();
				}else if(key=='r'){
					 treeAlignment.reset();
					 treeAlignment.draw();
				}
				
			}
		}
	}
	public void keyReleased(){
	}
	private void drawLoading(){
		if (loadScreen==false){
			
			//pathNewick ="/home/shared/levy/bbb22.nh";
			//pathClustalW = "/home/shared/levy/bbb22.nuc";
			//pathNewick="/Users/Vinny/Dropbox/workspace/Treelign/data/tree_for_sam_with_labels.newick.new";
			//pathClustalW="/Users/Vinny/Dropbox/workspace/Treelign/data/BB0057.aa.aln";
			if(pathNewick!=null && pathClustalW!=null){
				treeAlignment= new TreeAlignmentDrawer(this, 0, 0, width, height, pathNewick, pathClustalW);
				treeAlignment.draw();
			}
		}
		else{
			fill(100);
			rectMode(CENTER);
			rect(width/4,100,200,100);
			rect(3*width/4,100,200,100);
			fill(255);
			textAlign(CENTER);
			text("Newick Tree",width/4,100);
			text("Alignment File",3*width/4,100);
			reset();			
		}

	}
	/*
	private void drawOnTop(){
		drawMenu();
	}
	
	private void drawMenu(){
		rect(width/2-50,0,100,10);
		if(	mouseX<((width/2)+50) &&//Right Side 
			mouseX>((width/2)-50) &&//Left Side
		 	mouseY>0 &&//Top Side
		 	mouseY<5){//Bottom Side
			rect(width/2-50,0,100,50);
		}
	}
	*/

}