package edu.ucsb.cs.cs185.lauren05.beproud;

import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

@SuppressLint("DrawAllocation")
public class ChartTab extends SherlockFragment {
	
	MainActivity mainActivity;
	ChartView graphView;
	
	@Override
    public SherlockFragmentActivity getSherlockActivity() {
        return super.getSherlockActivity();
    }
 
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.activity_chart, container, false);
        
        mainActivity = (MainActivity)getActivity();

		LinearLayout ll = (LinearLayout) view.findViewById(R.id.chart_view);
		graphView = new ChartView(getActivity());
		ll.addView(graphView);
		notifyDataSetChanged();
		
        return view;
    }
 
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }
	
	private float[] calculateData(float[] data) {
		float total = 0;
		
		for (int i = 0; i < data.length; i++) {
			total += data[i];
		}
		
		for (int i = 0; i < data.length; i++) {
			if (total<=0) data[i] = 360/5;
			else data[i] = 360 * (data[i] / total);
		}
		
		return data;
	}
	
	public class ChartView extends View {
		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		public float[] value_degree;
		RectF rectf = new RectF(120, 120, 380, 380);

		public ChartView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Log.v("Dash", "on draw");
			
			float startAngle = 0;			
			for (int i = 0; i < value_degree.length; i++) {
				paint.setColor(getResources().getColor(Constants.COLORS[i]));
				
				canvas.drawArc(rectf, startAngle, value_degree[i], true, paint);

				startAngle += value_degree[i];
			}
		}  
	}
	
	public class CustomComparator implements Comparator<Entry> {
	    @Override
	    public int compare(Entry o1, Entry o2) {
	        return -1*o1.entryDate.compareTo(o2.entryDate);
	    }
	}
	public void sortList() {
		Collections.sort(mainActivity.list, new CustomComparator());
	}

	public void notifyDataSetChanged() {

		if (mainActivity==null) {
			Log.v("Dash", "main activity null");
			return;
		}
		
        float vals[] = {0,0,0,0,0};
        for (Entry e : mainActivity.list) {
        	if (e.isAltruistic) vals[4]++;
        	if (e.isEducational) vals[3]++;
        	if (e.isFinancial) vals[2]++;
        	if (e.isMental) vals[0]++;
        	if (e.isPhysical) vals[1]++;
        }
		graphView.value_degree = calculateData(vals);
		graphView.invalidate();
	}

}