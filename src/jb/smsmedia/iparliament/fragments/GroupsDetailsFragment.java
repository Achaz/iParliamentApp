package jb.smsmedia.iparliament.fragments;

import java.io.File;

import jb.smsmedia.iparliament.CameraActivity;
import jb.smsmedia.iparliament.CameraActivity2;
import jb.smsmedia.iparliament.CentralActivity;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import jb.smsmedia.iparliament.R;
import jb.smsmedia.iparliament.dtos.Comment;
import jb.smsmedia.iparliament.dtos.GroupPost;
import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.JSONParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//109.123.112.186
@SuppressLint("ValidFragment")
public class GroupsDetailsFragment extends ListFragment implements Connectable{

	JSONArray posts;
	String jsonString, group_id, user_id;
    private static final String TIME = "time";
	private static final String NAME = "name";
	private static final String POST = "post";
	private static final String GOOD = "good";
	private static final String BAD = "bad";
	private static final int TAKE_PIC   = 1;
    private static final int GET_PIC  = 2; 
	EditText post_msg;
	Button post_btn;
	ImageButton addpic;
	Gson jparser = new Gson();
	
	public GroupsDetailsFragment(String id, String uid){
		this.group_id = id;
		this.user_id = uid;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.group_detail_layout, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
		
		if(activity != null){			
				new DataTransmitterAsync().execute(); 
		}else{
			//Toast.makeText(activity, "no",Toast.LENGTH_LONG).show();
		}
		
		ActionItem cameraItem   = new ActionItem(TAKE_PIC, "Take Photo", getResources().getDrawable(R.drawable.access_camera));
	    ActionItem galleryItem     = new ActionItem(GET_PIC, "Upload Photo", getResources().getDrawable(R.drawable.gallery_picture));
	     
	     final QuickAction quickAction = new QuickAction(activity, QuickAction.VERTICAL);
	     quickAction.addActionItem(cameraItem);
	     quickAction.addActionItem(galleryItem);
	     
	     quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {          
	            @Override
	            public void onItemClick(QuickAction source, int pos, int actionId) {
	                //here we can filter which action item was clicked with pos or actionId parameter
	                switch(pos){
	                case 0:
	                	Intent out = new Intent(getActivity(), CameraActivity2.class);
	                	out.putExtra("user_id", user_id);
	                	out.putExtra("other_id", group_id);
	                	out.putExtra("type", "grouppost");
	        			startActivity(out);
	                	break;
	                	
	                case 1:
	                	Intent out2 = new Intent(getActivity(), CameraActivity2.class);
	                	out2.putExtra("user_id", user_id);
	                	out2.putExtra("other_id", group_id);
	                	out2.putExtra("type", "grouppost");
	                	startActivity(out2);
	                	break;
	                }    
	            }
	      });

	      quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {          
	            @Override
	            public void onDismiss() {
	                Toast.makeText(getActivity(), "No photo added", Toast.LENGTH_SHORT).show();
	            }
	     });
	      
		post_msg = (EditText)getView().findViewById(R.id.GROUP_POST_MSG);
		post_btn = (Button)getView().findViewById(R.id.GROUP_POST_BTN);
        addpic = (ImageButton)getView().findViewById(R.id.GROUP_ADDPIC_BTN);
	    
        addpic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			     quickAction.show(v);		    
			}
		});
		
		post_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = post_msg.getText().toString();
				
				if(msg.length() > 0){
					GroupPost gp = new GroupPost();
				    gp.setPost(msg);
				    gp.setUser_id(user_id);
				    gp.setOther_id(group_id);
				    gp.setType("grouppost");
				    gp.setImage(" ");
				    jsonString = jparser.toJson(gp);
				    new PostAsync().execute();	
				}else{
					Toast.makeText(getActivity(), "Enter a post", Toast.LENGTH_LONG).show();
				}
			    
			}
		});
		
		
	}

