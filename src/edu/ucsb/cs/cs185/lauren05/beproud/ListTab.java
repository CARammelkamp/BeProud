package edu.ucsb.cs.cs185.lauren05.beproud;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
//import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

@SuppressLint("SimpleDateFormat")
public class ListTab extends SherlockFragment {

	String todaysDate = null;
	String[] userInput = null;
	File myFile;
	EntryAdapter adapter;
	ListView listView;

	MainActivity mainActivity;

	private static String messageToTweet;

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;

	// Shared Preferences
	private static SharedPreferences mSharedPreferences;

	@Override
	public SherlockFragmentActivity getSherlockActivity() {
		return super.getSherlockActivity();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    mainActivity = (MainActivity) getActivity();
	    
		View view = inflater.inflate(R.layout.activity_list, container, false);

		adapter = new EntryAdapter();
		mSharedPreferences = getActivity().getSharedPreferences("MyPref", 0);
		
		listView = (ListView) view.findViewById(R.id.listview);
		listView.setAdapter(adapter);

		if (android.os.Build.VERSION.SDK_INT >= 9) {
			StrictMode.setThreadPolicy(ThreadPolicy.LAX);
		}

		initializeListView();
		twitterCallback();

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	public class EntryAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public EntryAdapter() {
			inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		public long getItemId(int position) {
			return position;
		}

		public int getCount() {
			return mainActivity.list.size();
		}

		public Entry getItem(int position) {
			return mainActivity.list.get(position);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder 			= null;

			TextView entryText 			= null;
			TextView dateText 			= null;

			ImageView mentalImage 		= null;
			ImageView physicalImage 	= null;
			ImageView financialImage 	= null;
			ImageView educationalImage 	= null;
			ImageView altruisticImage 	= null;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Entry e = getItem(position);

			entryText = holder.getEntryText();
			entryText.setText(e.entryText);

			dateText = holder.getDateText();
			dateText.setText(new SimpleDateFormat(Constants.DATE_FORMAT).format(e.entryDate.getTime()));

			if (e.isMental) {
				mentalImage = holder.getMentalImage();
				mentalImage.setBackgroundResource(R.drawable.circle_mental);
				mentalImage.setVisibility(View.VISIBLE);
			} else {
				mentalImage = holder.getMentalImage();
				mentalImage.setVisibility(View.GONE);
			}

			if (e.isPhysical) {
				physicalImage = holder.getPhysicalImage();
				physicalImage.setBackgroundResource(R.drawable.circle_physical);
				physicalImage.setVisibility(View.VISIBLE);
			} else {
				physicalImage = holder.getPhysicalImage();
				physicalImage.setVisibility(View.GONE);
			}

			if (e.isFinancial) {
				financialImage = holder.getFinancialImage();
				financialImage.setBackgroundResource(R.drawable.circle_financial);
				financialImage.setVisibility(View.VISIBLE);
			} else {
				financialImage = holder.getFinancialImage();
				financialImage.setVisibility(View.GONE);
			}

			if (e.isEducational) {
				educationalImage = holder.getEducationalImage();
				educationalImage.setBackgroundResource(R.drawable.circle_educational);
				educationalImage.setVisibility(View.VISIBLE);
			} else {
				educationalImage = holder.getEducationalImage();
				educationalImage.setVisibility(View.GONE);
			}

			if (e.isAltruistic) {
				altruisticImage = holder.getAltruisticImage();
				altruisticImage.setBackgroundResource(R.drawable.circle_altruistic);
				altruisticImage.setVisibility(View.VISIBLE);
			} else {
				altruisticImage = holder.getAltruisticImage();
				altruisticImage.setVisibility(View.GONE);
			}

			return convertView;
		}

		private class ViewHolder {
			private View row;

			private TextView entryTextView 			= null;
			private TextView dateTextView 			= null;

			private ImageView mentalImageView 		= null;
			private ImageView physicalImageView 	= null;
			private ImageView financialImageView 	= null;
			private ImageView educationalImageView 	= null;
			private ImageView altruisticImageView 	= null;

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

			ImageView getMentalImage() {
				if (mentalImageView == null) {
					mentalImageView = (ImageView) row.findViewById(R.id.mental_image);
				}

				return mentalImageView;
			}

			ImageView getPhysicalImage() {
				if (physicalImageView == null) {
					physicalImageView = (ImageView) row.findViewById(R.id.physical_image);
				}

				return physicalImageView;
			}

			ImageView getFinancialImage() {
				if (financialImageView == null) {
					financialImageView = (ImageView) row.findViewById(R.id.financial_image);
				}

				return financialImageView;
			}

			ImageView getEducationalImage() {
				if (educationalImageView == null) {
					educationalImageView = (ImageView) row.findViewById(R.id.educational_image);
				}

				return educationalImageView;
			}

			ImageView getAltruisticImage() {
				if (altruisticImageView == null) {
					altruisticImageView = (ImageView) row.findViewById(R.id.altruistic_image);
				}

				return altruisticImageView;
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == -1) { // RESULT_OK
			switch (requestCode) {
			case Constants.ADD_ENTRY: {
				String text = data.getExtras().getString(Constants.ENTRY);

				Entry e = new Entry(text);

				int year = data.getExtras().getInt(Constants.YEAR);
				int month = data.getExtras().getInt(Constants.MONTH);
				int day = data.getExtras().getInt(Constants.DAY);

				e.entryDate.set(Calendar.YEAR, year);
				e.entryDate.set(Calendar.MONTH, month);
				e.entryDate.set(Calendar.DAY_OF_MONTH, day);

				e.isMental 		= data.getExtras().getBoolean(Constants.MENTAL);
				e.isPhysical 	= data.getExtras().getBoolean(Constants.PHYSICAL);
				e.isFinancial 	= data.getExtras().getBoolean(Constants.FINANCIAL);
				e.isEducational = data.getExtras().getBoolean(Constants.EDUCATIONAL);
				e.isAltruistic = data.getExtras().getBoolean(Constants.ALTRUISTIC);

				mainActivity.list.add(e);
				mainActivity.sortList();
				mainActivity.appendToFile(e);
				mainActivity.notifyDataSetChanged();

				break;
			}
			case Constants.EDIT_ENTRY: {
				String text = data.getExtras().getString(Constants.ENTRY);

				int index 	= data.getExtras().getInt(Constants.INDEX);
				int year 	= data.getExtras().getInt(Constants.YEAR);
				int month 	= data.getExtras().getInt(Constants.MONTH);
				int day 	= data.getExtras().getInt(Constants.DAY);

				Entry e = mainActivity.list.get(index);

				e.entryText = text;

				e.entryDate.set(Calendar.YEAR, year);
				e.entryDate.set(Calendar.MONTH, month);
				e.entryDate.set(Calendar.DAY_OF_MONTH, day);

				e.isMental 		= data.getExtras().getBoolean(Constants.MENTAL);
				e.isPhysical 	= data.getExtras().getBoolean(Constants.PHYSICAL);
				e.isFinancial 	= data.getExtras().getBoolean(Constants.FINANCIAL);
				e.isEducational = data.getExtras().getBoolean(Constants.EDUCATIONAL);
				e.isAltruistic 	= data.getExtras().getBoolean(Constants.ALTRUISTIC);

				mainActivity.list.set(index, e);
				mainActivity.sortList();

				rewriteData();
				mainActivity.notifyDataSetChanged();

				break;
			}
			default:
				break;
			}
		}
	}
	
	public void scrollToListDate(Calendar cal) {
        int index = 0;
        for (Entry e : mainActivity.list) {
        	int c = e.entryDate.compareTo(cal);
        	if (c<=0) break;
        	index++;
        }
        if (index < mainActivity.list.size()) {
        	listView.smoothScrollToPosition(index);
        }
	}

	private void initializeListView() {
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				final Entry curEntry 	= mainActivity.list.get(position);
				final int index 		= position;


				
				
				alertDialogBuilder.setTitle(R.string.entry_options);
				alertDialogBuilder.setItems(Constants.ENTRY_OPTIONS,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0: // Tweet	
								AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
								alert.setTitle("Tweet Your Accomplishment");
								alert.setMessage("Are you sure you want to tweet your accomplishment?");
								alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0, int arg1) {
										arg0.dismiss();
										
										ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
										
										if (cd.isConnectingToInternet()) {
											if (isTwitterLoggedInAlready()) {
												new updateTwitterStatus().execute(curEntry.entryText);
											} else {
												messageToTweet = curEntry.entryText;
												loginToTwitter();
											}
										} else {
											Toast.makeText(getActivity().getApplicationContext(), Constants.CONNECTION_ERROR, Toast.LENGTH_LONG).show();
											return;
										}
									}});
								alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
									public void onClick(DialogInterface arg0, int arg1) {
										arg0.dismiss();
									}});
								
								alert.show();
								break;
							case 1: // Edit
								Intent intent = new Intent(getActivity(), EntryActivity.class);

								intent.putExtra(Constants.INDEX, 		index);
								
								intent.putExtra(Constants.ENTRY, 		curEntry.entryText);
								intent.putExtra(Constants.YEAR, 		curEntry.entryDate.get(Calendar.YEAR));
								intent.putExtra(Constants.MONTH,		curEntry.entryDate.get(Calendar.MONTH));
								intent.putExtra(Constants.DAY,			curEntry.entryDate.get(Calendar.DAY_OF_MONTH));
								
								intent.putExtra(Constants.MENTAL, 		curEntry.isMental);
								intent.putExtra(Constants.PHYSICAL,		curEntry.isPhysical);
								intent.putExtra(Constants.FINANCIAL,	curEntry.isFinancial);
								intent.putExtra(Constants.EDUCATIONAL,	curEntry.isEducational);
								intent.putExtra(Constants.ALTRUISTIC,	curEntry.isAltruistic);

								startActivityForResult(intent, Constants.EDIT_ENTRY);
								break;
							case 2: // Delete
								mainActivity.list.remove(index);
								mainActivity.notifyDataSetChanged();
								rewriteData();
								break;
