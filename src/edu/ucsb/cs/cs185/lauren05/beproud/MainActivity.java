package edu.ucsb.cs.cs185.lauren05.beproud;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends SherlockFragmentActivity {

    // Declare Variables
    ActionBar mActionBar;
    ViewPager mPager;
    Tab tab;
    ViewPagerAdapter adapter;
    ArrayList<Entry> list;
				
	private File myFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		list = new ArrayList<Entry>();

		// Activate Navigation Mode Tabs
        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        // Locate ViewPager in activity_main.xml
        mPager = (ViewPager) findViewById(R.id.pager);
 
        // Activate Fragment Manager
        FragmentManager fm = getSupportFragmentManager();
 
        // Capture ViewPager page swipes
        ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Find the ViewPager Position
                mActionBar.setSelectedNavigationItem(position);
            }
        };
 
        mPager.setOnPageChangeListener(ViewPagerListener);
        // Locate the adapter class called ViewPagerAdapter.java
        adapter = new ViewPagerAdapter(fm);
        // Set the View Pager Adapter into ViewPager
        mPager.setAdapter(adapter);
 
        // Capture tab button clicks
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
 
            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                // Pass the position on tab click to ViewPager
                mPager.setCurrentItem(tab.getPosition());
            }
 
            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }
 
            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        };
 
        // Create first Tab
        tab = mActionBar.newTab().setText("List").setTabListener(tabListener);
        mActionBar.addTab(tab);
 
        // Create second Tab
        tab = mActionBar.newTab().setText("Chart").setTabListener(tabListener);
        mActionBar.addTab(tab);
 
        // Create third Tab
        tab = mActionBar.newTab().setText("Calendar").setTabListener(tabListener);
        mActionBar.addTab(tab);

		recreateList();
	}
	
	public void scrollToListDate(Calendar cal) {
        mPager.setCurrentItem(0);
        adapter.listTab.scrollToListDate(cal);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data); 
		
		ListTab listFragment = (ListTab) adapter.getItem(0);
		listFragment.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		super.getSupportMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add: {
			Intent intent = new Intent(MainActivity.this, EntryActivity.class);
    		startActivityForResult(intent, Constants.ADD_ENTRY);
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	public void appendToFile(Entry e) {		
		String categories = "";
		
		categories += "\""+Constants.MENTAL+"\":\"" 		+ Boolean.toString(e.isMental) +"\",";
		categories += "\""+Constants.PHYSICAL+"\":\"" 		+ Boolean.toString(e.isPhysical) +"\",";
		categories += "\""+Constants.FINANCIAL+"\":\"" 		+ Boolean.toString(e.isFinancial) +"\",";
		categories += "\""+Constants.EDUCATIONAL+"\":\"" 	+ Boolean.toString(e.isEducational) +"\",";
		categories += "\""+Constants.ALTRUISTIC+"\":\"" 	+ Boolean.toString(e.isAltruistic) +"\"";
						
		try {	
			//check if stuff is already in the file
			String fileData = getFileContents();
						
			FileOutputStream fOutStream 		= new FileOutputStream(myFile);
			OutputStreamWriter outStreamWriter 	= new OutputStreamWriter(fOutStream);
			
			String toJSON;
			if (fileData.isEmpty())
				toJSON = "{ \"data\": [" +
						"{" +
						"\"day\":\"" + e.entryDate.get(Calendar.DAY_OF_MONTH) 	+"\"," + 
						"\"month\":\"" + e.entryDate.get(Calendar.MONTH)		+"\"," + 
						"\"year\":\"" + e.entryDate.get(Calendar.YEAR) 			+"\"," + 
						"\"entry\":\"" + e.entryText 							+"\"," +
						"\"categories\": {" + categories + "}" +
						"}]}";
			else {	
				// delete chars: ]}"				
				toJSON = fileData.substring(0, fileData.length()-2); 							
				toJSON = toJSON.concat(",{" +
						"\"day\":\"" + e.entryDate.get(Calendar.DAY_OF_MONTH) 	+"\"," + 
						"\"month\":\"" + e.entryDate.get(Calendar.MONTH)		+"\"," + 
						"\"year\":\"" + e.entryDate.get(Calendar.YEAR) 			+"\"," + 
						"\"entry\":\"" + e.entryText 							+"\"," +
						"\"categories\": {" + categories + "}" +
						"}]}");
			}	
						
			outStreamWriter.append(toJSON);
			
			outStreamWriter.close();
			fOutStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	public String getFileContents() {
		String fileContents = "";
		
		try {
			myFile = new File(getApplicationContext().getFilesDir(), Constants.FILE);
			
			if (!myFile.exists()) {
				myFile.createNewFile();
			}
			
			FileInputStream fInStream 	= new FileInputStream(myFile);
			BufferedReader buffReader 	= new BufferedReader(new InputStreamReader(fInStream));
			
			String dataRow = "";
			while ((dataRow = buffReader.readLine()) != null) {
				fileContents += dataRow;
			}
			
			fInStream.close();
			buffReader.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fileContents;
	}
	

	
	public class CustomComparator implements Comparator<Entry> {
	    @Override
	    public int compare(Entry o1, Entry o2) {
	        return -1*o1.entryDate.compareTo(o2.entryDate);
	    }
	}
	public void sortList() {
		Collections.sort(list, new CustomComparator());
	}
	
	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	public void rewriteData() {
		myFile = new File(getApplicationContext().getFilesDir(),
				Constants.FILE);

		if (myFile.exists()) {
			boolean deleted = myFile.delete();

			if (deleted) {
				myFile = new File(getApplicationContext()
						.getFilesDir(), Constants.FILE);

				// ArrayList should have most up-to-date contents, so write that
				// to file
				for (int i = 0; i < list.size(); i++) {
					 appendToFile(list.get(i));
				}

				adapter.notifyDataSetChanged();
			}
		}
	}
	private void recreateList() {
		try {
			list = new ArrayList<Entry>();
			myFile = new File(getApplicationContext()
					.getFilesDir(), Constants.FILE);

			if (myFile.exists()) {
				// Parse JSON object in file 'BeProudData.txt' which is stored
				// in SD card
				JSONObject jsonObject = new JSONObject(getFileContents());
				JSONArray jsonDataAry = jsonObject.getJSONArray("data");

				for (int i = 0; i < jsonDataAry.length(); i++) {
					JSONObject jDataObj = (JSONObject) jsonDataAry.get(i);
					JSONObject jCatObj = jDataObj
							.getJSONObject(Constants.CATEGORY);

					String entry = jDataObj.getString(Constants.ENTRY);

					Entry e = new Entry(entry);

					int year = Integer.parseInt(jDataObj
							.getString(Constants.YEAR));
					int month = Integer.parseInt(jDataObj
							.getString(Constants.MONTH));
					int day = Integer.parseInt(jDataObj
							.getString(Constants.DAY));

					e.entryDate.set(Calendar.YEAR, year);
					e.entryDate.set(Calendar.MONTH, month);
					e.entryDate.set(Calendar.DAY_OF_MONTH, day);

					e.isMental = Boolean.parseBoolean(jCatObj
							.getString(Constants.MENTAL));
					e.isPhysical = Boolean.parseBoolean(jCatObj
							.getString(Constants.PHYSICAL));
					e.isFinancial = Boolean.parseBoolean(jCatObj
							.getString(Constants.FINANCIAL));
					e.isEducational = Boolean.parseBoolean(jCatObj
							.getString(Constants.EDUCATIONAL));
					e.isAltruistic = Boolean.parseBoolean(jCatObj
							.getString(Constants.ALTRUISTIC));

					list.add(e);
					sortList();
				}

				adapter.notifyDataSetChanged();
			} else {
				myFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
