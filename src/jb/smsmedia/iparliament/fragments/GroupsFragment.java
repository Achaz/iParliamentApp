package jb.smsmedia.iparliament.fragments;

import org.json.JSONArray;
import jb.smsmedia.iparliament.CentralActivity;
import org.json.JSONObject;
import jb.smsmedia.iparliament.R;
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
public class GroupsFragment extends ListFragment implements Connectable {

	JSONArray groups;
	String jsonString, user_id;
	
	public GroupsFragment(String id){
		this.user_id = id;
	}
	
	private static final String GROUP = "group";
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_groups, null);
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

	private class GroupBody {
		public String group;
		public int iconRes;
		public GroupBody(String group, int iconRes) {
			this.group = group; 
			this.iconRes = iconRes;
		}
	}

	public class GroupAdapter extends ArrayAdapter<GroupBody> {

		public GroupAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.grouplayout, null);
			}
		
			TextView title = (TextView) convertView.findViewById(R.id.group_title);
			title.setText(getItem(position).group);
			
		

			return convertView;
		}

	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		try{ 
			JSONObject json = new JSONObject(jsonString);
			groups = json.getJSONArray("groups");
    		JSONObject content = groups.getJSONObject(position);
    		newContent = new GroupsDetailsFragment(content.getString("group_id"), user_id);
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
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"ggroups&id="+user_id);
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
	        GroupAdapter adapter = new GroupAdapter(getActivity());
	        
			try{ 
				JSONObject json = new JSONObject(jsonStr);
				groups = json.getJSONArray("groups");
	    		 
	    		 for(int i = 0; i < groups.length(); i++){
	    			 JSONObject content = groups.getJSONObject(i);    				
	    			 adapter.add(new GroupBody(content.getString(GROUP), R.drawable.group));
	    		 }	
	    		 
	    	 } catch (Exception e) {
	    		 Toast.makeText(activity, "error loading\n"+e.getMessage(), Toast.LENGTH_LONG).show(); 
	         }
			setListAdapter(adapter);
	    }
	}
}
