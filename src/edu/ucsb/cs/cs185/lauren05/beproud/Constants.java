package edu.ucsb.cs.cs185.lauren05.beproud;

public class Constants {
	public static final int ADD_ENTRY 			= 2;
	public static final int EDIT_ENTRY 			= 3;
	public static final int PICK_DATE 			= 4;
	public static final int TWEET_ENTRY			= 5;
	
	public static final String INDEX			= "index";
	public static final String ENTRY 			= "entry";
	public static final String DATE				= "date";
	public static String TWEET					= "tweet";
	
	//Twitter stuff
	public static final String TWITTER_CONSUMER_KEY = "eQLWpLvC7b8zZpgVaY2tA";
	public static final String TWITTER_CONSUMER_SECRET= "DPE2J2Qic6JRmuADssZacaxkCR7qZ0O2wc7TrVck";
	
//	public static final String REQUEST_URL = "http://api.twitter.com/oauth/request_token";
//	public static final String ACCESS_URL = "http://api.twitter.com/oauth/access_token";
//	public static final String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";
//	
//	public static final String	OAUTH_CALLBACK_SCHEME	= "x-oauthflow-twitter";
//	public static final String	OAUTH_CALLBACK_HOST		= "callback";
//	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	
	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token"; //"1253145654-K934TI95re472c43hGAurb6172tTNGBAd8rAPYo"; 
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret"; //"PaLy5ulHCrrJsvmepe96jxEvS3HA6G2ez38LEM4"; 
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	static final String TWITTER_CALLBACK_URL = "x-oauthflow-twitter://callback"; //"x-oauthflow-twitter://callback";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	
	public static void setConstantsTweet( String s )
	{
		Constants.TWEET = s;
	}
	
}