package dk.makeable.testcamera;

import android.Manifest;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.alhazmy13.gota.Gota;
import net.alhazmy13.gota.GotaResponse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Gota.OnRequestPermissionsBack {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAMERA_PHOTO = 111;
    private Uri imageToUploadUri;
    private String mCurrentPhotoPath;
    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn = (Button) findViewById(R.id.test);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(CAMERA_PHOTO);
            }
        });


    }

    private void checkPermission(int id) {
        new Gota.Builder(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestId(id)
                .setListener(this)
                .check();
    }

    @Override
    public void onRequestBack(int requestId, @NonNull GotaResponse gotaResponse) {
        if (gotaResponse.isGranted(android.Manifest.permission.CAMERA) && gotaResponse.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE) && gotaResponse.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.d("TEST", "Got permission ");
//            mConstraintLayout.setVisibility(View.VISIBLE);
            try {
                dispatchTakePictureIntent(CAMERA_PHOTO);
            } catch (IOException e) {
                Log.d("ERROR", "THIS: " + e.toString());
                e.printStackTrace();
            }
        } else {
            Log.d("TEST", "No permission ");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PHOTO && resultCode == RESULT_OK) {
            Log.d("FILES", "Files1: " + "TEST2?!?!?!");
            final Uri imageUri = Uri.parse(mCurrentPhotoPath);
            Log.d("FILES", "Files1: " + "TEST2?!?!?! " + imageUri);
//            MediaScannerConnection.scanFile(((MainActivity) getBaseContext()),
//                    new String[]{imageUri.getPath()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
////                            getActivity().runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    Log.d(TAG, "TEST IMAGEURI: " + imageUri);
////                                }
////                            });
//                        }
//                    });
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Jobilant");
        storageDir.mkdir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.d("FILES", "FILE: "+ mCurrentPhotoPath);
        return image;
    }

    private void dispatchTakePictureIntent(int id) throws IOException {
        Log.d("FILES", "Files1: " + "TEST?!?!?");
        android.content.Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("FILES", "Files1: " + photoFile.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(createImageFile());
                Log.d("FILES", "Files2: " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, id);
            }
        } else {
            Log.d(TAG, "NULL");
        }
    }
}
