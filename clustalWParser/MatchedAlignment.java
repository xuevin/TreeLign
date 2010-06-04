package clustalWParser;

public class MatchedAlignment {
	
	String reference;
	public MatchedAlignment(String inputRefrenceAlignment){
		reference = inputRefrenceAlignment;
		
	}
	public String getMatchedAlignment(String inputComparisonAlignment){
		String returnString ="";
		//If lengths are the same, then continue
		if(reference.length()==inputComparisonAlignment.length()){
			for(int i=0;i<reference.length();i++){
				if(inputComparisonAlignment.charAt(i)==inputComparisonAlignment.charAt(i)){
					returnString+="*";
				}else{
					returnString+=inputComparisonAlignment.charAt(i);
				}
			}
		}
		return returnString;
	}

}
