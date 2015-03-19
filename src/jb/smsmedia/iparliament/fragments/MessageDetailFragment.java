package jb.smsmedia.iparliament.fragments;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.dtos.Message;
import jb.smsmedia.iparliament.fragments.ComposeMessageFragment.PostAsync;
import jb.smsmedia.iparliament.fragments.GroupsDetailsFragment.DataTransmitterAsync;
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
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class MessageDetailFragment extends ListFragment implements Connectable{

	
	JSONArray msgz;
	private static final String SENDER = "sender";
	private static final String MSG = "msg";
	private static final String TIME = "time";
	private static final String STATE = "state";
	String from_id, to_id;
	String jsonString;
	ImageButton reply_btn;
	Gson jparser = new Gson();
	EditText message;
	
	public MessageDetailFragment(String from_id,  String to_id){
		this.from_id = from_id;
		this.to_id = to_id;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.msg_detaillayout, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
        Activity activity = getActivity();
		
		if(activity != null){			
				new DataTransmitterAsync().execute(); 
		}else{
			Toast.makeText(activity, "no",Toast.LENGTH_LONG).show();
		}
		
		reply_btn = (ImageButton)getView().findViewById(R.id.MD_REPLY_BTN);
	    message = (EditText)getView().findViewById(R.id.MD_REPLY_MSG);
        reply_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = message.getText().toString();
				message.setText("");
				if(msg.length() > 0){
					Message message = new Message();
					message.setFrom_id(to_id);
					message.setTo(from_id);
					message.setMessage(msg);
					jsonString = jparser.toJson(message);
					
					new PostAsync().execute();										
				}else{
					Toast.makeText(getActivity(), "Fill in all fields", Toast.LENGTH_LONG).show();
				}
				
				
			}
		});
		
		
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
			
				if(getItem(position).state.equals("i")){
				    convertView = LayoutInflater.from(getContext()).inflate(R.layout.msg_dlayout, null);
										
					ImageView userimg = (ImageView) convertView.findViewById(R.id.MSGD_PROFILE_IMAGE);
					userimg.setImageBitmap(getItem(position).profileImage);
					
					TextView sender = (TextView) convertView.findViewById(R.id.MSGD_SENDER);
					sender.setText(getItem(position).sender);
					
					TextView msg = (TextView) convertView.findViewById(R.id.MSGD);
					msg.setText(getItem(position).msg);
					
					TextView time = (TextView) convertView.findViewById(R.id.MSGD_TIME);
					time.setText(getItem(position).time);
				}else{					
					convertView = LayoutInflater.from(getContext()).inflate(R.layout.out_msg_dlayout, null);
					
					ImageView userimg = (ImageView) convertView.findViewById(R.id.OMSGD_PROFILE_IMAGE);
					userimg.setImageBitmap(getItem(position).profileImage);
					
					TextView sender = (TextView) convertView.findViewById(R.id.OMSGD_SENDER);
					sender.setText(getItem(position).sender);
					
					TextView msg = (TextView) convertView.findViewById(R.id.OMSGD);
					msg.setText(getItem(position).msg);
					
					TextView time = (TextView) convertView.findViewById(R.id.OMSGD_TIME);
					time.setText(getItem(position).time);
				}			
			
			}

			return convertView;
		}

	}
	
	class DataTransmitterAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
//	        progressDialog.setMessage("Loading Conversation...");
//	        progressDialog.show();
//	        progressDialog.setOnCancelListener(new OnCancelListener() {
//	            public void onCancel(DialogInterface diaInterface) {
//	                DataTransmitterAsync.this.cancel(true);
//	                diaInterface.dismiss();
//	            }
//	        });
	    }

	    JSONObject jobject;
	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        
	        try {
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"gmsgthrd&from_id="+from_id+"&to_id="+to_id);
		        output = jsonStr;
	        } catch (Exception e1) {
	        	output = "error"+e1.getMessage();
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String jsonStr) {
	       // this.progressDialog.dismiss();
	        Activity activity = getActivity();
	       
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
	    		 Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
	         }
			
			setListAdapter(adapter);
	    }
	}
	
	class PostAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
//	        progressDialog.setMessage("Sending...");
//	        progressDialog.show();
//	        progressDialog.setOnCancelListener(new OnCancelListener() {
//	            public void onCancel(DialogInterface diaInterface) {
//	                PostAsync.this.cancel(true);
//	                diaInterface.dismiss();
//	            }
//	        });
	    }

	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        try{
	            JSONObject jObj =  JSONParser.postJSONtoURL(URL2, "replymsg", jsonString);	        
	            output = jObj.getString("message");
            } catch (Exception e1) {
        	  output = "error"+e1.getMessage();
            }
	        return output;
	    }

	    protected void onPostExecute(String jsonRes) {
	       // this.progressDialog.dismiss();
	        new DataTransmitterAsync().execute();
	       // Toast.makeText(getActivity(), jsonRes, Toast.LENGTH_LONG).show();
	        
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
					//Toast.makeText(getActivity(), "string is not de", Toast.LENGTH_LONG).show();
					result = null;// nBitmapFactory.decodeResource(getResources(), R.drawable.f2);
				}
				break;		
		}
				
		return result;
	}
}
