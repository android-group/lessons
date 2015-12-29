package com.example.yury.camera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.yury.camera.model.MediaType;
import com.example.yury.camera.utils.MediaFileUtils;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 100;
    private static final int VIDEO_REQUEST_CODE = 200;

    private static final int SAVE_RESULT_CODE = -1;
    private static final int NOT_SAVE_RESULT_CODE = 0;

    private static final String TAG = "CameraAPP";

    Uri fileUri;

    private Button takePhotoBtn;
    private Button takeVideoBtn;


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkCameraHardware(this)) {
            setContentView(R.layout.activity_without_camera);
            return;
        }

        setContentView(R.layout.activity_main);
        takePhotoBtn = (Button) findViewById(R.id.takePhotoBtn);
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        takeVideoBtn = (Button) findViewById(R.id.takeVideoBtn);
        takeVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeVideo();
            }
        });

        getCameraInstance();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = MediaFileUtils.getOutputMediaFileUri(MediaType.IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    private void takeVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = MediaFileUtils.getOutputMediaFileUri(MediaType.VIDEO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        //качество видео
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        //сколько секунд
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);

        // какой размен видео в байтах
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 300);

        startActivityForResult(intent, VIDEO_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NOT_SAVE_RESULT_CODE) {
            return;
        }
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                Toast.makeText(this, "Фото сохранено", Toast.LENGTH_LONG).show();
                break;
            case VIDEO_REQUEST_CODE:
                Toast.makeText(this, "Видео сохранено", Toast.LENGTH_LONG).show();
                break;
        }
    }


}
