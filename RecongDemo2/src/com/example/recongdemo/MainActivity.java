package com.example.recongdemo;
import com.example.recongapi.RecongAPI;
import com.example.recongdemo.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {

	private Button mPicture;
	private Button mData;
	private static final int GET_PIC = 1;
	private String mPath;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initViews();
		mHandler = new Handler();

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		if (requestCode == GET_PIC && resultCode == Activity.RESULT_OK) {
			Uri originalUri = intent.getData();
            String[] proj = {MediaStore.Images.Media.DATA};            


            try {
		           Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
		           int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		          cursor.moveToFirst();

		          mPath = cursor.getString(column_index);
		          
		          new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						final String result = RecongAPI.getRecongAPI().doRecongFile(mPath, 640, 480, "op");
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(MainActivity.this, "result is "+ result, Toast.LENGTH_SHORT).show();
							}
						});
					}
				}).start();
            } catch (Exception e) {
            	e.printStackTrace();
            	return;
            }//处理图片识别
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	private void initViews() {
		mPicture = (Button) this.findViewById(R.id.picture);
		mPicture.setOnClickListener(this);
		mData = (Button) this.findViewById(R.id.data);
		mData.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.picture:
			getPictureFromGallery();
			break;
		case R.id.data:
			startCameraActivity();
			break;
		default:
			break;
		}
	}
	
	private void startCameraActivity() {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		MainActivity.this.startActivity(intent);
	}
	
	private void getPictureFromGallery() {
        Intent intent = new Intent();  

        intent.setType("image/*");  

        intent.setAction(Intent.ACTION_GET_CONTENT);   

        startActivityForResult(intent, GET_PIC);  
	}
	

}
