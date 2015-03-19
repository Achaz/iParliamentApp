package jb.smsmedia.iparliament.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import jb.smsmedia.iparliament.CentralActivity;
import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.dtos.Feed;
import jb.smsmedia.iparliament.dtos.FeedComment;
import jb.smsmedia.iparliament.dtos.PostVote;
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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class FeedsDetailFragment extends ListFragment implements Connectable{

	ArrayList<HashMap<String, String>> postList;
	JSONArray comments;
	private static final String SENDER = "name";
	private static final String POST = "post";
	private static final String TIME = "time";
	String post_id,jsonString, user_id, vote = "";
	EditText comment;
	Button post_btn, cancel_btn;
	TextView n_tup, n_tdown, name, post, time;
	ImageView tup, tdown, prof_pic, post_img;
	Feed feed;
	Gson jparser = new Gson();
	boolean likeable;
	
	public boolean isLikeable() {
		return likeable;
	}

	public void setLikeable(boolean likeable) {
		this.likeable = likeable;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new DataTransmitterAsync().execute(); 
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		new DataTransmitterAsync().execute(); 
	}

	public FeedsDetailFragment(String pid, String uid, Feed feed){
	    this.post_id = pid;	
	    this.user_id = uid;
	    this.feed = feed;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.feed_detaillayout, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
        Activity activity = getActivity();
		
		if(activity != null){			
				new DataTransmitterAsync().execute();			
		}else{
			Toast.makeText(activity, "no",Toast.LENGTH_LONG).show();
		}
		
		prof_pic = (ImageView)getView().findViewById(R.id.FD_PROFILE_IMAGE);
		prof_pic.setImageBitmap(feed.getProfileImage());
		
		post_img = (ImageView)getView().findViewById(R.id.FD_IMG_POST);
		post_img.setImageBitmap(feed.getPostImage());
		
		name = (TextView)getView().findViewById(R.id.FD_POSTER);
		name.setText(feed.getName());
		
		post = (TextView)getView().findViewById(R.id.FD_POST);
		post.setText(feed.getPost());
		
		time = (TextView)getView().findViewById(R.id.FD_POST_TIME);		
		time.setText(feed.getTime());
		
		comment = (EditText)getView().findViewById(R.id.FD_COMMENT_MSG);
		post_btn = (Button)getView().findViewById(R.id.FD_COMMENT_BTN);
		cancel_btn = (Button)getView().findViewById(R.id.FD_CANCEL_BTN);
		tup = (ImageView)getView().findViewById(R.id.FD_THUMBS_UP);
		tdown = (ImageView)getView().findViewById(R.id.FD_THUMBS_DOWN);
		
		n_tup = (TextView)getView().findViewById(R.id.FD_THUMBS_UP_NUM);
		n_tup.setText(feed.getGood());
		
		n_tdown = (TextView)getView().findViewById(R.id.FD_THUMBS_DOWN_NUM);
		n_tdown.setText(feed.getBad());
		
		
		tup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isVotable("good")){
					String tnum = n_tup.getText().toString(); 
					int num = Integer.parseInt(tnum);
					num++;
					PostVote pv = new PostVote();
					pv.setNumber(num+"");
					pv.setPost_id(post_id);
					pv.setUser_id(user_id);
					pv.setPost_type("feedpost");
					pv.setVote_type("tup");
					jsonString = jparser.toJson(pv);
					setLikeable(true);				 
					new VotingPostAsync().execute();
				}else{
					n_tup.setTextColor(Color.BLUE);
				}
				
				
			}
		});
		
        tdown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isVotable("bad")){
					String tnum = n_tdown.getText().toString();
					int num = Integer.parseInt(tnum);
					num++;
					PostVote pv = new PostVote();
					pv.setNumber(num+"");
					pv.setPost_id(post_id);
					pv.setUser_id(user_id);
					pv.setPost_type("feedpost");
					pv.setVote_type("tdown");
					jsonString = jparser.toJson(pv);
					setLikeable(false);				 
					new VotingPostAsync().execute();
				}else{
					n_tdown.setTextColor(Color.BLUE);
				}
				
				
			}
		});


		cancel_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchFragment(new FeedsFragment(user_id));
			}
		});
		
		post_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String commentIn = comment.getText().toString();
				
				if(commentIn.length() > 0){
					FeedComment fc = new FeedComment();
				    fc.setUser_id(user_id);
				    fc.setPost_id(post_id);
				    fc.setComment(commentIn);
				    jsonString = jparser.toJson(fc);
				    new PostAsync().execute();	
				}else{
					Toast.makeText(getActivity(), "Enter a comment", Toast.LENGTH_LONG).show();
				}
			    
			}
		});
	}

	private class FeedsBody {
		public String sender;
		public String time;
		public String post;
		public Bitmap profileImage;
		public FeedsBody(String sender, String post, String time, Bitmap profileImage) {
			this.sender = sender; 
			this.post = post;
			this.time = time;
			this.profileImage = profileImage;
		}
	}

	public class FeedsAdapter extends ArrayAdapter<FeedsBody> {

		public FeedsAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.commentlayout, null);
			}
			
			ImageView icon = (ImageView) convertView.findViewById(R.id.COMMENT_PROFILE_IMAGE);
			icon.setImageBitmap(getItem(position).profileImage);
			
			TextView sender = (TextView) convertView.findViewById(R.id.COMMENT_SENDER);
			sender.setText(getItem(position).sender);
			
			TextView post = (TextView) convertView.findViewById(R.id.COMMENT);
			post.setText(getItem(position).post);
			
			TextView time = (TextView) convertView.findViewById(R.id.COMMENT_TIME);
			time.setText(getItem(position).time);

			return convertView;
		}

	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		
		
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
	
	public boolean isVotable(String type){
		if(type.equals("good")){
			return true;
		}else if(type.equals("bad")){
			return true;
		}else{
			return false;
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

	    String inString, parameterPass;
	    JSONObject jobject;
	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        
	        try {
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"gcomments&ctype=feed&id="+post_id);
		        output = jsonStr;
	        } catch (Exception e1) {
	        	output = "error";
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String jsonStr) {
	        this.progressDialog.dismiss();
	        Activity activity = getActivity();
	    	FeedsAdapter adapter = new FeedsAdapter(getActivity());
			try{ 
				JSONObject json = new JSONObject(jsonStr);
				comments = json.getJSONArray("comments");
	    		 
	    		 for(int i = 0; i < comments.length(); i++){
	    			 
	    			 JSONObject comment = comments.getJSONObject(i);
	    			 String s = comment.getString("comment");
	    			 JSONObject content = new JSONObject(s);
	    			 adapter.add(new FeedsBody(content.getString(SENDER), content.getString(POST), content.getString(TIME), getBitmap(content.getString("prof_pic"), 0)));
	    		 }	
	    		 
	    	 } catch (Exception e) {
	    		 //Toast.makeText(activity, "error loading\n"+e.getMessage(), Toast.LENGTH_LONG).show();  
	         }
			setListAdapter(adapter);
	    }

	}
	
	class PostAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
	        progressDialog.setMessage("Posting...");
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
	            JSONObject jObj =  JSONParser.postJSONtoURL(URL2, "scomment", jsonString);	        
	            output = jObj.getString("message");
            } catch (Exception e1) {
        	output = "error";
            }
	        return output;
	    }

	    protected void onPostExecute(String jsonRes) {
	        this.progressDialog.dismiss();
	        Toast.makeText(getActivity(), jsonRes, Toast.LENGTH_LONG).show();
	        new DataTransmitterAsync().execute();
	    }

	}
	
	class VotingPostAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
  
	    }

	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        try{
	            JSONObject jObj =  JSONParser.postJSONtoURL(URL2, "postvote", jsonString);	        
	            output = jObj.getString("message");
	            vote = output;   
	            
	            
            } catch (Exception e1) {
        	    output = "error";
            }
	        return vote;
	    }

	    protected void onPostExecute(String res) {
	       
	    	//Toast.makeText(getActivity(), "vote = "+res, Toast.LENGTH_LONG).show();
	    	String[] vt = vote.split("_");
	    	//if(!isLikeable()){
	           	n_tdown.setText(vt[0]);
	        //}
            
            //if(isLikeable()){
	           	n_tup.setText(vt[1]);
	        //}
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
