package jb.smsmedia.iparliament.fragments;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.dtos.Message;
import jb.smsmedia.iparliament.fragments.FeedsFragment.DataTransmitterAsync;
import jb.smsmedia.iparliament.fragments.FeedsFragment.PostAsync;
import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.JSONParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class ComposeMessageFragment extends Fragment implements Connectable{
	
	JSONArray names;
	ArrayList<String> namesList;
	ArrayAdapter<String> adapter;
	Button send_btn;
	EditText message;
	AutoCompleteTextView recipient;
	String jsonString, user_id;
	Gson jparser = new Gson();
	
	public ComposeMessageFragment(String id){
		this.user_id = id;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {			
		return inflater.inflate(R.layout.messages_compose, null);
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		
		Activity activity = getActivity();
		namesList = new ArrayList<String>();
		
		if(activity != null){			
				new DataTransmitterAsync().execute();			
		}else{
			Toast.makeText(activity, "no",Toast.LENGTH_LONG).show();
		}	
		
		recipient = (AutoCompleteTextView)getView().findViewById(R.id.RECIPIENT);
		adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, namesList);
	    recipient.setAdapter(adapter);
	    send_btn = (Button)getView().findViewById(R.id.COMPOSE_BTN);
	    message = (EditText)getView().findViewById(R.id.COMPOSE_MSG);

	    send_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = message.getText().toString();
				String to = recipient.getText().toString().trim();
				int name_count = 0;
				if(msg.length() > 0 && to.length() > 0){
					for(int i = 0; i< namesList.size(); i++){
						if(to.equals(namesList.get(i))){
							Message message = new Message();
							message.setFrom_id(user_id);
							message.setTo(to);
							message.setMessage(msg);
							jsonString = jparser.toJson(message);
							new PostAsync().execute();
							
						}else{
							name_count++;
							if(name_count == namesList.size()){
								Toast.makeText(getActivity(), "That Recipient doesn't exist", Toast.LENGTH_LONG).show();
							}
						}
					}
					
					
					
				}else{
					Toast.makeText(getActivity(), "Fill in all fields", Toast.LENGTH_LONG).show();
				}
			}
		});
	    
	    
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

	    String inString, parameterPass;
	    JSONObject jobject;
	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        
	        try {
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"ntypes");
		        output = jsonStr;
	        } catch (Exception e1) {
	        	output = "error";
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String jsonStr) {
	        this.progressDialog.dismiss();
	        Activity activity = getActivity();
	    	
			try{ 
				JSONObject json = new JSONObject(jsonStr);
				names = json.getJSONArray("names");
	    		 
	    		 for(int i = 0; i < names.length(); i++){
	    			 JSONObject content = names.getJSONObject(i);
	    			 namesList.add(content.getString("name"));
	    		 }	
	    		 //Toast.makeText(activity, namesList.get(0), Toast.LENGTH_LONG).show();  
	    	 } catch (Exception e) {
	    		 Toast.makeText(activity, "error loading\n"+e.getMessage(), Toast.LENGTH_LONG).show();  
	         }
	    }

	}
	
	class PostAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
	        progressDialog.setMessage("Sending...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() {
	            public void onCancel(DialogInterface diaInterface) {
	                PostAsync.this.cancel(true);
	                diaInterface.dismiss();
	            }
	        });
	    }

	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        try{
	            JSONObject jObj =  JSONParser.postJSONtoURL(URL2, "sendmsg", jsonString);	        
	            output = jObj.getString("message");
            } catch (Exception e1) {
        	  output = "error"+e1.getMessage();
            }
	        return output;
	    }

	    protected void onPostExecute(String jsonRes) {
	        this.progressDialog.dismiss();
	        Toast.makeText(getActivity(), jsonRes, Toast.LENGTH_LONG).show();
	        
	    }

	}
	
	
}
