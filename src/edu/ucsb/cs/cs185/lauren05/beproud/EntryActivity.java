package edu.ucsb.cs.cs185.lauren05.beproud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Map;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class EntryActivity extends SherlockFragmentActivity {	
	EditText e;
	String timeStamp;
	
	static Map<String,ArrayList<String> > entryObject = null;
	
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initialize variables
		timeStamp 	= new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
		entryObject	= new HashMap<String, ArrayList<String> >();
		
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		e = (EditText) findViewById(R.id.body);
		
		// User is editing their entry.
		if (getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();
			
			this.getSupportActionBar().setTitle(bundle.getString(Constants.DATE));
			e.setText(bundle.getString(Constants.ENTRY));
		}
		
		else {
			// TODO: set date in title bar
		}
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		super.getSupportMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {		
		switch (item.getItemId()) {
		case android.R.id.home:
			// TODO
			// [back button was pressed]
			// prompt user -- is it okay to discard data?
		case R.id.save:
			saveEntry(this.findViewById(R.layout.activity_main));
			return true;
		case R.id.month:
			Intent intent = new Intent(EntryActivity.this, CalendarView.class);
    		intent.putExtra("date", timeStamp);
    		startActivityForResult(intent, Constants.PICK_DATE);				
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void saveEntry(View v){			
		// Use regexes for error handling -- we don't want whitespace as an Accomplishment Entry
		String pattern = "(\\s+)";

		if (e.getText() != null && !e.getText().toString().matches(pattern)) {
//			if (!(entryObject.containsKey(timeStamp)))
//					entryObject.put(timeStamp, new ArrayList<String>());
//			newEntry =  e.getText().toString();
//			if ( !(entryObject.get(timeStamp).contains(newEntry)))
//				entryObject.get(timeStamp).add(newEntry);
//			else
//				Toast.makeText(this.getApplicationContext(), "Sorry, you have already entered this accomplishment.", Toast.LENGTH_LONG).show();		
//			String[] passUserInput = entryObject.get(timeStamp).toArray(new String[entryObject.size()]);
			
			// TODO: check string for correctness
			Intent result = getIntent();
			
			result.putExtra(Constants.ENTRY, e.getText().toString());
			result.putExtra(Constants.DATE, timeStamp);
									
			setResult(RESULT_OK, result);
			finish();
		}
		else
			Toast.makeText(this.getApplicationContext(), "Oops! It seems you have not entered anything. Please enter an Accomplishment.", Toast.LENGTH_LONG).show();	
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.PICK_DATE) {
			if (resultCode == RESULT_OK) {
				timeStamp = data.getStringExtra(Constants.DATE);
			}
		}
	}

	public static class LineEditText extends EditText{
		private Rect 	mRect;
	    private Paint 	mPaint; 
	    
		public LineEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			
		    mRect 	= new Rect();
		    mPaint 	= new Paint();
		    
		    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		    mPaint.setColor(Color.BLUE);
		}
 
	     @Override
	     protected void onDraw(Canvas canvas) {
	         int height 		= getHeight();
	         int line_height 	= getLineHeight();
	
	         int count = height / line_height;
	
	         if (getLineCount() > count)
	             count = getLineCount();
	
	         Rect r 		= mRect;
	         Paint paint 	= mPaint;
	         
	         int baseline 	= getLineBounds(0, r);
	
	         for (int i = 0; i < count; i++) {
	             canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
	             baseline += getLineHeight();
	
	             super.onDraw(canvas);
	         }
	     }
	}
}
