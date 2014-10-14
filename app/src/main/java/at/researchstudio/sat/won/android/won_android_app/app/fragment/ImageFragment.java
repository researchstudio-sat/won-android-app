package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.service.ImageLoaderService;
import com.viewpagerindicator.IconPageIndicator;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by fsuda on 25.08.2014.
 */
public class ImageFragment extends Fragment{
    private static final String LOG_TAG = ImageFragment.class.getSimpleName();
    public static final String ARG_IMAGE_URL = "image_url";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ViewPager mImageViewPager;
    private ImagePagerAdapter mImagePagerAdapter;
    private IconPageIndicator mIconPageIndicator;
    private ImageLoaderService mImgLoader;

    private boolean addFlag;
    private String imageUrl;

    public ImageFragment(){
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"ON CREATE VIEW");
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        mImageViewPager = (ViewPager) getActivity().findViewById(R.id.create_image_pager);
        mImagePagerAdapter = (ImagePagerAdapter) ((ViewPager) getActivity().findViewById(R.id.create_image_pager)).getAdapter();
        mIconPageIndicator = (IconPageIndicator) getActivity().findViewById(R.id.create_image_pager_indicator);
        mImgLoader = new ImageLoaderService(getActivity());

        Bundle args = getArguments();

        final ImageView createPostImage = (ImageView) rootView.findViewById(R.id.create_image);
        //TODO: MOVE THIS LOADING THINGY TO AN ASYNCTASK OR SOMETHING

        if((imageUrl==null) && (args == null)) {
            createPostImage.setImageResource(R.drawable.add_image);
            addFlag = true;
        }else{
            if((imageUrl==null) || (imageUrl.trim().equals(""))) {
                imageUrl = args.getString(ARG_IMAGE_URL);
            }
            Log.d(LOG_TAG, "ImageUrl: " + imageUrl);
            mImgLoader.displayImage(imageUrl, R.drawable.image_placeholder_donotcommit ,createPostImage);
        }

        createPostImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!addFlag){
                    Log.d(LOG_TAG,"LONG CLICK ON EXISTING IMAGE");

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.dialog_delete_image_text);
                    builder.setTitle(R.string.dialog_delete_image_title);

                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(LOG_TAG,"DIALOG YES");
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

                    return true; //prohibit further processing
                }
                return false;
            }
        });

        createPostImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(addFlag) {
                    Log.d(LOG_TAG, "ADD IMAGE WAS CLICKED!");
                    imageUrl = Mock.generateRandomImageUrl();
                    mImagePagerAdapter.addItem(imageUrl);
                    mImgLoader.displayImage(imageUrl, R.drawable.image_placeholder_donotcommit, createPostImage);
                    mImagePagerAdapter.notifyDataSetChanged();
                    mIconPageIndicator.notifyDataSetChanged();

                    addFlag = false;
                    /* TODO: THIS IS THE CAMERA INTENT STUFF
                    if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_no_camera), Toast.LENGTH_SHORT).show();
                    }*/
                }else {
                    Log.d(LOG_TAG, createPostImage.toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.dialog_set_title_image_text);
                    builder.setTitle(R.string.dialog_set_title_image_title);

                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(LOG_TAG,"DIALOG YES");
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
        });

        
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG,"ON DESTROY VIEW");
        super.onDestroyView();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(LOG_TAG,"ON ACTIVITY RESULT");
        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ImageView mImageView = (ImageView) getActivity().findViewById(R.id.imageView1);
            mImageView.setImageBitmap(imageBitmap);
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }*/
        super.onActivityResult(requestCode,resultCode,data);
    }
}
