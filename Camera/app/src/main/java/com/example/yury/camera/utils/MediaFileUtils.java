package com.example.yury.camera.utils;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.yury.camera.model.MediaType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaFileUtils {

    private static final String TAG = "MediaFileUtils";

    public static File getOutputMediaFile(MediaType type) {
        File mediaStorageDir = makeFolderByParentDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "camera");
        if (mediaStorageDir == null) return null;

        //Create media file
        String currentTime = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        File mediaFile;
        switch (type) {
            case IMAGE:
                File imageDir = makeFolderByParentDir(mediaStorageDir, type.name());

                mediaFile = new File(imageDir, currentTime + ".jpg");
                Log.i(TAG, "NEXT image will saved in " + mediaFile.getPath());
                break;
            case VIDEO:
                File videoDir = makeFolderByParentDir(mediaStorageDir, type.name());

                mediaFile = new File(videoDir, currentTime + ".mp4");
                Log.i(TAG, "NEXT video will saved in " + mediaFile.getPath());
                break;
            default:
                return null;
        }

        return mediaFile;
    }

    public static Uri getOutputMediaFileUri(MediaType type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File makeFolderByParentDir(File parentDir, String nameNewDir) {
        File newDir = new File(parentDir, nameNewDir);

        if (!newDir.exists()) {
            if (!newDir.mkdir()) {
                String errorMsg = "Can't create directory in " + Environment.DIRECTORY_PICTURES;
                Log.e(TAG, errorMsg);
                throw new RuntimeException(errorMsg);
            }
        }
        return newDir;
    }
}