//							case 3: //Twitter logout
//								logoutFromTwitter();
							default:
								break;
						}
					}
				}).setCancelable(true).create().show();
			}
		});
	}

	private void rewriteData() {
		myFile = new File(getActivity().getApplicationContext().getFilesDir(), Constants.FILE);

		if (myFile.exists()) {
			boolean deleted = myFile.delete();

			if (deleted) {
				myFile = new File(getActivity().getApplicationContext().getFilesDir(), Constants.FILE);

				// ArrayList should have most up-to-date contents, so write that to file
				for (int i = 0; i < mainActivity.list.size(); i++) {
					 mainActivity.appendToFile(mainActivity.list.get(i));
				}

				mainActivity.notifyDataSetChanged();
			}
		}
	}

	private void twitterCallback() {
		if (!isTwitterLoggedInAlready()) {
			Uri uri = getActivity().getIntent().getData();
			if (uri != null && uri.toString().startsWith(Constants.TWITTER_CALLBACK_URL)) {
				String verifier = uri.getQueryParameter(Constants.URL_TWITTER_OAUTH_VERIFIER);

				try {
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					// Shared Preferences
					Editor e = mSharedPreferences.edit();

					e.putString(Constants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(Constants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
					e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);

					e.commit();

					new updateTwitterStatus().execute(messageToTweet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isTwitterLoggedInAlready() {
		return mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN,
				false);
	}

	private void loginToTwitter() {
		ConfigurationBuilder builder = new ConfigurationBuilder();

		builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);

		Configuration configuration = builder.build();

		twitter = new TwitterFactory(configuration).getInstance();

		try {
			requestToken = twitter.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
		} catch (TwitterException e) {
			Toast.makeText(getActivity(), "Twitter failed to connect. Please check your internet connection", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void logoutFromTwitter() {
		Log.v("ListTab:"," in logoutFromTwitter");
		
		// Clear the shared preferences
		Editor e = mSharedPreferences.edit();
		e.remove(Constants.PREF_KEY_OAUTH_TOKEN);
		e.remove(Constants.PREF_KEY_OAUTH_SECRET);
		e.remove(Constants.PREF_KEY_TWITTER_LOGIN);
		e.commit();

	}

	private class updateTwitterStatus extends AsyncTask<String, String, String> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Updating to twitter...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			// String response = "";

			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);

				String oauth_token = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, "");
				String oauth_secret = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, "");

				AccessToken accessToken = new AccessToken(oauth_token, oauth_secret);
				Twitter twitter 		= new TwitterFactory(builder.build()).getInstance(accessToken);

				// Update status
				twitter4j.Status response = twitter.updateStatus(args[0]);

				Log.v("MainActivity", "response: " + response.getText());
			} catch (TwitterException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Toast.makeText(getActivity(), "Tweet successful!", Toast.LENGTH_SHORT).show();
		}
	}

	public void notifyDataSetChanged() {
		if (adapter!=null)
			adapter.notifyDataSetChanged();
	}
}