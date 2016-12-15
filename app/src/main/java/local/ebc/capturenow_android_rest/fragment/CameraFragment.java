package local.ebc.capturenow_android_rest.fragment;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * Created by ebc on 15.12.2016.
 */



public class CameraFragment extends Fragment {


    @BindView(R.id.fragment_capture_container) FrameLayout preview;
    private Camera mCamera;
    private CameraPreview mPreview;
    //@BindView(R.id.fragment_capture_container) FrameLayout preview;

    public CameraFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, container, false);
        //View v = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);

        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getActivity(), mCamera);
        preview.addView(mPreview);

        final MainActivity activity;
        activity = (MainActivity) getActivity();
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, activity.mPicture);
            }
        });

        return view;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCamera.release();
    }
}
