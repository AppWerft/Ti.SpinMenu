package ti.spinmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SpinMenuFragment extends Fragment {

	protected View content;

	public SpinMenuFragment(View view) {
		super();
		this.content = view;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		FrameLayout layout = new FrameLayout(getActivity());
		layout.setLayoutParams(params);
		layout.addView(content);
		return layout;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (content.getParent() != null) {
			((ViewGroup) content.getParent()).removeView(content);
		}

	}

	public View getContent() {
		return content;
	}

	public void setContent(View content) {
		this.content = content;
	}

}
