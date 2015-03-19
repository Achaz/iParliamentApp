package jb.smsmedia.iparliament.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import jb.smsmedia.iparliament.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationFragment extends ListFragment {

	ArrayList<HashMap<String, String>> postList;
	JSONArray notifications;
	private static final String SENDER = "sender";
	private static final String post = "post";
	private static final String TIME = "time";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_notifications, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String jsonStr = "{ 'posts' :" +
				"                  [  { 'sender' : 'Muheirwe Julian', 'post' : 'My post may look long but it aint as you read it', 'time' : '30mins ago' }, { 'sender' : 'Joshua Kusasira', 'post' : 'Look this long post, it even bores as you read it when it starts going on and on', 'time' : '7hrs ago' }," +
				"                     { 'sender' : 'Invitation', 'post' : 'Hilton Launch Party', 'time' : '2hrs ago' }, { 'sender' : 'Nambaale Raymond', 'post' : 'Look at those long posts, it even bores as you read them, they just start going on and on', 'time' : '9hrs ago' }," +
				"                     { 'sender' : 'Temangalo Group', 'post' : 'Mbabasi taken to jail from fraud with NSSF', 'time' : '5hrs ago' }, { 'sender' : 'Mussimenta', 'post' : 'Look at this long post, it even bores as you read it when it starts going on and on', 'time' : 'yesterday' }" +
				"                  ]" +
				"         }";
		
		NotificationsAdapter adapter = new NotificationsAdapter(getActivity());
		
		try{ 
			JSONObject json = new JSONObject(jsonStr);
			notifications = json.getJSONArray("posts");
    		 
    		 for(int i = 0; i < notifications.length(); i++){
    			 JSONObject content = notifications.getJSONObject(i);    				
    			 adapter.add(new NotificationsBody(content.getString(SENDER), content.getString(post), content.getString(TIME)));
    		 }	
    		 
    	 } catch (Exception e) {
             
         }
		
		
		setListAdapter(adapter);
	}

	private class NotificationsBody {
		public String sender;
		public String time;
		public String post;
		
		public NotificationsBody(String sender, String post, String time) {
			this.sender = sender; 
			this.post = post;
			this.time = time;
		}
	}

	public class NotificationsAdapter extends ArrayAdapter<NotificationsBody> {

		public NotificationsAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.notificationlayout, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.NOTIFICATION_PROFILE_IMAGE);
			icon.setImageResource(R.drawable.f2);
			
			TextView sender = (TextView) convertView.findViewById(R.id.NOTIFICATION_SENDER);
			sender.setText(getItem(position).sender);
			
			TextView post = (TextView) convertView.findViewById(R.id.NOTY_COMMENT);
			post.setText(getItem(position).post);
			
			TextView time = (TextView) convertView.findViewById(R.id.NOTIFICATION_TIME);
			time.setText(getItem(position).time);

			return convertView;
		}

	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		
		
	}
	
	private Bitmap getBitmap(String encodedString, int imgtype){
		Bitmap result = null;
		
		switch(imgtype){
			case 0:
				if(encodedString.length() > 1){
					byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
					result = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);	
				}else{
					result = BitmapFactory.decodeResource(getResources(), R.drawable.f2);
				}
				break;
			case 1:
				
				if(encodedString.length() > 1){
					byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
					result = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);	
					
				}else{
					Toast.makeText(getActivity(), "string is not de", Toast.LENGTH_LONG).show();
					result = null;// nBitmapFactory.decodeResource(getResources(), R.drawable.f2);
				}
				break;		
		}
				
		return result;
	}
}