//	@Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		super.onStart();
//		new SilentAsync().execute(); 
//	}
	
	private class CommentBody {
		public String post, time, name, good, bad;
		public Bitmap profileImage, postImage;
		public CommentBody( String name, String post, String time, String good, String bad,  Bitmap profileImage, Bitmap postImage) {
			this.time = time; 
			this.post = post;
			this.name = name;
			this.good = good;
			this.bad = bad;
			this.profileImage = profileImage;
			this.postImage = postImage;
		}
	}

	public class PostsAdapter extends ArrayAdapter<CommentBody> {

		public PostsAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.groups_commentlayout, null);
			}
			
			ImageView icon = (ImageView) convertView.findViewById(R.id.GP_COMMENT_PROFILE_IMAGE);
			icon.setImageBitmap(getItem(position).profileImage);
			
			ImageView postimg = (ImageView) convertView.findViewById(R.id.GP_IMG_POST);
			postimg.setImageBitmap(getItem(position).postImage);
		    
			TextView sender = (TextView) convertView.findViewById(R.id.GP_COMMENT_SENDER);
			sender.setText(getItem(position).name);
			
			TextView time = (TextView) convertView.findViewById(R.id.GP_COMMENT_TIME);
			time.setText(getItem(position).time);
			
			TextView post = (TextView) convertView.findViewById(R.id.GP_COMMENT);
			post.setText(getItem(position).post);
			
			TextView good = (TextView) convertView.findViewById(R.id.GP_THUMBS_UP_NUM);
			good.setText(getItem(position).good);
			
			TextView bad = (TextView) convertView.findViewById(R.id.GP_THUMBS_DOWN_NUM);
			bad.setText(getItem(position).bad);
			
		

			return convertView;
		}

	}
	
	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Comment comment = new Comment();
		try{ 
			JSONObject json = new JSONObject(jsonString);
			posts = json.getJSONArray("comments");
    		JSONObject feed = posts.getJSONObject(position);
    		String s = feed.getString("comment");
    		JSONObject content = new JSONObject(s);
    		
    		Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.f2);
    		comment.setComment(content.getString(POST));
    		comment.setProfileImage(bm);
    		comment.setSender(content.getString(NAME));
    		comment.setThumbsdown(content.getString(BAD));
    		comment.setThumbsup(content.getString(GOOD));
    		comment.setTime(content.getString(TIME));
    		comment.setUser_id(user_id);
    		comment.setPost_id(content.getString("post_id"));
    		comment.setPost_type("grouppost");
    		comment.setProfileImage(getBitmap(content.getString("prof_pic"), 0));
    		comment.setPostImage(getBitmap(content.getString("image"), 1));
    		
    		DialogFragment df = new CommentDetailFragment(comment, 1, group_id, user_id);	
    		df.show(getFragmentManager(), "");
    		 
    	 } catch (Exception e) {
    		// Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();  
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
	        progressDialog.setMessage("Fetching...");
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
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"gcomments&ctype=group&id="+group_id);
		        output = jsonStr;
	        } catch (Exception e1) {
	        	output = "error";
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String jsonStr) {
	        this.progressDialog.dismiss();
	        jsonString = jsonStr;
	    	new LoadPosts().execute();
	    }
	}
	
	class SilentAsync extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
	        
	    }

	    String inString, parameterPass;
	    JSONObject jobject;
	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        
	        try {
		        String jsonStr =  JSONParser.getJSONStringfromURL(URL+"gcomments&ctype=group&id="+group_id);
		        output = jsonStr;
	        } catch (Exception e1) {
	        	output = "error";
            }
	        
	        return output;
	    }

	    protected void onPostExecute(String jsonStr) {
	        //this.progressDialog.dismiss();
	        jsonString = jsonStr;
	    	new LoadPosts().execute();
	    }
	}
	
	class LoadPosts extends AsyncTask<JSONObject, Void, String> {
		Activity activity = getActivity();
	    public ProgressDialog progressDialog = new ProgressDialog(getActivity());

	    protected void onPreExecute() {
	        progressDialog.setMessage("Loading...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() {
	            public void onCancel(DialogInterface diaInterface) {
	                LoadPosts.this.cancel(true);
	                diaInterface.dismiss();
	            }
	        });
	    }

	    String inString, parameterPass;
	    JSONObject jobject;
	    PostsAdapter adapter;
	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        adapter = new PostsAdapter(getActivity());
	    	try{ 
				JSONObject js = new JSONObject(jsonString);
				posts = js.getJSONArray("comments");
	    		 
	    		 for(int i = 0; i < posts.length(); i++){
	    			 
	    			 JSONObject feed = posts.getJSONObject(i);
	    			 String s = feed.getString("comment");
	    			 JSONObject content = new JSONObject(s);
	    			 adapter.add(new CommentBody(content.getString(NAME), content.getString(POST), content.getString(TIME), content.getString(GOOD), content.getString(BAD), getBitmap(content.getString("prof_pic"), 0), getBitmap(content.getString("image"), 1)));
	    			
	    		 }	
	    		 
	    	 } catch (Exception e) {
	    		 Toast.makeText(activity, "no posts", Toast.LENGTH_LONG).show();  
	         }
	        
	        return output;
	    }

	    protected void onPostExecute(String jsonStr) {
	        this.progressDialog.dismiss();
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
	            JSONObject jObj =  JSONParser.postJSONtoURL(URL2, "postsmth", jsonString);	        
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
