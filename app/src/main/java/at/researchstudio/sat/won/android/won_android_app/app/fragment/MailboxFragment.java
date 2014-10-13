package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;

import java.io.*;

/**
 * Created by fsuda on 21.08.2014.
 */
public class MailboxFragment extends Fragment {
    private static final String LOG_TAG = MailboxFragment.class.getSimpleName();
    private Button cameraButton;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public String currentPhotoPath = null;
    public Uri capturedImageURI = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d(LOG_TAG, "onCreateView");

        if(savedInstanceState!=null) {
            Log.d(LOG_TAG, "path:"+savedInstanceState.getString("photoPath"));
            Log.d(LOG_TAG, "uri:"+savedInstanceState.getString("photoUri"));
        }

        View mailboxView = inflater.inflate(R.layout.fragment_mailbox, container, false);

        File imgFile = new  File("/storage/emulated/0/Pictures/won/newimage.jpg");
        if(imgFile.exists()){
            Log.d(LOG_TAG,"exists");

            Bitmap img = BitmapFactory.decodeFile("/storage/emulated/0/Pictures/won/newimage.jpg");
            Log.d(LOG_TAG,"bitmap: "+img);

            ImageView myImage = (ImageView) mailboxView.findViewById(R.id.imageView1);

            myImage.setImageBitmap(img);

        }else{
            Log.d(LOG_TAG,"doesnt exist");
        }

        cameraButton = (Button) mailboxView.findViewById(R.id.camera);
        cameraButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        getActivity().setTitle(R.string.mi_mailbox);
        return mailboxView;
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstance");
        super.onSaveInstanceState(outState);

        if(currentPhotoPath!=null) {
            outState.putString("photoPath", currentPhotoPath);
            Log.d(LOG_TAG,currentPhotoPath);
        }
        if(capturedImageURI!=null) {
            outState.putString("photoUri", capturedImageURI.toString());
            Log.d(LOG_TAG,capturedImageURI.toString());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView mImageView = (ImageView) getActivity().findViewById(R.id.imageView1);
            mImageView.setImageBitmap(imageBitmap);*/

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            try {
                FileOutputStream out = new FileOutputStream("/storage/emulated/0/Pictures/won/newimage.jpg");
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    protected void dispatchTakePictureIntent() {
        if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_no_camera), Toast.LENGTH_SHORT).show();
        }else{
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                // If you don't do this, you may get a crash in some devices.
                File photoFile = null;

                try {
                    photoFile = createImageFile();
                }catch(IOException ioe){
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_saving_picture), Toast.LENGTH_SHORT).show();
                }

                if (photoFile != null) {
                    Uri fileUri = Uri.fromFile(photoFile);
                    capturedImageURI = fileUri;
                    currentPhotoPath = fileUri.getPath();

                    Log.d(LOG_TAG,currentPhotoPath);

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_saving_picture), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected File createImageFile() throws IOException{
        File f = new File(getAlbumStorageDir("won"), "newimage.jpg");
        Log.d(LOG_TAG, f.getAbsolutePath());
        return f;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("getAlbumStorageDir", "Directory not created");
        }
        return file;
    }
}
