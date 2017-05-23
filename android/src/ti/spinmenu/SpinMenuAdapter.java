package ti.spinmenu;

import java.util.ArrayList;

import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SpinMenuAdapter extends PagerAdapter {
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
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return false;
	}
}
