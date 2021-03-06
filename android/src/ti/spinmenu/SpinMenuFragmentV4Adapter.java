package ti.spinmenu;

import java.util.ArrayList;
import android.support.v4.view.PagerAdapter;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Author: Navid Ghahramani You can contact to me with
 * ghahramani.navid@gmail.com
 */
public class SpinMenuFragmentV4Adapter extends FragmentStatePagerAdapter
		implements ViewPagerDynamicAdapter {

	private List<SpinMenuFragment> pagerFragments = new ArrayList<SpinMenuFragment>();

	public SpinMenuFragmentV4Adapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public int addView(View view) {
		return addView(view, pagerFragments.size());
	}

	@Override
	public int addView(View view, int position) {
		pagerFragments.add(position, new SpinMenuFragment(view));
		notifyDataSetChanged();
		return position;
	}

	// -----------------------------------------------------------------------------
	// Removes the "view" at "position" from "views".
	// Retuns position of removed view.
	// The app should call this to remove pages; not used by ViewPager.
	@Override
	public int removeView(ViewPager viewPager, View view) {
		for (int i = 0; i < pagerFragments.size(); i++) {
			SpinMenuFragment fragment = pagerFragments.get(i);
			if (fragment.getContent().equals(view)) {
				return removeView(viewPager, i);
			}
		}
		return -1;
	}

	// -----------------------------------------------------------------------------
	// Removes the "view" at "position" from "views".
	// Retuns position of removed view.
	// The app should call this to remove pages; not used by ViewPager.
	@Override
	public int removeView(ViewPager viewPager, int position) {
		viewPager.setAdapter(null);
		pagerFragments.remove(position);
		viewPager.setAdapter(this);
		return position;
	}

	// -----------------------------------------------------------------------------
	// Returns the "view" at "position".
	// The app should call this to retrieve a view; not used by ViewPager.
	@Override
	public View getView(int position) {
		return pagerFragments.get(position).getContent();
	}

	/**
	 * Return the Fragment associated with a specified position.
	 */
	public Fragment getItem(int position) {
		return pagerFragments.get(position);
	}

	@Override
	public int getCount() {
		return pagerFragments.size();
	}
}
