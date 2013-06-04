package edu.ucsb.cs.cs185.lauren05.beproud;
//used resources from Lauri Nevala: https://github.com/nevalla/CalendarView

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;


public class CalendarView extends SherlockFragmentActivity {
	public Calendar currentDate;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items; // container to store some random calendar items
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);
	    currentDate = Calendar.getInstance();
	    onNewIntent(getIntent());
	    
	    // TODO (problem if january)
	    int month = currentDate.get(Calendar.MONTH) - 1;
	    
	    currentDate.set(Calendar.MONTH, month);
	    
	    this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    items = new ArrayList<String>();
	    adapter = new CalendarAdapter(this, currentDate);
	    
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(adapter);
	    
	    handler = new Handler();
	    handler.post(calendarUpdater);
	    
	    TextView title  = (TextView) findViewById(R.id.title);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", currentDate));
	    
	    TextView previous  = (TextView) findViewById(R.id.previous);
	    previous.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(currentDate.get(Calendar.MONTH)== currentDate.getActualMinimum(Calendar.MONTH)) {				
					currentDate.set((currentDate.get(Calendar.YEAR)-1),currentDate.getActualMaximum(Calendar.MONTH),1);
				} else {
					currentDate.set(Calendar.MONTH,currentDate.get(Calendar.MONTH)-1);
				}
				refreshCalendar();
			}
		});
	    
	    TextView next  = (TextView) findViewById(R.id.next);
	    next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentDate.get(Calendar.MONTH)== currentDate.getActualMaximum(Calendar.MONTH)) {				
					currentDate.set((currentDate.get(Calendar.YEAR)+1),currentDate.getActualMinimum(Calendar.MONTH),1);
				} else {
					currentDate.set(Calendar.MONTH,currentDate.get(Calendar.MONTH)+1);
				}
				refreshCalendar();
				
			}
		});
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	TextView date = (TextView)v.findViewById(R.id.date);
		        if(date instanceof TextView && !date.getText().equals("")) {
		        	
		        	Intent intent = new Intent();
		        	String day = date.getText().toString();
		        	if(day.length()==1) {
		        		day = "0"+day;
		        	}
		        	// return chosen date as string format 
		        	intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", currentDate)+"-"+day);
		        	setResult(RESULT_OK, intent);
		        	finish();
		        }
		        
		    }
		});
	}
	
	public void refreshCalendar() {
		TextView title  = (TextView) findViewById(R.id.title);
		
		adapter.refreshDays();
		adapter.notifyDataSetChanged();				
		handler.post(calendarUpdater); // generate some random calendar items				
		
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", currentDate));
	}
	
	public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("/");
		currentDate.set(Integer.parseInt(dateArr[2]), Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]));
	}
	
	// Add icons to dates with recorded entries
	public Runnable calendarUpdater = new Runnable() {		
		@Override
		public void run() {
//			items.clear();
//			for(int i=0;i<31;i++) {
//				Random r = new Random();
//				
//				if(r.nextInt(10)>6)
//				{
//					items.add(Integer.toString(i));
//				}
//			}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};
}
