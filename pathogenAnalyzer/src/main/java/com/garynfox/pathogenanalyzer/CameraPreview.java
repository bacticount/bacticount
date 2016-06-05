package com.garynfox.pathogenanalyzer;

/* import java.io.IOException;

THIS WAS MY ORIGINAL IMPLEMENTATION

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//** A basic Camera preview class */
/*public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    
	// Required for some reason to avoid a warning (solved via stack overflow)
	// Warning:
	// Custom view com/example/view/adapter/SomeAdapter is missing constructor used 
	// by tools: (Context) or (Context,AttributeSet) or (Context,AttributeSet,int)
	public CameraPreview(Context context) {
	    super(context);
	}
	public CameraPreview(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	// OK, code begins here
	
	
	private static final String TAG = "MyActivity";
	private SurfaceHolder mHolder;
    private Camera mCamera;

    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        
    	mCamera.setDisplayOrientation(90);
    	try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }
 
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
    	
    	if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        mCamera.setDisplayOrientation(90);
        
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}*/

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.hardware.Camera.Size;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	private Camera camera;
	private SurfaceHolder holder;

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraPreview(Context context) {
		super(context);
	}

	public void init(Camera camera) {
		this.camera = camera;
		initSurfaceHolder();
	}

	@SuppressWarnings("deprecation") // needed for < 3.0
	private void initSurfaceHolder() {
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initCamera(holder);
	}

	private void initCamera(SurfaceHolder holder) {
		
		camera.setDisplayOrientation(90);
		Parameters previewParams = camera.getParameters();
        List sizes = previewParams.getSupportedPictureSizes();
        Camera.Size result = null;
        for (int i=0;i<sizes.size();i++){
            result = (Size) sizes.get(i);
            Log.i("Picture Sizes", "Supported Size. Width: " + result.width + "height : " + result.height);
        }
        previewParams.setPreviewSize(1920, 1080);
        camera.setParameters(previewParams);
        try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (Exception e) {
			Log.d("Error setting camera preview", e.toString());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
}












