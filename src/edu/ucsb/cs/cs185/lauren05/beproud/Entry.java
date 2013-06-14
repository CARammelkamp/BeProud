package edu.ucsb.cs.cs185.lauren05.beproud;

import java.util.Calendar;

public class Entry {
	public Calendar entryDate;
	
	public String entryText;
//	public String entryDate;
	
	public boolean isMental 		= false;
	public boolean isPhysical 		= false;
	public boolean isFinancial 		= false;
	public boolean isEducational 	= false;
	public boolean isAltruistic 	= false;
	
	public boolean[] categories = {
			isMental,
			isPhysical,
			isFinancial,
			isEducational,
			isAltruistic
	};
	
	public Entry(String entryText) {
		this.entryDate = Calendar.getInstance();
		this.entryText = entryText;	
	}
}