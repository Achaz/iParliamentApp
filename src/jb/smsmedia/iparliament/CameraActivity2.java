package jb.smsmedia.iparliament;

import java.io.ByteArrayOutputStream;
import java.io.File;

import jb.smsmedia.iparliament.dtos.FeedPost;
import jb.smsmedia.iparliament.dtos.GroupPost;
import jb.smsmedia.iparliament.dtos.TopicPost;
import jb.smsmedia.iparliament.utils.Connectable;
import jb.smsmedia.iparliament.utils.JSONParser;
import org.json.JSONObject;
import com.google.gson.Gson;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity2 extends Activity implements Connectable{

	protected static final int CAMERA_PIC_REQUEST = 1337;
	private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    ImageView image;
    ImageButton cam, gal, upload;
    Button post;
    EditText caption;
    String user_id, other_id, img, jsonString, type;
    String imgbits;
    Gson jparser = new Gson();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera3);
		
		Intent in = getIntent();
		user_id = in.getStringExtra("user_id");
		other_id = in.getStringExtra("other_id");
		type = in.getStringExtra("type");
		
		image =(ImageView)findViewById(R.id.UPLOAD2_IMAGE);
		cam =(ImageButton)findViewById(R.id.UPLOAD2_FROM_CAMERA);
		gal =(ImageButton)findViewById(R.id.UPLOAD2_FROM_GALLERY);
		post =(Button)findViewById(R.id.UPLOAD2_POST_BTN);
		caption =(EditText)findViewById(R.id.IMAGE2_POST_CAPTION);
				
		cam.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);		
			}
		});
		
        post.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = caption.getText().toString();
				
				if(msg.length() > 1 || imgbits.length() > 1){
					if(type.equals("feedpost")){
						FeedPost fp = new FeedPost();
					    fp.setPost(msg);
					    fp.setUser_id(user_id);
					    fp.setOther_id("0");
					    fp.setType("feedpost");
					    jsonString = jparser.toJson(fp);
					    new PostAsync().execute();	
					}else if(type.equals("topicpost")){
						TopicPost tp = new TopicPost();
					    tp.setPost(msg);
					    tp.setUser_id(user_id);
					    tp.setOther_id(other_id);
					    tp.setType("topicpost");
					    jsonString = jparser.toJson(tp);
					    new PostAsync().execute();	
					}else if(type.equals("grouppost")){
						GroupPost gp = new GroupPost();
					    gp.setPost(msg);
					    gp.setUser_id(user_id);
					    gp.setOther_id(other_id);
					    gp.setType("grouppost");
					    jsonString = jparser.toJson(gp);
					    new PostAsync().execute();
					}else{
						
					}	
				}else{
					Toast.makeText(CameraActivity2.this, "Take a photo or add one from your gallery", Toast.LENGTH_LONG).show();
				}
			    
								
			}
		});
		
		gal.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
	             intent.setType("image/*");
	             intent.setAction(Intent.ACTION_GET_CONTENT);
	             startActivityForResult(Intent.createChooser(intent,
	                     "Select Picture"), SELECT_PICTURE);		
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		Bitmap myBitmap;
		try{
		
	    if( requestCode == CAMERA_PIC_REQUEST)
	    {
	    //  data.getExtras()
	        myBitmap = (Bitmap) data.getExtras().get("data");
	        	        
			image.setImageBitmap(myBitmap);
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
            
            //Resize the image
            double width = myBitmap.getWidth();
            double height = myBitmap.getHeight();
            double ratio = 400/width;
            int newheight = (int)(ratio*height);
             
           
             
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 400, newheight, true);
             
            //Here you can define .PNG as well
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 95, bao);
            byte[] ba = bao.toByteArray();
            imgbits = Base64.encodeToString(ba, Base64.DEFAULT);
            
	    }else if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = getPath(selectedImageUri);
            
            if(selectedImagePath.length() > 1){
            	 File imgFile = new  File(selectedImagePath);
                 myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                 image.setImageBitmap(myBitmap);
                 ByteArrayOutputStream bao = new ByteArrayOutputStream();
                 
                 //Resize the image
                 double width = myBitmap.getWidth();
                 double height = myBitmap.getHeight();
                 double ratio = 400/width;
                 int newheight = (int)(ratio*height);
                  
                
                  
                 myBitmap = Bitmap.createScaledBitmap(myBitmap, 400, newheight, true);
                  
                 //Here you can define .PNG as well
                 myBitmap.compress(Bitmap.CompressFormat.JPEG, 95, bao);
                 byte[] ba = bao.toByteArray();
                 imgbits = Base64.encodeToString(ba, Base64.DEFAULT);
                
 				
            }else{
            	Toast.makeText(getApplicationContext(), "Nothing gotten from gallery", Toast.LENGTH_LONG).show();
            }
           
        }
	    else 
	    {
	        Toast.makeText(getApplicationContext(), "No Action Done", Toast.LENGTH_LONG).show();
	    }
	    super.onActivityResult(requestCode, resultCode, data);
		}catch(Exception e){
			Toast.makeText(getApplicationContext(), "No Image Gotten", Toast.LENGTH_LONG).show();
		}
	}
	
	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

	class PostAsync extends AsyncTask<JSONObject, Void, String> {
	    public ProgressDialog progressDialog = new ProgressDialog(CameraActivity2.this);

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
	        	JSONObject jObj =  JSONParser.uploadImage(URL2, "postimg", jsonString, imgbits);	        
	            output = jObj.getString("message");
            } catch (Exception e1) {
        	  output = "error"+e1.getMessage();
            }
	        return output;
	    }

	    protected void onPostExecute(String jsonRes) {
	        this.progressDialog.dismiss();
	        //Toast.makeText(CameraActivity2.this, jsonRes, Toast.LENGTH_LONG).show();
	        finish();
	    }

	}
}
