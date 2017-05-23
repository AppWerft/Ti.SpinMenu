package ti.spinmenu;

import java.util.ArrayList;

import android.app.Activity;

import com.hitomi.smlibrary.*;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

public class TiSpinMenu extends TiUIView implements
		OnSpinMenuStateChangeListener, OnSpinSelectedListener {
	public static final String LCAT = SpinmenuModule.LCAT;

	private SpinMenu spinMenu;
	private final ArrayList<TiViewProxy> views;
	private final SpinMenuAdapter adapter;
	private int curIndex = 0;
	private TiViewProxy proxy;

	public TiSpinMenu(TiViewProxy proxy) {
		super(proxy);
		this.proxy = proxy;
		Activity activity = proxy.getActivity();
		views = new ArrayList<TiViewProxy>();
		adapter = new SpinMenuAdapter(activity, views);
		spinMenu = new SpinMenu(activity);
		spinMenu.setFragmentAdapter(adapter);

		// spinMenu.setOnFlipListener(this);

	}

	public ArrayList<TiViewProxy> getViews() {
		return views;
	}

	public void setViews(Object viewsObject) {
		boolean changed = false;
		clearViewsList();
		if (viewsObject instanceof Object[]) {
			Object[] views = (Object[]) viewsObject;
			Activity activity = this.proxy.getActivity();
			for (int i = 0; i < views.length; i++) {
				if (views[i] instanceof TiViewProxy) {
					TiViewProxy tv = (TiViewProxy) views[i];
					tv.setActivity(activity);
					tv.setParent(this.proxy);
					// views.add(tv); // TODO
					changed = true;
				}
			}
		}
		if (changed) {
			adapter.notifyDataSetChanged();
		}
	}

	private void clearViewsList() {
		if (views == null || views.size() == 0) {
			return;
		}
		for (TiViewProxy viewProxy : views) {
			viewProxy.releaseViews();
			viewProxy.setParent(null);
		}
		views.clear();
	}

	@Override
	public void processProperties(KrollDict d) {
		super.processProperties(d);
		if (d.containsKey(TiC.PROPERTY_VIEWS)) {
			setViews(d.get(TiC.PROPERTY_VIEWS));
		}
		if (d.containsKey(TiC.PROPERTY_CURRENT_PAGE)) {
			int page = TiConvert.toInt(d, TiC.PROPERTY_CURRENT_PAGE);
			if (page > 0) {
				setCurrentPage(page);
			}
		}
		setNativeView(spinMenu);
		super.processProperties(d);
	}

	public void setCurrentPage(Object view) {
		/*
		 * if (view instanceof Number) { move(((Number) view).intValue(),
		 * false); } else if (Log.isDebugModeEnabled()) { Log.w(TAG,
		 * "Request to set current page is ignored, as it is not a number.",
		 * Log.DEBUG_MODE); }
		 */
	}

	@Override
	public void onMenuOpened() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMenuClosed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpinSelected(int position) {
		if (proxy.hasListeners("spinselected")) {
			KrollDict kd = new KrollDict();
			kd.put("position", position);
			proxy.fireEvent("spinselected", kd);
		}

	}

}
