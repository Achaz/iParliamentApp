package jb.smsmedia.iparliament;

import org.json.JSONException;
import org.json.JSONObject;

import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.JSONParser;
import jb.smsmedia.iparliament.utils.PreferenceConnector;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends Activity implements Connectable {

	Button signin;
	EditText username, password;
	TextView head;
	String user, pass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		
		head = (TextView)findViewById(R.id.HTXT);
		username = (EditText)findViewById(R.id.USERNAME);
	    password = (EditText)findViewById(R.id.PASSWORD);
	    signin = (Button)findViewById(R.id.SIGNIN_BTN);
		user = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.USERNAME, null);
		pass = PreferenceConnector.readString(getApplicationContext(), PreferenceConnector.PASSWORD, null);
		
		if(user != null && pass != null){
			head.setVisibility(-1);
			username.setVisibility(-1);
		    password.setVisibility(-1);
		    signin.setVisibility(-1);
		    
			setUser(user);
			setPass(pass);
			new AutoLoginAsync().execute(); 
		}else{				
		    signin.setOnClickListener(signinListener);
		
		}
		
	}
    
	View.OnClickListener signinListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setUser(username.getText().toString());
			setPass(password.getText().toString());
			
			PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.USERNAME, getUser());
			PreferenceConnector.writeString(getApplicationContext(), PreferenceConnector.PASSWORD, getPass());
			new DataTransmitterAsync().execute(); 
		}
	};
	
	class DataTransmitterAsync extends AsyncTask<JSONObject, Void, String> {
		
	    public ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);

	    protected void onPreExecute() {
	        progressDialog.setMessage("Authenticating...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() {
	            public void onCancel(DialogInterface diaInterface) {
	                DataTransmitterAsync.this.cancel(true);
	                diaInterface.dismiss();
	            }
	        });
	    }

	    JSONObject jobject;
	    String id = "";
	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        
	        try {
	        	String u = getUser().trim();
	        	String p = getPass().trim();
		        String res =  JSONParser.getJSONStringfromURL(URL+"login&username="+u+"&password="+p);
		        output = res;
	        } catch (Exception e1) {
	        	output = "error"+e1.getMessage();
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String response) {
	        this.progressDialog.dismiss();
	        try {
				JSONObject json = new JSONObject(response);
				String succ = json.getString("success");
				if(succ.equals("1")){
			        	Intent out = new Intent(getApplicationContext(), CentralActivity.class);
			        	out.putExtra("auth_id", json.getString("auth_id"));
			        	out.putExtra("uname", json.getString("uname"));
			        	startActivity(out);
			        	finish();
			    }else{
			        	username.setText("");
			        	password.setText("");
			        	Toast.makeText(getApplicationContext(), "wrong username Or password", Toast.LENGTH_LONG).show();
			    }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
			}
	       
	    }
	}
	
    class AutoLoginAsync extends AsyncTask<JSONObject, Void, String> {
		
	    public ProgressDialog progressDialog = new ProgressDialog(SignInActivity.this);

	    protected void onPreExecute() {
	        progressDialog.setMessage("Logging In...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() {
	            public void onCancel(DialogInterface diaInterface) {
	            	AutoLoginAsync.this.cancel(true);
	                diaInterface.dismiss();
	            }
	        });
	    }

	    JSONObject jobject;
	    String id = "";
	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        
	        try {
	        	String u = getUser().trim();
	        	String p = getPass().trim();
		        String res =  JSONParser.getJSONStringfromURL(URL+"login&username="+u+"&password="+p);
		        output = res;
	        } catch (Exception e1) {
	        	output = "error"+e1.getMessage();
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String response) {
	        this.progressDialog.dismiss();
	        try {
				JSONObject json = new JSONObject(response);
				String succ = json.getString("success");
				if(succ.equals("1")){
			        	Intent out = new Intent(getApplicationContext(), CentralActivity.class);
			        	out.putExtra("auth_id", json.getString("auth_id"));
			        	out.putExtra("uname", json.getString("uname"));
			        	startActivity(out);
			        	finish();
			    }else{
			        	username.setText("");
			        	password.setText("");
			        	Toast.makeText(getApplicationContext(), "wrong username Or password", Toast.LENGTH_LONG).show();
			    }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
			}
	       
	    }
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

}
