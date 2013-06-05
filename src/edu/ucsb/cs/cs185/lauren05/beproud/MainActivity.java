package edu.ucsb.cs.cs185.lauren05.beproud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {
	String todaysDate = null;
	String[] userInput = null;
	ListView listView = null;
	ArrayList<Entry> list;
	EntryAdapter adapter;
	String timeStamp;
	File myFile;
	
		// Progress dialog
		ProgressDialog pDialog;

		// Twitter
		private static Twitter twitter;
		private static RequestToken requestToken;
		
		// Shared Preferences
		private static SharedPreferences mSharedPreferences;
		
		// Internet Connection detector
		private ConnectionDetector cd;
		
		// Alert Dialog Manager
		AlertDialogManager alert = new AlertDialogManager();
		
	    int j = 0;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		timeStamp = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
		
		listView = (ListView) findViewById(R.id.listview);
		
		list 	= new ArrayList<Entry>();
		adapter = new EntryAdapter();
		
		listView.setAdapter(adapter);;

		
		// Shared Preferences
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);

		//re-create listview based on stored data file (BeProudData.txt)
		recreateList();
		
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// Binding user's accomplishment entries to ListAdapter
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final CharSequence[] items = { "tweet", "edit", "delete" };
				
				final Entry curEntry 	= list.get(position);
				final int index 		= position;

				// set title
				alertDialogBuilder.setTitle("Entry Options");
				alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: // Tweet
							
							Constants.setConstantsTweet(curEntry.entryText);
							
							cd = new ConnectionDetector(getApplicationContext());

							// Check if Internet present
							if (!cd.isConnectingToInternet()) {
								// Internet Connection is not present
								alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
										"Please connect to working Internet connection", false);
								// stop executing code by return
								return;
							}
							
							// Check if twitter keys are set
							if(Constants.TWITTER_CONSUMER_KEY.trim().length() == 0 || Constants.TWITTER_CONSUMER_SECRET.trim().length() == 0){
								// Internet Connection is not present
								alert.showAlertDialog(MainActivity.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
								// stop executing code by return
								return;
							}
							
							Log.v("MainActivity", "about to log into twitter");
							
							loginToTwitter();
							Log.v("MainActivity", "logging in..?");
							break;
						case 1: // Edit
							Intent intent = new Intent(MainActivity.this, EntryActivity.class);
							intent.putExtra(Constants.INDEX, index);
							intent.putExtra(Constants.ENTRY, curEntry.entryText);
							intent.putExtra(Constants.DATE, curEntry.entryDate);						
							startActivityForResult(intent, Constants.EDIT_ENTRY);
							break;
						case 2: // Delete
							list.remove(index);
							rewriteData();
							adapter.notifyDataSetChanged();							
							break;
						default:
							break;
						}
					}
				}).setCancelable(true).create().show();
			}
		});

		
		/** This if conditions is tested once is
		 * redirected from twitter page. Parse the uri to get oAuth
		 * Verifier
		 * */
		if (!isTwitterLoggedInAlready()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(Constants.TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri
						.getQueryParameter(Constants.URL_TWITTER_OAUTH_VERIFIER);

				try {
					// Get the access token
					AccessToken accessToken = twitter.getOAuthAccessToken(
							requestToken, verifier);

					// Shared Preferences
					Editor e = mSharedPreferences.edit();

					// After getting access token, access token secret
					// store them in application preferences
					e.putString(Constants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(Constants.PREF_KEY_OAUTH_SECRET,
							accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes
					
					try {
						new updateTwitterStatus().execute(Constants.TWEET);
					}
					catch(Exception ex)
					{
						Log.v("MainActivity", "caught exception");
					}
					
					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data); 
		
		if (resultCode == RESULT_OK) {
			
			switch (requestCode) {
			case Constants.ADD_ENTRY: {
				Log.v("MainActivity", "ADD entry.");
				
				String text 	= data.getExtras().getString(Constants.ENTRY); 
			    String date 	= data.getExtras().getString(Constants.DATE); 
		     
			    list.add(new Entry(text, date));
			    appendToFile(date, text);
			    
			    j++;
			    Log.v("onActivityResult", "ADD_ENTRY: "+j +":" +text +" -- "+ date);
			    
			    adapter.notifyDataSetChanged();
			    
				break;
			}
			case Constants.EDIT_ENTRY: {
				Log.v("MainActivity", "EDIT entry.");
				
				int index 		= data.getExtras().getInt(Constants.INDEX);
				String text 	= data.getExtras().getString(Constants.ENTRY); 
			    String date 	= data.getExtras().getString(Constants.DATE); 
				
			    list.get(index).entryText = text;
			    list.get(index).entryDate = date;
			    
			    rewriteData();
			    
				/**********************************DANGER: LOGOUT FOR PROTOTYPE DEMO ONLY************************************/
			    logoutFromTwitter();
			    /**********************************END DANGER****************************************************************/
			    
				break;
			}
			case Constants.TWEET_ENTRY: {
				
				Toast.makeText(this.getApplicationContext(), "MainActivity: tweet in onActivityResult", Toast.LENGTH_SHORT).show();
				Log.v("MainActivity", "TWEET entry");
			}
			default:
				break;
			}
		}
		
		else if (resultCode == RESULT_CANCELED) {    
	         // TODO
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		super.getSupportMenuInflater().inflate(R.menu.menu_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			Intent intent = new Intent(MainActivity.this, EntryActivity.class);
    		startActivityForResult(intent, Constants.ADD_ENTRY);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class EntryAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public EntryAdapter() {
			inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public long getItemId(int position) {
			return position;
		}

		public int getCount() {
			return list.size();
		}

		public Entry getItem(int position) {
			return list.get(position);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder	= null;
			
			TextView entryText 	= null;
			TextView dateText 	= null;

//			EntryObject entry = (EntryObject) getItem(position);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			entryText = holder.getEntryText();
			entryText.setText(getItem(position).entryText);

			dateText = holder.getDateText();
			dateText.setText(getItem(position).entryDate);

			return convertView;
		}
	    
	    private class ViewHolder {
			private View row;
			
			private TextView entryTextView 	= null;
			private TextView dateTextView 	= null;

			public ViewHolder(View row) {
				this.row = row;
			}

			TextView getEntryText() {
				if (entryTextView == null) {
					entryTextView = (TextView) row.findViewById(R.id.entry_text);
				}
				
				return entryTextView;
			}
			
			TextView getDateText() {
				if (dateTextView == null) {
					dateTextView = (TextView) row.findViewById(R.id.date_text);
				}
				
				return dateTextView;
			}
		}
	}
	
	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();
			
			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter
						.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(requestToken.getAuthenticationURL())));
				Log.v("MainActivity","logintoTwitter request token grab");
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			Toast.makeText(getApplicationContext(),
					"Already Logged into twitter", Toast.LENGTH_LONG).show();
			
			try {
				new updateTwitterStatus().execute(Constants.TWEET);
			}
			catch(Exception ex)
			{
				Log.v("MainActivity", "caught exception");
			}
			
		}
	}

	
	
	
	/**
	 * Function to update status
	 * */
	class updateTwitterStatus extends AsyncTask<String, String, String> {
		//Alex suggested loading circle? make invisible on pre-execute and post-execute.
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Updating to twitter...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			Log.d("Tweet Text", "> " + args[0]);
			String status = args[0];
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);
				
				// Access Token 
				String access_token = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, "");
				// Access Token Secret
				String access_token_secret = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, "");
				
				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
				
				// Update status
				twitter4j.Status response = twitter.updateStatus(status);
				
				Log.d("Status", "> " + response.getText());
			} catch (TwitterException e) {
				// Error in updating status
				Log.d("Twitter Update Error", e.getMessage());
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog and show
		 * the data in UI Always use runOnUiThread(new Runnable()) to update UI
		 * from background thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(),
							"Status tweeted successfully", Toast.LENGTH_SHORT)
							.show();
				}
			});
			
			try{
				this.finalize();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		

	}

	/**
	 * Function to logout from twitter
	 * It will just clear the application shared preferences
	 * */
	private void logoutFromTwitter() {
		// Clear the shared preferences
		Editor e = mSharedPreferences.edit();
		e.remove(Constants.PREF_KEY_OAUTH_TOKEN);
		e.remove(Constants.PREF_KEY_OAUTH_SECRET);
		e.remove(Constants.PREF_KEY_TWITTER_LOGIN);
		e.commit();
	}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);
	}

	protected void onResume() {
		super.onResume();
	}

	
	public void finishCurrentActivity()
	{
		Log.v("TwitterActivity", "finishCurrentActivity");
		Intent result = getIntent();
		setResult(RESULT_OK, result);
		finish();
	}
	
	/**
	 * writes data (date and entry) to SD card in JSON format
	 * resource used: http://www.java-samples.com/showtutorial.php?tutorialid=1523
	 * @param date -- date of the entry
	 * @param text -- entry text
	 */
	
	public void appendToFile(String date, String entry)
	{
		Log.v("MainActivity","appendToFile");
		try {
			
			//check if stuff is already in the file
			String stuffInFile = getFileContents();
			
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = 
									new OutputStreamWriter(fOut);
			String toJSON;
			
			if(stuffInFile.isEmpty())
				toJSON = "{\"data\":[{\"date\":\"" + date +"\",\"entry\":\"" + entry +"\"}]}";
			else
			{	
				//do String parse and append new date and entry to end of JSON object				
				toJSON = stuffInFile.substring(0, stuffInFile.length()-2); //delete chars: ]}"
				toJSON = toJSON.concat(",{\"date\":\"" + date +"\",\"entry\":\"" + entry +"\"}]}");
			}	
			
			Log.v("MainActivity", "writing this to file:" + toJSON);
			myOutWriter.append(toJSON);
			myOutWriter.close();
			fOut.close();
			Toast.makeText(getBaseContext(),
					"Done writing SD 'BeProudData.txt'",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public String getFileContents()
	{
		try {
			File myFile = new File(Environment.getExternalStorageDirectory().toString() + "/BeProudData.txt");
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(
					new InputStreamReader(fIn));
			String aDataRow = "";
			String aBuffer = "";
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow;
			}
			myReader.close();
			
			Log.v("MainActivity","aBuffer: "+aBuffer);
			return aBuffer;

		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		
		return "";
	}
	
	
	/**
	 * using info from ArrayList list object
	 * rewrites the data in the file BeProud.txt (stored on SD card)
	 */
	
	public void rewriteData()
	{
		
		myFile = new File(Environment.getExternalStorageDirectory().toString() + "/BeProudData.txt");
		
		if(myFile.exists())
		{	
			//delete stored file 
			File myFile = new File(Environment.getExternalStorageDirectory().toString() + "/BeProudData.txt");
			boolean deleted = myFile.delete();
			
			if(deleted) {
	
				//list should have most up-to-date contents
				//so write to file
				for(int i=0; i< list.size(); i++)
					appendToFile(list.get(i).entryDate, list.get(i).entryText);
				
				//display new list
				 adapter.notifyDataSetChanged();
			}
		}
	}

	
	public void recreateList()
	{
		myFile = new File(Environment.getExternalStorageDirectory().toString() + "/BeProudData.txt");
		
		try {
			if(!myFile.exists())
				myFile.createNewFile();
			else
			{
				//Parse JSON object in file 'BeProudData.txt' which is stored in SD card
				JSONObject jsonobject = new JSONObject(getFileContents());
				JSONArray jsonarr = jsonobject.getJSONArray("data");
				
				for(int i=0; i<jsonarr.length(); i++)
				{
					JSONObject jobj = (JSONObject) jsonarr.get(i);
					list.add(new Entry(jobj.getString("entry"), jobj.getString("date")));
				}
				
			    adapter.notifyDataSetChanged();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
				e1.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
