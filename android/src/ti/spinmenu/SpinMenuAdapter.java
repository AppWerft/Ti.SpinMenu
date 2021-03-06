package ti.spinmenu;

import java.util.ArrayList;

import org.appcelerator.titanium.proxy.TiViewProxy;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class SpinMenuAdapter extends PagerAdapter implements
		ViewPagerDynamicAdapter {
	private final ArrayList<TiViewProxy> viewProxies;
	protected final Context ctx;

	public SpinMenuAdapter(Activity activity, ArrayList<TiViewProxy> viewProxies) {
		this.ctx = activity.getBaseContext();
		this.viewProxies = viewProxies;
	}

	@Override
	public int getCount() {
		return viewProxies.size();
	}

	public Object getItem(int position) {
		return (position >= getCount()) ? null : viewProxies.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return viewProxies.get(position).getOrCreateView().getOuterView();
	}

	@Override
	public void notifyDataSetChanged() {
		// do somethings
		super.notifyDataSetChanged();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int addView(View view) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int addView(View view, int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeView(ViewPager viewPager, View view) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeView(ViewPager viewPager, int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position) {
		// TODO Auto-generated method stub
		return null;
	}
}
