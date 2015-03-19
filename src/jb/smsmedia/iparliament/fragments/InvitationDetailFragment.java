package jb.smsmedia.iparliament.fragments;

import org.json.JSONObject;
import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.dtos.Invitation;
import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.JSONParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class InvitationDetailFragment extends DialogFragment implements Connectable{
	Invitation content;
	String user_id, inv_id, status;
	public InvitationDetailFragment(Invitation invitation, String user_id){
		this.content = invitation;
		this.user_id = user_id;
		this.inv_id = invitation.getId();
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("Invitation Detail");
		return inflater.inflate(R.layout.invitation_detaillayout, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	     
		TextView title = (TextView)getView().findViewById(R.id.EVT_NAME);
		title.setText(content.getTitle());
		TextView venue = (TextView)getView().findViewById(R.id.EVT_VENUE);
		venue.setText(content.getVenue());
		TextView date = (TextView)getView().findViewById(R.id.EVT_DATE);
		date.setText(content.getDate());
		TextView time = (TextView)getView().findViewById(R.id.EVT_TIME);
		time.setText(content.getTime());
		TextView msg = (TextView)getView().findViewById(R.id.EVT_MSG);
		msg.setText(content.getMsg());
		TextView detail_msg = (TextView)getView().findViewById(R.id.EVT_DETAIL_MSG);
		detail_msg.setText(content.getDetail_msg());
		
		Button go = (Button)getView().findViewById(R.id.INV_GO_BTN);
		Button no = (Button)getView().findViewById(R.id.INV_NO_BTN);
		
		go.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				status = "Going";
				new DataTransmitterAsync().execute();				
			}
		});
		
		no.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				status = "NotGoing";
				new DataTransmitterAsync().execute();
			}
		});
			
	}
	
	class DataTransmitterAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
	        progressDialog.setMessage("Scheduling...");
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
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"sevent&user_id="+user_id+"&inv_id="+inv_id+"&status="+status);
		        JSONObject res = new JSONObject(jsonStr);
		        output = res.getString("message");
	        } catch (Exception e1) {
	        	output = "error"+e1.getMessage();
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String res) {
	        this.progressDialog.dismiss();
	        Toast.makeText(activity, res, Toast.LENGTH_LONG).show();
	        InvitationDetailFragment.this.dismiss();
	         
	    }
	}

}
