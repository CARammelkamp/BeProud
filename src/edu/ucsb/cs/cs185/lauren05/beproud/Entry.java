package edu.ucsb.cs.cs185.lauren05.beproud;

import java.util.ArrayList;

public class Entry {
	public String entryText;
	public String entryDate;
	
	public ArrayList<String> categories;
	
	public Entry(String entryText, String entryDate) {
		this.entryText = entryText;
		this.entryDate = entryDate;
	}
}