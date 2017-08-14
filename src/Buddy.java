 /**
    * Creates a Buddy object for each person with their interests, for use with BuddyBot.
    * @author:    Sophia Tao
    * @date:      August 12, 2017
    * @version:   1.0
    */

import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

public class Buddy {

	/**
	 * Constructor.
	 * @param about (required) A description of the buddy, can be a short blurb written by the person in question.
	 */
	Buddy(String name, String about) {
		this.about = about;
		this.name = name;
	}
	
	String about;
	String name;
	HashMap<String, Integer> interests; //stores interests with the number of times it occurred
	ArrayList<String> iterableInterests; //interests as array, without the count
	
	/**
	 * Finds a Buddy's interests based on given interests, stores interest and count in the HashMap interests.
	 * @param keywords (required) list of interests.
	 */
	void findInterests(HashSet<String> keywords) {
		interests = new HashMap<String, Integer>(); //initializes interest HashMap
		String[] aboutWords = toWords(about);
		for (String word : aboutWords) {
			boolean match = keywords.contains(word);
			if (match) {
				if (interests.containsKey(word))
					interests.put(word, interests.get(word) + 1);
				else
					interests.put(word, 1);
			}
		}
	}
	
	/**
	 * Sorts a Buddy's interests, rearranging from most to least.
	 */
	void sortInterests() {
		HashMap<String, Integer> sortedInterests = interests.entrySet().stream()
                .sorted(HashMap.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		interests = sortedInterests;
		iterableInterests = new ArrayList<String>();
		for (String key : interests.keySet()) {
			iterableInterests.add(key);
		}
	}
	
	/**
	 * Converts a string of text into individual words, without punctuation.
	 * @param text (required) the text to be converted.
	 * @return individual words from text as array
	 */
	String[] toWords(String text) {
		char[] chars = about.toLowerCase().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (!Character.isLetter(chars[i]))
				chars[i] = ' ';
		}
		String parsedString = new String(chars);
		String[] words = parsedString.split(" ");
		return words;
	}
}
