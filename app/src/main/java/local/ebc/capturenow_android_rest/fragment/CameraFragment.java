package local.ebc.capturenow_android_rest.fragment;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import local.ebc.capturenow_android_rest.R;
import local.ebc.capturenow_android_rest.activity.MainActivity;
import local.ebc.capturenow_android_rest.helper.CameraPreview;

/**
 * @author Emil Claussen on 15.12.2016.
 */



public class CameraFragment extends Fragment {

    // Declare variables and initialize the camera preview layout.
    @BindView(R.id.fragment_capture_container) FrameLayout preview;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Boolean captured;

    public CameraFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, container, false);
        ButterKnife.bind(this, view);
        mCamera = getCameraInstance();
        captured = false;

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getActivity(), mCamera);
        preview.addView(mPreview);

        final MainActivity activity;
        activity = (MainActivity) getActivity();
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!captured) {
                    mCamera.takePicture(null, null, activity.mPicture);
                    captured = true;
                } else {
                    mCamera.startPreview();
                    captured = false;
                }
            }
        });

        return view;
    }

    // Function to properly release camera after use.
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            // Ready the rear camera.
            c = Camera.open(0);
        }
        catch (Exception e){
            // No camera found.
            Log.d("CameraFragment", "Camera not found.");
        }
        return c; // Returns null if camera is unavailable.
    }

    // Release camera when fragment is detached.
    @Override
    public void onDetach() {
        super.onDetach();
        releaseCamera();
    }
}
