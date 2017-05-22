package ti.spinmenu;

import java.util.ArrayList;

import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SpinMenuAdapter extends PageAdapter {
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

	@Override
	public Object getItem(int position) {
		return (position >= getCount()) ? null : viewProxies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return viewProxies.get(position).getOrCreateView().getOuterView();
	}
}
