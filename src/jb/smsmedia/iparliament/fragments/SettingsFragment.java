package jb.smsmedia.iparliament.fragments;

import org.json.JSONObject;
import com.google.gson.Gson;
import jb.smsmedia.iparliament.CameraActivity;
import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.dtos.Settings;
import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.JSONParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class SettingsFragment extends Fragment implements Connectable{
	
    Button save;
    String jsonString, user_id;
    Button launchCamera;
	Spinner status;
	CheckBox pub, pri, mgm, followers;
	Gson jparser = new Gson();
	Uri imageUri;
	
	public SettingsFragment(String user_id){
		this.user_id = user_id;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_settings, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		launchCamera = (Button)getView().findViewById(R.id.GET_IMAGE_BTN);
	    launchCamera.setOnClickListener(imglist);
	    
	    save = (Button)getView().findViewById(R.id.SET_SAVE_BTN);
	    save.setOnClickListener(savelist);
	    
	    status = (Spinner)getView().findViewById(R.id.CURR_STATUS);
	    pub = (CheckBox)getView().findViewById(R.id.SET_PUBLIC);
	    pri = (CheckBox)getView().findViewById(R.id.SET_PRIVATE);
	    mgm = (CheckBox)getView().findViewById(R.id.SET_MEMZ);
	    followers = (CheckBox)getView().findViewById(R.id.SET_FOLLOWERS);
	  						
	}
	
    View.OnClickListener savelist = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
	        String currstatus = (String)status.getSelectedItem();
	        String publik, privet, grpmems, flwrz;
	        if(pub.isChecked()){
	        	publik = "1";
	        }else{
	        	publik = "0";
	        }
	        
	        if(pri.isChecked()){
	        	privet = "1";
	        }else{
	        	privet = "0";
	        }
	        
	        if(mgm.isChecked()){
	        	grpmems = "1";
	        }else{
	        	grpmems = "0";
	        }
	        
	        if(followers.isChecked()){
	        	flwrz = "1";
	        }else{
	        	flwrz = "0";
	        }
	        
	        Settings settings = new Settings();
	        settings.setFollowers(flwrz);
	        settings.setMgm(grpmems);
	        settings.setPri(privet);
	        settings.setPub(publik);
	        settings.setStatus(currstatus);
	        settings.setUser_id(user_id);
	        
	        jsonString = jparser.toJson(settings);
	        new PostAsync().execute();
	        
		}
	};
	
    View.OnClickListener imglist = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent out = new Intent(getActivity(), CameraActivity.class);
			out.putExtra("user_id", user_id);
			startActivity(out);
			
		}
	};
		
		
	class PostAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
	        progressDialog.setMessage("Persisting Changes...");
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
	            JSONObject jObj =  JSONParser.postJSONtoURL(URL2, "ssettings", jsonString);	        
	            output = jObj.getString("message");
            } catch (Exception e1) {
        	output = "error";
            }
	        return output;
	    }

	    protected void onPostExecute(String jsonRes) {
	        this.progressDialog.dismiss();
	        Toast.makeText(getActivity(), jsonRes, Toast.LENGTH_LONG).show();
	    }

	}
	
	
}
