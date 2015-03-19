package jb.smsmedia.iparliament.fragments;

import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.CentralActivity;
import jb.smsmedia.iparliament.SignInActivity;
import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.PreferenceConnector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class OptionsListFragment extends ListFragment implements Connectable{

	String user_id, uname;
	TextView name;
	
	public OptionsListFragment(String id, String name){
		this.user_id = id;
		this.uname = name;
	}
	
	public OptionsListFragment(){
		//this.user_id = id;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		name = (TextView)getView().findViewById(R.id.UNAME);
		name.setText(uname);
		
		OptionAdapter adapter = new OptionAdapter(getActivity());
		adapter.add(new OptionItem("News Feed", R.drawable.feed));
		adapter.add(new OptionItem("Messages", R.drawable.ic_action_mail));
		adapter.add(new OptionItem("Your Topics", R.drawable.topics));
		adapter.add(new OptionItem("Your Groups", R.drawable.group));
		adapter.add(new OptionItem("Inivitations", R.drawable.invite));
		adapter.add(new OptionItem("Settings", R.drawable.settings));
		adapter.add(new OptionItem("Logout", R.drawable.logout));
		setListAdapter(adapter);
	}

	private class OptionItem {
		public String tag;
		public int iconRes;
		public OptionItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	public class OptionAdapter extends ArrayAdapter<OptionItem> {

		public OptionAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {
		case 0:
			newContent = new FeedsFragment(user_id);			
			break;
		case 1:
			newContent = new MessageFragment(user_id);
			break;
		case 2:
			newContent = new TopicsFragment(user_id);
			break;
		case 3:
			newContent = new GroupsFragment(user_id);
			break;
		case 4:
			newContent = new InvitationsFragment(user_id);
			break;	
		case 5:
			newContent = new SettingsFragment(user_id);
			break;
		case 6:
			PreferenceConnector.deleteString(getActivity(), PreferenceConnector.USERNAME);
			PreferenceConnector.deleteString(getActivity(), PreferenceConnector.PASSWORD);
			startActivity(new Intent(getActivity(), SignInActivity.class));
			//getActivity().finish();
			break;	
		}
		
		if (newContent != null)
			switchFragment(newContent);
	}
	
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof CentralActivity) {
			CentralActivity fca = (CentralActivity) getActivity();
			fca.switchContent(fragment);
		}else{
			
		}
	}
}
