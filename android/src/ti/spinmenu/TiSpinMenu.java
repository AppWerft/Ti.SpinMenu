package ti.spinmenu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hitomi.smlibrary.*;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

public class TiSpinMenu extends TiUIView implements
		OnSpinMenuStateChangeListener, OnSpinSelectedListener {

	public static final String LCAT = SpinmenuModule.LCAT;

	private ViewPagerDynamicAdapter adapter;

	private SpinMenu spinMenu;
	private final ArrayList<TiViewProxy> viewProxies;
	private ViewPager viewPager;
	private int curIndex = 0;

	/* Constructor */

	public TiSpinMenu(final TiViewProxy proxy, KrollDict properties) {
		this(proxy);
		applyProperties(properties);
	}

	public TiSpinMenu(final TiViewProxy proxy) {
		super(proxy);
		// list of all Viewproxies inside container
		viewProxies = new ArrayList<TiViewProxy>();
		// new container
		LinearLayout nativeView = new LinearLayout(proxy.getActivity());
		nativeView.setOrientation(LinearLayout.VERTICAL);
		// this container is the TiUIView
		setNativeView(nativeView);

		ViewPager.LayoutParams viewPagerLayoutParams = new ViewPager.LayoutParams();
		viewPagerLayoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
		viewPagerLayoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;

		viewPager = new ViewPager(nativeView.getContext());
		viewPager.setId(8889);

		// adding viewpager to our nativeView
		nativeView.addView(viewPager, viewPagerLayoutParams);

		FragmentActivity activity = (FragmentActivity) proxy.getActivity();

		adapter = new SpinMenuFragmentV4Adapter(
				activity.getSupportFragmentManager());

		spinMenu = new com.hitomi.smlibrary.SpinMenu(activity, new KrollDict(),
				0); // extended from
		spinMenu.setAdapter((SpinMenuAdapter) adapter);
		spinMenu.setOnSpinMenuStateChangeListener(this);
		spinMenu.setOnSpinSelectedListener(this);
	}

	public void applyProperties(KrollDict properties) {
	}

	public int getCurrentView() {
		return curIndex;
	}

	public void addView(TiViewProxy proxy) {
		if (!viewProxies.contains(proxy)) {
			proxy.setActivity(this.proxy.getActivity());
			proxy.setParent(this.proxy);
			viewProxies.add(proxy);
			getProxy().setProperty(TiC.PROPERTY_VIEWS, viewProxies.toArray());
			adapter.notifyDataSetChanged();
		}
	}

	public void removeView(TiViewProxy proxy) {
		if (viewProxies.contains(proxy)) {
			viewProxies.remove(proxy);
			proxy.setParent(null);
			getProxy().setProperty(TiC.PROPERTY_VIEWS, viewProxies.toArray());
			adapter.notifyDataSetChanged();
		}
	}

	public ArrayList<TiViewProxy> getSpinMenus() {
		return viewProxies;
	}

	public ArrayList<TiViewProxy> getViews() {
		return viewProxies;
	}

	public void setViews(Object viewsObject) {
		Log.d(LCAT, "TiSpinMenu.setViews() inside");
		boolean changed = false;
		clearViewsList();
		if (viewsObject instanceof Object[]) {
			Object[] views = (Object[]) viewsObject;
			Activity activity = this.proxy.getActivity();
			for (int i = 0; i < views.length; i++) {
				if (views[i] instanceof TiViewProxy) {
					TiViewProxy viewProxy = (TiViewProxy) views[i];
					viewProxy.setActivity(activity);
					viewProxy.setParent(this.proxy);
					viewProxies.add(viewProxy);
					changed = true;
				}
			}
		} else
			Log.w(LCAT, "setView with wrong parameters");
		if (changed) {
			adapter.notifyDataSetChanged();
		}
	}

	private void clearViewsList() {
		if (viewProxies == null || viewProxies.size() == 0) {
			return;
		}
		for (TiViewProxy viewProxy : viewProxies) {
			viewProxy.releaseViews();
			viewProxy.setParent(null);
		}
		viewProxies.clear();
	}

	@Override
	public void processProperties(KrollDict d) {
		super.processProperties(d);
		if (d.containsKey(TiC.PROPERTY_VIEWS)) {
			Log.d(LCAT, "Ti.SpinMenu.setViews ");
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

	public void onSpinMenuStateChanged(SpinMenu spinMenu, int position) {
		if (proxy.hasListeners("SpinMenuStateChanged")) {
			KrollDict kd = new KrollDict();
			kd.put("position", position);
			proxy.fireEvent("SpinMenuStateChanged", kd);
		}
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
