package jb.smsmedia.iparliament.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.dtos.Invitation;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class UnansweredInvitationsFragment extends ListFragment implements Connectable {

	JSONArray invitations;
	JSONObject toPopUp;
	String jsonString, user_id;
	private static final String TITLE = "title";
	private static final String VENUE = "venue";
	private static final String DATE = "date";
	private static final String TIME = "time";
	private static final String MSG = "message";
	private static final String DETAIL_MSG = "detailed_msg";
	
	public UnansweredInvitationsFragment(String id){
		this.user_id = id;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_invitations_unanswered, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
        Activity activity = getActivity();
		
		if(activity != null){			
				new DataTransmitterAsync().execute(); 
		}else{
			//Toast.makeText(activity, "no",Toast.LENGTH_LONG).show();
		}
	}

	private class InvitationBody {
		public String invitation;
		public int iconRes;
		public InvitationBody(String invitation, int iconRes) {
			this.invitation = invitation; 
			this.iconRes = iconRes;
		}
	}

	public class InvitationAdapter extends ArrayAdapter<InvitationBody> {

		public InvitationAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.invitationlayout, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.inv_icon);
			icon.setImageResource(getItem(position).iconRes);
			
			TextView title = (TextView) convertView.findViewById(R.id.inv_title);
			title.setText(getItem(position).invitation);
			

			return convertView;
		}

	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Invitation inv = new Invitation();
		try{
				JSONObject json = new JSONObject(jsonString);
				invitations = json.getJSONArray("invitations");
	    		JSONObject invitation = invitations.getJSONObject(position);
	    		String s = invitation.getString("invitation");
	    		toPopUp = new JSONObject(s);	    		 	
	            
	    		inv.setId(toPopUp.getString("id"));
				inv.setTitle(toPopUp.getString(TITLE));
				inv.setMsg(toPopUp.getString(MSG));
				inv.setTime(toPopUp.getString(TIME));
				inv.setVenue(toPopUp.getString(VENUE));
				inv.setDate(toPopUp.getString(DATE));
				inv.setDetail_msg(toPopUp.getString(DETAIL_MSG));
			
			DialogFragment df = new InvitationDetailFragment(inv, user_id);	
			df.show(getFragmentManager(), "");
		}catch(JSONException e){
			Toast.makeText(getActivity(), "error loading pop up\n"+e.getMessage(), Toast.LENGTH_LONG).show();
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
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"ginvs&id="+user_id);
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
	        //Toast.makeText(activity, jsonStr, Toast.LENGTH_LONG).show();
	        InvitationAdapter adapter = new InvitationAdapter(getActivity());
	        
			try{ 
				JSONObject json = new JSONObject(jsonStr);
				invitations = json.getJSONArray("invitations");
	    		 
	    		 for(int i = 0; i < invitations.length(); i++){
	    			 JSONObject inv = invitations.getJSONObject(i);
	    			 String s = inv.getString("invitation");
	    			 JSONObject content = new JSONObject(s);
	    			 adapter.add(new InvitationBody(content.getString(TITLE),R.drawable.invite));
	    		 }	
	    		 
	    	 } catch (Exception e) {
	    		 Toast.makeText(activity, "no invitations for you", Toast.LENGTH_LONG).show();
		         
	         }
			setListAdapter(adapter);
	    }
	}
	
}
