package edu.ucsb.cs.cs185.lauren05.beproud;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class ViewPagerAdapter extends FragmentPagerAdapter {
 
    // Declare the number of ViewPager pages
    final int PAGE_COUNT = 3;
    ListTab listTab;
    ChartTab chartTab;
    CalendarTab calTab;
 
    @Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		
		listTab.notifyDataSetChanged();
		chartTab.notifyDataSetChanged();
		calTab.notifyDataSetChanged();
	}

	public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    	listTab = new ListTab();
    	chartTab = new ChartTab();
    	calTab = new CalendarTab();
    }
 
    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
 
        // Open FragmentTab1.java
        case 0:
            return listTab;
 
        // Open FragmentTab2.java
        case 1:
        	return chartTab;
 
        // Open FragmentTab3.java
        case 2:
            return calTab;
        }
        return null;
    }
 
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return PAGE_COUNT;
    }
 
}