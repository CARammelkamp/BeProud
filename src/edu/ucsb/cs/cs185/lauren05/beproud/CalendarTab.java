package edu.ucsb.cs.cs185.lauren05.beproud;

//used resources from Lauri Nevala: https://github.com/nevalla/CalendarView

import java.util.ArrayList;
import java.util.Calendar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class CalendarTab extends SherlockFragment {

	public Calendar currentDate;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> calItems; // container to store some random calendar
									// items
	MainActivity mainActivity;
	
	@Override
	public SherlockFragmentActivity getSherlockActivity() {
		return super.getSherlockActivity();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	float values[] = { 700, 400, 100, 500, 600 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from fragmenttab1.xml
		View view = inflater.inflate(R.layout.calendar, container, false);
		
		mainActivity = (MainActivity)getActivity();
    
		currentDate = Calendar.getInstance();

		// TODO (problem if january)
		int month = currentDate.get(Calendar.MONTH);

		currentDate.set(Calendar.MONTH, month);

//		getActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		calItems = new ArrayList<String>();
		adapter = new CalendarAdapter(getActivity(), currentDate);

		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

		handler = new Handler();
		handler.post(calendarUpdater);

		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy",
				currentDate));

		TextView previous = (TextView) view.findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentDate.get(Calendar.MONTH) == currentDate
						.getActualMinimum(Calendar.MONTH)) {
					currentDate.set((currentDate.get(Calendar.YEAR) - 1),
							currentDate.getActualMaximum(Calendar.MONTH), 1);
				} else {
					currentDate.set(Calendar.MONTH,
							currentDate.get(Calendar.MONTH) - 1);
				}
				refreshCalendar();
			}
		});

		TextView next = (TextView) view.findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentDate.get(Calendar.MONTH) == currentDate
						.getActualMaximum(Calendar.MONTH)) {
					currentDate.set((currentDate.get(Calendar.YEAR) + 1),
							currentDate.getActualMinimum(Calendar.MONTH), 1);
				} else {
					currentDate.set(Calendar.MONTH,
							currentDate.get(Calendar.MONTH) + 1);
				}
				refreshCalendar();

			}
		});

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				TextView date = (TextView) v.findViewById(R.id.date);
				if (date instanceof TextView && !date.getText().equals("")) {

					String day = date.getText().toString();
					int dayInt = Integer.parseInt(day);
					Calendar c = currentDate;
					c.set(Calendar.DAY_OF_MONTH, dayInt);
					mainActivity.scrollToListDate(c);
					
//					// return chosen date as string format
//					android.text.format.DateFormat.format("yyyy-MM",currentDate) + "-" + day);
				}

			}
		});

		return view;
	}

	public void refreshCalendar() {
		TextView title = (TextView) getView().findViewById(R.id.title);

		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some random calendar items

		title.setText(android.text.format.DateFormat.format("MMMM yyyy",
				currentDate));
	}

	// Add icons to dates with recorded entries
	public Runnable calendarUpdater = new Runnable() {
		@Override
		public void run() {
			 calItems.clear();
			 
			 for (Entry e : mainActivity.list) {
				 if (e.entryDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
					 calItems.add(Integer.toString(e.entryDate.get(Calendar.DAY_OF_MONTH)));
				 }
			 }

			adapter.setItems(calItems);
			adapter.notifyDataSetChanged();
		}
	};

	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		
	}
	
}
