package edu.ucsb.cs.cs185.lauren05.beproud;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

@SuppressLint("SimpleDateFormat")
public class EntryActivity extends SherlockFragmentActivity {	
	// Order: Mental, Physical, Financial, Educational, Altruistic
	private boolean[] categories = { false, false, false, false, false };
	
	private String timeStamp;
	
	private EditText editText;
	private Calendar curCalendar;
	Spinner spinner;
	Spinner spinner2;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
					
		editText 	= (EditText) findViewById(R.id.body);
		spinner 	= (Spinner) findViewById(R.id.spinner1);
		spinner2 	= (Spinner) findViewById(R.id.spinner2);
		
		spinner		.setVisibility(View.GONE);
		spinner2	.setVisibility(View.GONE);
		
		curCalendar = Calendar.getInstance();
		
		List<String> list = new ArrayList<String>();
		list.add(Constants.MENTAL);
		list.add(Constants.PHYSICAL);
		list.add(Constants.ALTRUISTIC);
		list.add(Constants.FINANCIAL);
		list.add(Constants.EDUCATIONAL);
		
		List<String> list2 = new ArrayList<String>();
		list2.add("06/07/2013");
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner.setAdapter(dataAdapter);
		spinner2.setAdapter(dataAdapter2);
		
		// User is editing their entry -- populate data.
		if (getIntent().getExtras() != null) {
			Bundle bundle = getIntent().getExtras();
			
			editText.setText(bundle.getString(Constants.ENTRY));
			
			curCalendar.set(Calendar.YEAR, 			bundle.getInt(Constants.YEAR));
			curCalendar.set(Calendar.MONTH, 		bundle.getInt(Constants.MONTH));
			curCalendar.set(Calendar.DAY_OF_MONTH, 	bundle.getInt(Constants.DAY));
			
			categories[0] = bundle.getBoolean(Constants.MENTAL);
			categories[1] = bundle.getBoolean(Constants.PHYSICAL);
			categories[2] = bundle.getBoolean(Constants.FINANCIAL);
			categories[3] = bundle.getBoolean(Constants.EDUCATIONAL);
			categories[4] = bundle.getBoolean(Constants.ALTRUISTIC);
		}
		
		timeStamp = new SimpleDateFormat(Constants.DATE_FORMAT).format(curCalendar.getTime());			
		this.getSupportActionBar().setTitle(timeStamp);	
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		super.getSupportMenuInflater().inflate(R.menu.menu_entry, menu);
        return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {		
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.category:
			showCategoriesDialog();	        
			return true;
		case R.id.month:
			showCalendarDialog();				
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void saveEntry(View v){			
		// Use regexes for error handling -- we don't want whitespace as an Accomplishment Entry
		String pattern = "(\\s+)";

		if (editText.getText() != null && !editText.getText().toString().matches(pattern)) {
			Intent result = getIntent();
			
			// Add date and entry info
			result.putExtra(Constants.ENTRY, 		editText.getText().toString());
			
			result.putExtra(Constants.YEAR, 		curCalendar.get(Calendar.YEAR));
			result.putExtra(Constants.MONTH, 		curCalendar.get(Calendar.MONTH));
			result.putExtra(Constants.DAY, 			curCalendar.get(Calendar.DAY_OF_MONTH));
			
			// Add category info
			result.putExtra(Constants.MENTAL, 		categories[0]);
			result.putExtra(Constants.PHYSICAL, 	categories[1]);
			result.putExtra(Constants.FINANCIAL, 	categories[2]);
			result.putExtra(Constants.EDUCATIONAL, 	categories[3]);
			result.putExtra(Constants.ALTRUISTIC, 	categories[4]);
									
			setResult(RESULT_OK, result);
			finish();
		}
		else
			Toast.makeText(this.getApplicationContext(), "Oops! It seems you have not entered anything. Please enter an Accomplishment.", Toast.LENGTH_LONG).show();	
	}
	
	private void showCategoriesDialog() {
//		ListAdapter adapter = new CategoryAdapter( this, categoyObjs );
		
		AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
		
	    builder.setTitle(R.string.choose_categories);
	    builder.setMultiChoiceItems(Constants.CATEGORIES, categories, new DialogInterface.OnMultiChoiceClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
	    		categories[which] = isChecked;
	    	}
	    });
	    
	    builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
	    	@Override
		    public void onClick(DialogInterface dialog, int id) {
	    		dialog.dismiss();
		    }
		});
	    
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
		    public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
		    }
		});
	    		
	    builder.create().show();
	}
	
	private void showCalendarDialog() {
		int year 	= curCalendar.get(Calendar.YEAR);
		int month 	= curCalendar.get(Calendar.MONTH);
		int day 	= curCalendar.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {								
				curCalendar.set(Calendar.YEAR, year);
				curCalendar.set(Calendar.MONTH, monthOfYear);
				curCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				
				timeStamp = new SimpleDateFormat("MM/dd/yyyy").format(curCalendar.getTime());
				getSupportActionBar().setTitle(timeStamp);
			}
		}, year, month, day);
		
		datePickerDialog.show();
	}

	public static class LineEditText extends EditText {
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
