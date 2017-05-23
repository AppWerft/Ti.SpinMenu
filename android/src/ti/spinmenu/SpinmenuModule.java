package ti.spinmenu;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;

@Kroll.module(name = "Spinmenu", id = "ti.spinmenu")
public class SpinmenuModule extends KrollModule {

	public static final String LCAT = "TiSpinMenu";

	public SpinmenuModule() {
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {
		Log.d(LCAT, "inside onAppCreate");
	}

}
