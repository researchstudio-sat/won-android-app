/*
 * Copyright 2014 Research Studios Austria Forschungsges.m.b.H.
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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.service.ImageLoaderService;
import at.researchstudio.sat.won.android.won_android_app.app.util.FileUtils;
import com.viewpagerindicator.IconPageIndicator;

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

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ViewPager mImageViewPager;
    private ImagePagerAdapter mImagePagerAdapter;
    private IconPageIndicator mIconPageIndicator;
    private ImageLoaderService mImgLoader;
    private ImageView createPostImage;

    private boolean addFlag;
    private boolean editable=true;
    private String mImageUrl;

    public ImageFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        mImageViewPager = (ViewPager) getActivity().findViewById(R.id.image_pager);
        mImagePagerAdapter = (ImagePagerAdapter) ((ViewPager) getActivity().findViewById(R.id.image_pager)).getAdapter();
        mIconPageIndicator = (IconPageIndicator) getActivity().findViewById(R.id.image_pager_indicator);
        mImgLoader = new ImageLoaderService(getActivity());

        Bundle args = getArguments();

        createPostImage = (ImageView) rootView.findViewById(R.id.image);
        //TODO: MOVE THIS LOADING THINGY TO AN ASYNCTASK OR SOMETHING

        if((mImageUrl==null) && (args == null)) {
            createPostImage.setImageResource(R.drawable.add_image);
            addFlag = true;
        }else{
            if((mImageUrl==null) || (mImageUrl.trim().equals(""))) {
                mImageUrl = args.getString(ARG_IMAGE_URL);
            }
            editable = args.getBoolean(ARG_IMAGE_EDITABLE);

            mImgLoader.displayImage(mImageUrl, R.drawable.image_placeholder_donotcommit ,createPostImage);
        }

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
                        dispatchTakePictureIntent();
                        addFlag = false;
                    } else {
                        displaySetTitleImageDialog();
                    }
                }
            });
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            try {
                FileOutputStream out = new FileOutputStream(FileUtils.createNewImageFile().getAbsolutePath());
                photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (Exception e) {
                Log.e(LOG_TAG, "ERROR WHILE PROCESSING CAMERA PICTURE: " + e.getMessage());
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
                    photoFile = FileUtils.createNewImageFile();
                }catch(IOException ioe){
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_saving_picture), Toast.LENGTH_SHORT).show();
                }

                if (photoFile != null) {
                    mImageUrl = photoFile.getAbsolutePath();
                    mImagePagerAdapter.addItem(mImageUrl);
                    mImgLoader.displayImage(mImageUrl, R.drawable.image_placeholder_donotcommit, createPostImage);
                    mImagePagerAdapter.notifyDataSetChanged();
                    mIconPageIndicator.notifyDataSetChanged();

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
                Log.d(LOG_TAG, "DIALOG YES");
                    /*
                    //TODO: UPDATE IMAGEVIEW DELETE DOES NOT WORK CORRECTLY YET
                    int currentItem = mImageViewPager.getCurrentItem();
                    mImageViewPager.setCurrentItem(currentItem+1);
                    mImagePagerAdapter.removeItem(currentItem);
                    mImagePagerAdapter.notifyDataSetChanged();
                    mIconPageIndicator.notifyDataSetChanged();*/
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
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOG_TAG, "DIALOG YES");
                //TODO: DIALOG YES ACTION
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(LOG_TAG, "DIALOG NO");
                //TODO: DIALOG NO ACTION
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
