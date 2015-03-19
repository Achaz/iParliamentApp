package jb.smsmedia.iparliament.fragments;

import jb.smsmedia.iparliament.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;


@SuppressLint("ValidFragment")
public class InvitationsFragment extends Fragment  implements OnTabChangeListener {
	
	private static final String TAG = "FragmentTabs";

	private View mRoot;
	private TabHost mTabHost;
	private int mCurrentTab;
	ViewPager mViewPager;
    ImageView x;
    String user_id;
    
    public InvitationsFragment(String id){
		this.user_id = id;
	}
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		mRoot = inflater.inflate(R.layout.activity_inv_holder, null);
		mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
		setupTabs();
		return mRoot;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(mCurrentTab);
		// manually start loading stuff in the first tab
		updateTab("1");
	}

	private void setupTabs() {
		mTabHost.setup(); // important!
		mTabHost.addTab(newTab("1", R.drawable.invite, R.id.invtab_1));
		mTabHost.addTab(newTab("2", R.drawable.attending, R.id.invtab_2));
	}

	private TabSpec newTab(String tag, int labelId, int tabContentId) {
		Log.d(TAG, "buildTab(): tag=" + tag);

		View indicator = LayoutInflater.from(getActivity()).inflate(
				R.layout.msgtab,
				(ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
		((ImageView) indicator.findViewById(R.id.img)).setImageResource(labelId);

		TabSpec tabSpec = mTabHost.newTabSpec(tag);
		tabSpec.setIndicator(indicator);
		tabSpec.setContent(tabContentId);
		return tabSpec;
	}

	@Override
	public void onTabChanged(String tabId) {
		updateTab(tabId);
		mCurrentTab = Integer.valueOf(tabId) - 1;
	}

	private void updateTab(String tabId) {
		FragmentManager fm = getFragmentManager();
		
		if (fm.findFragmentByTag(tabId) == null) {
			if (tabId.equals("1"))
				 fm.beginTransaction().replace(R.id.invtab_1, new UnansweredInvitationsFragment(user_id), tabId).commit();
			else if (tabId.equals("2"))
				fm.beginTransaction().replace(R.id.invtab_2, new AttendingInvitationsFragment(user_id), tabId).commit();
		}else{
			if (tabId.equals("1"))
				 fm.beginTransaction().replace(R.id.invtab_1, new UnansweredInvitationsFragment(user_id), tabId).commit();
		else if (tabId.equals("2"))
				fm.beginTransaction().replace(R.id.invtab_2, new AttendingInvitationsFragment(user_id), tabId).commit();

		}
	}
	
	
	
}
