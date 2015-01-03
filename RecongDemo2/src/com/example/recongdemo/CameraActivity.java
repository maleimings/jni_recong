package com.example.recongdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.recongapi.RecongAPI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnTouchListener, AutoFocusCallback{


	private SurfaceView mSurfaceView;
	private SurfaceHolder mHolder;
	private Camera mCamera;
    private Handler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camera);

		mSurfaceView = (SurfaceView) this.findViewById(R.id.camera);
		mSurfaceView.setOnTouchListener(this);
		mSurfaceView.setKeepScreenOn(true);
		mHolder = mSurfaceView.getHolder();
		mHolder.addCallback(new holderCallback());
	    mHandler = new Handler();
	}
	
	public class holderCallback implements Callback
	{
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height)
		{
			// 设置参数
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPictureFormat(ImageFormat.JPEG);
			parameters.setPreviewFormat(ImageFormat.NV21);
			parameters.setPictureSize(640, 480);
			parameters.setPreviewSize(640, 480);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
		}
		
		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{
			// TODO Auto-generated method stub
			if(mCamera == null)
			{
				mCamera = Camera.open();
				try
				{
					mCamera.setPreviewDisplay(holder);
					mCamera.setPreviewCallback(new GetViewCallBack());
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder)
		{
			// TODO Auto-generated method stub
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;

		}
		
	}
	
    public class GetViewCallBack implements PreviewCallback
    {
		String nowPlate = null;
		String nowplateType = null;
		int times = 0;
		Toast toast = null;
		@Override
		public void onPreviewFrame(byte[] data, Camera camera)
		{
		    if (times == 20 && toast != null) {
		        toast.cancel();
		        toast = null;
	            times ++;
	            return;
		    }
		    if (times < 40) {
		        times ++;
		        return;
		    }
			final String result = RecongAPI.getRecongAPI().doRecongData(data, 640, 480, "oo");
			times = 0;
            mHandler.post(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    toast = Toast.makeText(CameraActivity.this, "result is "+ result, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
		}
    	
    }
    
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		// TODO Auto-generated method stub
		
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
			List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
	
			Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f);
			focusAreas.add(new Camera.Area(focusRect, 1000));
			setFocus(focusAreas);
	
			return true;
		} 
		return false;
	}
	
    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;  
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
  
        int centerX = (int) (x / getWindowManager().getDefaultDisplay().getWidth()* 2000 - 1000);
        int centerY = (int) (y / getWindowManager().getDefaultDisplay().getHeight()* 2000 - 1000);
  
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);
  
        return new Rect(left, top, right, bottom);
        }  
    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }  
        if (x < min) {
            return min;
        }
        return x;
    }


	@Override
	public void onAutoFocus(boolean arg0, Camera arg1)
	{
		// TODO Auto-generated method stub
//		Toast.makeText(CameraActivity.this, "正在对焦", Toast.LENGTH_SHORT).show();
	}
	
	private void setFocus(List<Area> focusAreas) {
		if (mCamera == null) return;
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			List<String> supportedFocus = parameters.getSupportedFocusModes();
	//		if (isSupported(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, supportedFocus)) {
	//			parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
	//		} else 
			if (isSupported(Parameters.FOCUS_MODE_AUTO, supportedFocus)) {
				parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			}
	        if (parameters.getMaxNumFocusAreas() > 0) {
	            parameters.setFocusAreas(focusAreas);
	        }  
	        mCamera.setParameters(parameters);
	        mCamera.autoFocus(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    private static boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

}
