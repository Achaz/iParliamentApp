package jb.smsmedia.iparliament.fragments;

import org.json.JSONObject;

import com.google.gson.Gson;

import jb.smsmedia.iparliament.CentralActivity;
import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.dtos.Comment;
import jb.smsmedia.iparliament.dtos.Invitation;
import jb.smsmedia.iparliament.dtos.PostVote;
import jb.smsmedia.iparliament.fragments.FeedsDetailFragment.VotingPostAsync;
import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.JSONParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class CommentDetailFragment extends DialogFragment implements Connectable{
	
    Comment content;
    ImageView tup, tdown;
	TextView thumbsdown, thumbsup;
    boolean likeable;
    int origin;
    String other_id;
    String user_id;
    Gson jparser = new Gson();
    String jsonString, vote = "";
    Fragment newContent = null;

	public boolean isLikeable() {
		return likeable;
	}

	public void setLikeable(boolean likeable) {
		this.likeable = likeable;
	}
	public CommentDetailFragment(Comment comment, int origin, String other_id, String user_id){
		this.content = comment;
		this.origin = origin;
		this.user_id = user_id;
		this.other_id = other_id;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("Comment Detail");
		return inflater.inflate(R.layout.comment_detail, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	
		TextView sender = (TextView)getView().findViewById(R.id.CD_POSTER);
		sender.setText(content.getSender());
		
		TextView comment = (TextView)getView().findViewById(R.id.CD_POST);
		comment.setText(content.getComment());
		
		TextView time = (TextView)getView().findViewById(R.id.CD_POST_TIME);
		time.setText(content.getTime());
		
		thumbsup = (TextView)getView().findViewById(R.id.CD_THUMBS_UP_NUM);
		thumbsup.setText(content.getThumbsup());
		
		thumbsdown = (TextView)getView().findViewById(R.id.CD_THUMBS_DOWN_NUM);
		thumbsdown.setText(content.getThumbsdown());
		
		tup = (ImageView)getView().findViewById(R.id.CD_THUMBS_UP);
		tdown = (ImageView)getView().findViewById(R.id.CD_THUMBS_DOWN);
		
		ImageView img = (ImageView)getView().findViewById(R.id.CD_PROFILE_IMAGE);
		img.setImageBitmap(content.getProfileImage());
		
		ImageView postimg = (ImageView)getView().findViewById(R.id.CD_POST_IMG);
		postimg.setImageBitmap(content.getPostImage());
	
        tup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(isVotable("good")){
					String tnum = thumbsup.getText().toString(); 
					int num = Integer.parseInt(tnum);
					num++;
					PostVote pv = new PostVote();
					pv.setNumber(num+"");
					pv.setPost_id(content.getPost_id());
					pv.setUser_id(content.getUser_id());
					pv.setPost_type(content.getPost_type());
					pv.setVote_type("tup");
					jsonString = jparser.toJson(pv);
					setLikeable(true);				 
					new VotingPostAsync().execute();
					
//					if(origin == 1){
//	                    dismiss();
//						newContent = new GroupsDetailsFragment(other_id, user_id);
//			    		switchFragment(newContent);
//					}else{
//						dismiss();
//						newContent = new TopicsDetailsFragment(other_id, user_id);
//			    		switchFragment(newContent);
//					}
				}else{
					thumbsup.setTextColor(Color.BLUE);
				}
				
				
			}
		});
		
        tdown.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isVotable("bad")){
					String tnum = thumbsdown.getText().toString();
					int num = Integer.parseInt(tnum);
					num++;
					PostVote pv = new PostVote();
					pv.setNumber(num+"");
					pv.setPost_id(content.getPost_id());
					pv.setUser_id(content.getUser_id());
					pv.setPost_type(content.getPost_type());
					pv.setVote_type("tdown");
					jsonString = jparser.toJson(pv);
					setLikeable(false);				 
					new VotingPostAsync().execute();
					
//					if(origin == 1){
//						dismiss();
//						newContent = new GroupsDetailsFragment(other_id, user_id);
//			    		switchFragment(newContent);
//					}else{
//						dismiss();
//						newContent = new TopicsDetailsFragment(other_id, user_id);
//			    		switchFragment(newContent);
//					}
					
				}else{
					thumbsdown.setTextColor(Color.BLUE);
				}
				
				
			}
		});
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

	    protected void onPostExecute(String jsonRes) {
	       String[] vt = vote.split("_");
	    	//if(!isLikeable()){
	           	thumbsdown.setText(vt[0]);
	        //}
            
            //if(isLikeable()){
	           thumbsup.setText(vt[1]);
	        //}
	    }

	}

}
