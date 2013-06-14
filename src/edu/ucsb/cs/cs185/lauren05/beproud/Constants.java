package edu.ucsb.cs.cs185.lauren05.beproud;

public class Constants {
	public static final int ADD_ENTRY 			= 2;
	public static final int EDIT_ENTRY 			= 3;
	public static final int PICK_DATE 			= 4;
	
	public static final String DATE_FORMAT		= "MM/dd/yyyy";
	
	public static final String FILE				= "BeProudData.txt";
	
	public static final String CONNECTION_ERROR = "Please connect to working Internet connection.";
	
	public static final String INDEX			= "index";
	public static final String ENTRY 			= "entry";
	public static final String DATE				= "date";
	public static final String CATEGORY			= "categories";
	public static final String YEAR				= "year";
	public static final String MONTH			= "month";
	public static final String DAY				= "day";
	
	public static final String MENTAL			= "Mental Health";
	public static final String PHYSICAL			= "Physical Health";
	public static final String FINANCIAL		= "Financial Wellbeing";
	public static final String EDUCATIONAL		= "Educational Pursuit";
	public static final String ALTRUISTIC		= "Altruistic Deed";
	
	// TODO -- Do we need this?
	public static String TWEET			= "tweet";
	
	// Preference Constants
	public static final String PREF_KEY_OAUTH_TOKEN 		= "oauth_token";
	public static final String PREF_KEY_OAUTH_SECRET 		= "oauth_token_secret"; 
	public static final String PREF_KEY_TWITTER_LOGIN 		= "isTwitterLoggedIn";
	public static final String TWITTER_CALLBACK_URL 		= "x-oauthflow-twitter://callback";
	
	// Twitter
	public static final String TWITTER_CONSUMER_KEY 		= "eQLWpLvC7b8zZpgVaY2tA";
	public static final String TWITTER_CONSUMER_SECRET		= "DPE2J2Qic6JRmuADssZacaxkCR7qZ0O2wc7TrVck";

	// Twitter OAuth URLs
	public static final String URL_TWITTER_AUTH 			= "auth_url";
	public static final String URL_TWITTER_OAUTH_VERIFIER 	= "oauth_verifier";
	public static final String URL_TWITTER_OAUTH_TOKEN 		= "oauth_token";
	
	public static final String[] ENTRY_OPTIONS = {
		"Tweet",
		"Edit",
		"Delete"
//		"Twitter Logout"
	};
	
	public static final String[] CATEGORIES = {
		MENTAL,
		PHYSICAL,
		FINANCIAL,
		EDUCATIONAL,
		ALTRUISTIC
	};
	
	public static final int[] COLORS = { 
		R.color.orange, 
		R.color.red, 
		R.color.green, 
		R.color.blue, 
		R.color.yellow 
	};
}