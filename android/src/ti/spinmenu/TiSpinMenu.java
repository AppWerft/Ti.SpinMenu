package ti.spinmenu;

import java.util.ArrayList;

import android.app.Activity;

import com.hitomi.smlibrary.*;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;

public class TiSpinMenu extends TiUIView {
	public static final String LCAT = SpinmenuModule.LCAT;

	private SpinMenu spinMenu;
	private final ArrayList<TiViewProxy> views;
	private final SpinMenuAdapter adapter;
	private int mCurIndex = 0;

	public TiSpinMenu(TiViewProxy proxy) {
		super(proxy);
		LayoutArrangement arrangement = LayoutArrangement.DEFAULT;
		Activity activity = proxy.getActivity();
		views = new ArrayList<TiViewProxy>();
		adapter = new SpinMenuAdapter(activity, views);
		spinMenu = new SpinMenu(activity);
		spinMenu.setFragmentAdapter(adapter);
		;
		// spinMenu.setOnFlipListener(this);

	}

	@Override
	public void processProperties(KrollDict d) {
		super.processProperties(d);
	}

}
