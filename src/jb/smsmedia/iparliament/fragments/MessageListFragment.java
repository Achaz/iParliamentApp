package jb.smsmedia.iparliament.fragments;

import org.json.JSONArray;
import org.json.JSONObject;
import jb.smsmedia.iparliament.CentralActivity;
import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.JSONParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

@SuppressLint("ValidFragment")
public class MessageListFragment extends ListFragment implements Connectable {

	
	JSONArray msgz;
	private static final String SENDER = "from";
	private static final String MSG = "message";
	private static final String TIME = "time";
	private static final String STATE = "status";
	String jsonString, user_id;
	
	public MessageListFragment(String id){
		this.user_id = id;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.messages_read, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        
		Activity activity = getActivity();
		
		if(activity != null){			
				new DataTransmitterAsync().execute(); 
		}else{
			Toast.makeText(activity, "no",Toast.LENGTH_LONG).show();
		}
	}

	private class MessageBody {
		public String sender;
		public String time;
		public String msg;
		public String state;
		public Bitmap profileImage;
		public MessageBody(String sender, String msg, String time, String state, Bitmap profileImage) {
			this.sender = sender; 
			this.msg = msg;
			this.time = time;
			this.state = state;
			this.profileImage = profileImage;
		}
	}

	public class MessageAdapter extends ArrayAdapter<MessageBody> {

		public MessageAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.msglayout, null);
			}
			
			ImageView userimg = (ImageView) convertView.findViewById(R.id.MSG_PROFILE_IMAGE);
			userimg.setImageBitmap(getItem(position).profileImage);
			
			if(getItem(position).state.equals("u")){
				ImageView ic = (ImageView) convertView.findViewById(R.id.MSG_ICON);
				ic.setImageResource(R.drawable.msgblack);	
			}else{
				ImageView ic = (ImageView) convertView.findViewById(R.id.MSG_ICON);
				ic.setImageResource(R.drawable.r_msgblack);
			}
			
			
			TextView sender = (TextView) convertView.findViewById(R.id.SENDER);
			sender.setText(getItem(position).sender);
			
			TextView msg = (TextView) convertView.findViewById(R.id.MSG);
			msg.setText(getItem(position).msg);
			
			TextView time = (TextView) convertView.findViewById(R.id.MSG_TIME);
			time.setText(getItem(position).time);

			return convertView;
		}

	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		try{
			JSONObject json = new JSONObject(jsonString);
			msgz = json.getJSONArray("msgs");
			JSONObject feed = msgz.getJSONObject(position);
			String s = feed.getString("msg");
			JSONObject content = new JSONObject(s);
			newContent = new MessageDetailFragment(content.getString("from_id"), content.getString("to_id"));
			switchFragment(newContent);
		} catch (Exception e) {
			 Toast.makeText(getActivity(), "error loading\n"+e.getMessage(), Toast.LENGTH_LONG).show();  
	    }
		
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
	
	class DataTransmitterAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
	        progressDialog.setMessage("Loading...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() {
	            public void onCancel(DialogInterface diaInterface) {
	                DataTransmitterAsync.this.cancel(true);
	                diaInterface.dismiss();
	            }
	        });
	    }

	    JSONObject jobject;
	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        
	        try {
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"gmsg&id="+user_id);
		        output = jsonStr;
	        } catch (Exception e1) {
	        	output = "error"+e1.getMessage();
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String jsonStr) {
	        this.progressDialog.dismiss();
	        Activity activity = getActivity();
	        jsonString = jsonStr;
	        MessageAdapter adapter = new MessageAdapter(getActivity());
	        
			try{ 
				JSONObject json = new JSONObject(jsonStr);
				msgz = json.getJSONArray("msgs");
	    		 
	    		 for(int i = 0; i < msgz.length(); i++){
	    			 JSONObject msg = msgz.getJSONObject(i);
	    			 String s = msg.getString("msg");
	    			 JSONObject content = new JSONObject(s);     				
	    			 
                         if(content.getString("to_prof_pic").length() > 1 || content.getString("from_prof_pic").length() > 1){
	    				 	 adapter.add(new MessageBody(content.getString(SENDER), content.getString(MSG), content.getString(TIME), content.getString(STATE), getBitmap(content.getString("from_prof_pic"), 0))); 
	    			     }else{
	    				     Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.f2);
	    				     adapter.add(new MessageBody(content.getString(SENDER), content.getString(MSG), content.getString(TIME), content.getString(STATE), bmp));
	    			     }
	    		 }	
	    		 
	    	 } catch (Exception e) {
	    		 Toast.makeText(activity, "error loading\n"+e.getMessage(), Toast.LENGTH_LONG).show();  
	         }
			setListAdapter(adapter);
	    }
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
