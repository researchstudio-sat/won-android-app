/*
 * Copyright 2015 Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.service.ImageLoaderService;
import at.researchstudio.sat.won.android.won_android_app.app.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by fsuda on 25.08.2014.
 */
public class ImageFragment extends Fragment{
    private static final String LOG_TAG = ImageFragment.class.getSimpleName();
    public static final String ARG_IMAGE_URL = "image_url";
    public static final String ARG_IMAGE_EDITABLE = "image_editable";
    public static final String ARG_TITLE_IMAGE = "title_image";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageLoaderService mImgLoader;
    private ImageView createPostImage;
    private LinearLayout titleImageIndicator;

    private boolean addFlag;
    private boolean titleImageFlag;
    private boolean editable=true;
    private String mImageUrl;

    private MainActivity activity;

    private CreateTask createTask;

    public ImageFragment(){
        super();
    }

    //*****************FRAGMENT LIFECYCLE************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        createPostImage = (ImageView) rootView.findViewById(R.id.image);
        titleImageIndicator = (LinearLayout) rootView.findViewById(R.id.title_image_indicator);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();

        mImgLoader = new ImageLoaderService(activity);

        Bundle args = getArguments();

        if(mImageUrl == null && args == null){
            addFlag = true;
        }else{
            if(mImageUrl== null || mImageUrl.trim().length()==0){
                mImageUrl = args.getString(ARG_IMAGE_URL);
            }
            editable = args.getBoolean(ARG_IMAGE_EDITABLE);
            titleImageFlag = args.getBoolean(ARG_TITLE_IMAGE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        createTask = new CreateTask();
        createTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(createTask != null && createTask.getStatus() == AsyncTask.Status.RUNNING) {
            createTask.cancel(true);
        }
    }

    //***********************************************************************************************

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO: BUG ADDING AN IMAGE CHANGES THE ACTIONBAR TO A VIEWPOST ACTIONBAR
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            try {
                FileOutputStream out = new FileOutputStream(FileUtils.createNewImageFile().getAbsolutePath());
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                activity.getTempPost().removeLastAddedImage();
                Log.e(LOG_TAG, "ERROR WHILE PROCESSING CAMERA PICTURE: " + e.getMessage());
            }
        }else if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_CANCELED){
            activity.getTempPost().removeLastAddedImage(); //REMOVE IMAGE AGAIN IF THE REQUEST IS CANCELED
        }else if(requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK){
            if(data.getData() != null) {
                Uri _uri = data.getData();

                //User had pick an image.
                Cursor cursor = getActivity().getContentResolver().query(_uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                cursor.moveToFirst();

                //Link to the image
                final String imageFilePath = cursor.getString(0);

                activity.getTempPost().addImage(imageFilePath);
                cursor.close();
            }

        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    protected void dispatchGalleryPictureIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK);
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
                    photoFile = FileUtils.createNewImageFile();
                }catch(IOException ioe){
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_saving_picture), Toast.LENGTH_SHORT).show();
                }

                if (photoFile != null) {
                    mImageUrl = photoFile.getAbsolutePath();

                    activity.getTempPost().addImage(mImageUrl);

                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_saving_picture), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void displayDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete_image_text);
        builder.setTitle(R.string.dialog_delete_image_title);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOG_TAG, "Delete image with url: "+mImageUrl);
                activity.getTempPost().removeImage(mImageUrl);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOG_TAG, "DIALOG NO");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displaySetTitleImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_set_title_image_text);
        builder.setTitle(R.string.dialog_set_title_image_title);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                //TODO: UPDATE THE TITLE IMAGE
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //NOTHING NEEDS TO BE DONE
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class CreateTask  extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return null; //TODO: I DONT KNOW IF THERE NEEDS TO BE DIFFERENT THINGS HAPPENING HERE
        }

        @Override
        protected void onPostExecute(String s) {
            if(editable) { //ONLY ALLOW THE 'FONDLING' OF IMAGES WHEN IT IS IN EDIT MODE
                createPostImage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!addFlag) {
                            displayDeleteDialog();
                            return true; //prohibit further processing
                        }
                        return false;
                    }
                });

                createPostImage.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (addFlag) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(R.string.dialog_select_from)
                                    .setItems(R.array.image_picker, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch(which){
                                                case 0:
                                                    dispatchTakePictureIntent();
                                                    break;
                                                case 1:
                                                    dispatchGalleryPictureIntent();
                                                    break;
                                            }
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            displaySetTitleImageDialog();
                        }
                    }
                });
            }

            if(addFlag) {
                createPostImage.setImageResource(R.drawable.add_image);
                titleImageIndicator.setVisibility(View.GONE);
            }else{
                mImgLoader.displayImage(mImageUrl, R.drawable.image_placeholder_donotcommit, createPostImage);
                titleImageIndicator.setVisibility(titleImageFlag ? View.VISIBLE : View.GONE);
            }

            super.onPostExecute(s);
        }
    }
}
