package jb.smsmedia.iparliament;

import java.io.ByteArrayOutputStream;
import java.io.File;

import jb.smsmedia.iparliament.CameraActivity2.PostAsync;
import jb.smsmedia.iparliament.dtos.ProfileImage;
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

public class CameraActivity extends Activity implements Connectable{

	protected static final int CAMERA_PIC_REQUEST = 1337;
	private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    ImageView image;
    ImageButton cam, gal, upload;
    Button post;
    EditText caption;
    String user_id, imgbits, jsonString;
    Gson jparser = new Gson();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera2);
		
		Intent in = getIntent();
		user_id = in.getStringExtra("user_id");
		//Toast.makeText(this, "user_id = "+user_id, Toast.LENGTH_LONG).show();
		
		image =(ImageView)findViewById(R.id.UPLOAD_IMAGE);
		cam =(ImageButton)findViewById(R.id.UPLOAD_FROM_CAMERA);
		gal =(ImageButton)findViewById(R.id.UPLOAD_FROM_GALLERY);
		upload =(ImageButton)findViewById(R.id.UPLOAD_IMAGE_BTN);
		
        upload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(imgbits.length() < 1){
					Toast.makeText(getApplicationContext(), "No Image Selected", Toast.LENGTH_LONG).show();
				}else{
					ProfileImage pi = new ProfileImage();
					pi.setImage("x");
					pi.setUser_id(user_id);
					jsonString = jparser.toJson(pi);
					new UploadAsync().execute();
				}		
			}
		});

		cam.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);		
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
            
			image.setImageBitmap(myBitmap);
			
            
	    }else if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = getPath(selectedImageUri);
            
            if(selectedImagePath.length() > 1){
            	 File imgFile = new  File(selectedImagePath);
                 myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                 
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
                 
                 image.setImageBitmap(myBitmap);
                 
                
 				
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
	
	class UploadAsync extends AsyncTask<JSONObject, Void, String> {
	    public ProgressDialog progressDialog = new ProgressDialog(CameraActivity.this);

	    protected void onPreExecute() {
	        progressDialog.setMessage("Uploading...");
	        progressDialog.show();
	        progressDialog.setOnCancelListener(new OnCancelListener() {
	            public void onCancel(DialogInterface diaInterface) {
	            	UploadAsync.this.cancel(true);
	                diaInterface.dismiss();
	            }
	        });
	    }

	    @Override
	    protected String doInBackground(JSONObject...json) {

	        String output = "";
	        try{
	        	JSONObject jObj =  JSONParser.uploadImage(URL2, "uploadimg", jsonString, imgbits);	        
	            output = jObj.getString("message");
            } catch (Exception e1) {
        	  output = "error"+e1.getMessage();
            }
	        return output;
	    }

	    protected void onPostExecute(String jsonRes) {
	        this.progressDialog.dismiss();
	        //Toast.makeText(CameraActivity.this, jsonRes, Toast.LENGTH_LONG).show();
	        finish();
	    }

	}

}
