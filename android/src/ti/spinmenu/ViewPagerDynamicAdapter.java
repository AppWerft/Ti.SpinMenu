package ti.spinmenu;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Author: Navid Ghahramani You can contact to me with
 * ghahramani.navid@gmail.com
 */
public interface ViewPagerDynamicAdapter {
	public int addView(View view);

	public void notifyDataSetChanged();

	public int addView(View view, int position);

	public int removeView(ViewPager viewPager, View view);

	public int removeView(ViewPager viewPager, int position);

	public View getView(int position);

	public int getCount();

}