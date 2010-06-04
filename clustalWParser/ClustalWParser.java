package clustalWParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ClustalWParser {
	private HashMap<String,String> hashMap;
	
	/**
	 * Instantiates a new clustalw parser.
	 * 
	 * @param inputPath the path at which the string is stored
	 */
	public ClustalWParser(String inputPath){
		hashMap = new HashMap<String,String>();
		parseClustalW(inputPath);
	}
	
	/**
	 * Parses the clustalw. Puts the results into a hash where the key is the 
	 * sequence name and the string is the value
	 * 
	 * @param inputPath the input path
	 */
	private void parseClustalW(String inputPath){
		String path=inputPath;	

		FileReader fileReader;
		try {
			fileReader = new FileReader(path);
			BufferedReader buffer = new BufferedReader(fileReader);
			//Boolean if the first line is parsed. This is to simplify parsing so you can use substrings;
			boolean firstLineParsed=false;
			int charAtStartSeq=0;
			
			try {
				String line;
				while((line=buffer.readLine())!=null){
					if(line.length()==0||line.charAt(0)=='\n'||line.charAt(0)=='\t'){
						//skip line if there is a line skip, tab
					}else{
						if(line.contains("CLUSTAL")){
							//Skips First line
						}else if(firstLineParsed==false){
							for(int i=0;i<line.length();i++){
								//If the character is a space and the character infront is not a sapce
								//Then the index is the start of the sequence.
								if(i+1<line.length()&&line.charAt(i)==' '&&line.charAt(i+1)!=' '){
									if(charAtStartSeq==0){
										charAtStartSeq=i+1;									
									}else{
									}
								}
							}
							//The first line is what determines where the sequence starts.
							firstLineParsed=true;
							putIntoHash(line.substring(0,charAtStartSeq),line.substring(charAtStartSeq, line.length()));				
						}else{
							putIntoHash(line.substring(0,charAtStartSeq),line.substring(charAtStartSeq, line.length()));
						}	
					}
				}
			} catch (IOException e) {
				System.out.println("IO Exception");
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Puts the string from the parsing method into the hash
	 * 
	 * @param seqName the sequence name
	 * @param sequence the alignment sequence
	 */
	private void putIntoHash(String seqName,String sequence){
		//Deletes all spaces in the name of sequence
		String manipulatedSeqName=seqName.replaceAll(" ", "");
		//changes everything to lowercase so it can be found later
		manipulatedSeqName = manipulatedSeqName.toLowerCase();
		
		if(manipulatedSeqName.length()==0){
			manipulatedSeqName="key";
		}
		
		String manipulatedSequence;
		//Deletes the number
		if(seqName.charAt(0)!=' '){
			if(sequence.contains(" ")){
				manipulatedSequence=sequence.substring(0,sequence.lastIndexOf(' '));	
			}else{
				manipulatedSequence=sequence;
			}
			
		}else{
			manipulatedSequence=sequence;
		}
		
		if(hashMap.containsKey(manipulatedSeqName)){
			String temp = hashMap.get(manipulatedSeqName);
			temp+=manipulatedSequence;			
			hashMap.put(manipulatedSeqName, temp);
		}else{
			hashMap.put(manipulatedSeqName, manipulatedSequence);
		}
		
	}
	
	/**
	 * Gets the hash map that has all of the sequence names and alignment strings. 
	 * 
	 * @return the hash map where the key is the seq name and the value is the string
	 */
	public HashMap<String,String> getHashMap(){
		return hashMap;
	}

}
