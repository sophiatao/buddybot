 /**
    * BuddyBot! BuddyBot and associated documents Copyright (c) 2017 Sophia Tao.
    * Matches people with similar interests into groups. 
    * Default word list credits to TalkEnglish.com, retrieved from: http://www.talkenglish.com/vocabulary/top-1500-nouns.aspx
    * @author:    Sophia Tao
    * @date:      August 12, 2017
    * @version:   1.0
    */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;


public class BuddyBot {
	
	/* below is some sample code to test the BuddyBot using college admission essays*/
	public static void main(String[] args) {
		final int GROUP_SIZE = 3;
		BuddyBot test = new BuddyBot("college_essay_test.csv", GROUP_SIZE);
		test.findBuddies();
	}

	String wordList;
	String buddyList;
	int groupSize;
	ArrayList<Buddy> buddies;
	ArrayList<ArrayList<Buddy>> matches = new ArrayList<ArrayList<Buddy>>();
	
	/**
	 * Constructors.
	 * @param buddies (required) name of the .csv file containing the buddies. Entries must be strings. 
	 * Files containing quote literals and commas are not yet supported, they will need to be manually removed beforehand.
	 * @param wordList (optional) important keywords to use in matching, otherwise will resort to default list
	 */
	BuddyBot(String buddyList, int groupSize, String wordList) {
		this.wordList = wordList;
		this.buddyList = buddyList;
		this.groupSize = groupSize;
	}
	
	BuddyBot(String buddyList, int groupSize) {
		this(buddyList, groupSize, "common_nouns.txt");
	}
	
	/**
	 * Prints all the matches!
	 * @param groupsize (required) size of each group, must be greater than total number of buddies, integer >= 2.
	 **/
	void findBuddies() {
		Scanner in = new Scanner(System.in);
		
		/*calling buddyMaker() to initialize our Buddy objects*/
		String buddyFile = buddyList;
		while(true) {
			try {
				buddyMaker(buddyFile);
				break;
			} catch (FileNotFoundException e) {
				System.out.println("Error: File not found. Please re-enter file name\n(should be type .csv) or type 'q' to quit:");
				buddyFile = in.next();
				if (buddyFile.toLowerCase().equals("q"))
					System.exit(0);
				else
					continue;
			}
		};
		
		/*calling readKeywords() to initialize our interests*/
		String wordFile = wordList;
		HashSet<String> keywords;
		while(true) {
			try {
				keywords = readKeywords(wordFile);
				break;
			} catch (FileNotFoundException e) {
				System.out.println("Error: File not found. Please re-enter file name\n(should be type .csv) or type 'q' to quit:");
				wordFile = in.next();
				if (wordFile.toLowerCase().equals("q"))
					System.exit(0);
				else
					continue;
			}
		};
		
		in.close();
		
		/*initializing interests for each buddy*/
		System.out.println("------------------------------\nSorting interests...");
		for (Buddy b : buddies) {
			b.findInterests(keywords);
			b.sortInterests();
			System.out.println(b.name + ": " + b.interests);
		}
		
		
		System.out.println("------------------------------\nMatchmaking...");
		match(groupSize, buddies);
		
		/*printing out the results*/
		for (ArrayList<Buddy> group : matches) {
			System.out.printf("-------- GROUP of (%d) --------\n", group.size());
			for (Buddy b: group) {
				System.out.println(b.name);
			}
		}
		System.out.printf("------------------------------\nMade %d groups total", matches.size());
	}
	
	
	/**
	 * Matches buddies with each other based on interests. It gives priority to preference, but will iterate over all the interests.
	 * This recursive algorithm will be improved in future versions, as the current version creates a leftover group with no interests in common.
	 * Stay tuned for revisions!
	 * @param groupSize size of groups you want to create
	 */
	void match(int groupSize, ArrayList<Buddy> unmatched){
		if (unmatched.size() <= groupSize) { //base case, put all the unmatched into one group
			ArrayList<Buddy> group = new ArrayList<Buddy>();
			for (Buddy b: unmatched) {
				System.out.printf("Matched %s in the last group.\n", b.name);
				group.add(b);
			}
			matches.add(group);
			return;
		}

		ArrayList<Buddy> group = new ArrayList<Buddy>();
		recursiveIteration:
		while (group.size() < groupSize) {
			Collections.shuffle(unmatched); //randomize order
			for (int i = 0; i < unmatched.size(); i++) {
				Buddy b = unmatched.get(i);
				group.add(b);
				System.out.printf("Put %s in empty group.\n",b.name);
				unmatched.remove(b);
				for (int j = 0; j < b.iterableInterests.size(); j++) { //finding an interest in order of preference
					String interest = b.iterableInterests.get(j);
					for (int k = i; k < unmatched.size(); k++) { //finding a match
						if (k == i)
							continue; //skip over the same buddy we're already choosing a match for
						Buddy c = unmatched.get(k);
							for (int l = 0; l < c.iterableInterests.size(); l++) {
								if (c.iterableInterests.get(l).equals(interest)) { //yay! we've got an interests match!
										if (group.size() < groupSize) {
											System.out.printf("Matched %s and %s based on '%s'\n",b.name, c.name, interest);
											group.add(c);
											unmatched.remove(c);
											k--; //to account for the removal of a student in unmatched
										}
										else 
											break recursiveIteration;
								}
							}//end for l
					}//end for k
				}// end for j
				i--; //account for removal of a student in unmatched
			}//end for i
		}//end while
		matches.add(group);
		match(groupSize, unmatched);
	}
	
	/**
	 * Creates buddy objects and filenames from a .csv file.
	 * @param fileName (required) name of file ending with .csv with buddy name and about
	 * @throws FileNotFoundException if the file does not exist
	 **/
	void buddyMaker(String fileName) throws FileNotFoundException {
		final String DELIMITER = ",";
		File csv = new File(fileName);
		Scanner csvReader = new Scanner(csv);
		csvReader.useDelimiter(DELIMITER);
		buddies = new ArrayList<Buddy>(); //initializes an arraylist to store all the new buddies
		while (csvReader.hasNext()) {
			String name = csvReader.next();
			String about = csvReader.nextLine();
			buddies.add(new Buddy(name, about));
		}
		csvReader.close();
	}
	
	
	/**
	 * Reads a list of keywords from file and return them as an ArrayList.
	 * @param fileName (required) name of file ending with .txt with one keyword per line
	 * @throws FileNotFoundException if the file does not exist
	 **/
	HashSet<String> readKeywords(String fileName) throws FileNotFoundException {
		File txt = new File(fileName);
		Scanner txtReader = new Scanner(txt);
		ArrayList<String> keywords = new ArrayList<String>();
		while (txtReader.hasNextLine()) {
			keywords.add(txtReader.nextLine());
		}
		txtReader.close();
		HashSet<String> noDuplicates = removeDuplicates(keywords);
		return noDuplicates;
	}
	
	/**
	 * Reads a list of keywords from ArrayList and return them as HashSet, duplicates removed.
	 * @param words (required) list of keywords containing duplicates
	 **/
	HashSet<String> removeDuplicates(ArrayList<String> words) {
		HashSet<String> keywords = new HashSet<String>();
		keywords.addAll(words);
		return keywords;
	}
}
