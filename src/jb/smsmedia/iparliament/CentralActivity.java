package jb.smsmedia.iparliament;

import jb.smsmedia.iparliament.BaseActivity;
import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.fragments.FeedsFragment;
import jb.smsmedia.iparliament.fragments.MessageFragment;
import jb.smsmedia.iparliament.fragments.NotificationFragment;
import jb.smsmedia.iparliament.fragments.OptionsListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class CentralActivity extends BaseActivity{
	
	private Fragment mContent;
	String auth_id, uname;
	
	
	public CentralActivity() {
		super(R.string.changing_fragments);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent in = getIntent();
		auth_id = in.getStringExtra("auth_id");
		uname = in.getStringExtra("uname");
		// set the Above View
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new FeedsFragment(auth_id);	
		

		
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContent)
		.commit();
		
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, new OptionsListFragment(auth_id, uname))
		.commit();
		
		// customize the SlidingMenu
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}
	
	public String getAuth_id() {
		return auth_id;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ab_messages:
			mContent = new MessageFragment(auth_id);
			switchContent(mContent);
			
			return true;
		case R.id.ab_notifications:
			Fragment newContent = null;
			newContent = new NotificationFragment();
			switchContent(newContent);
			return true;
		
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	

}
