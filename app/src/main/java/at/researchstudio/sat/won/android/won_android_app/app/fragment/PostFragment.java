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

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.MessageListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.util.StringUtils;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.IconPageIndicator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by fsuda on 21.08.2014.
 */
public class PostFragment extends Fragment {
    private static final String LOG_TAG = PostFragment.class.getSimpleName();
    private static final String MAP_STATE_KEY = "POST_MAP_STATE";

    private CreateTask createTask;

    private String postId;
    private String refPostTitle; //title of the reference post

    private TextView postTitle;
    private TextView postObject; //TODO: REMOVE THIS STUB
    private TextView postDescription;
    private ImageView postType;
    private ImagePagerAdapter mImagePagerAdapter;
    private ViewPager mImagePager;
    private IconPageIndicator mIconPageIndicator;
    private MapView mMapView;

    private MainActivity activity;

    private Geocoder mGeocoder;
    private GoogleMap map;

    private Post post;

    @Override
    public void onStart() {
        super.onStart();
        createTask = new CreateTask();
        createTask.execute();
    }


    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy trying to cancel createListTask");
        super.onDestroy();
        mMapView.onDestroy();
        if(createTask != null && createTask.getStatus() == AsyncTask.Status.RUNNING) {
            createTask.cancel(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"ON CREATE VIEW");
        Bundle args = getArguments();

        activity = (MainActivity) getActivity();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
            refPostTitle=args.getString(Post.TITLE_REF);
            Log.d(LOG_TAG,"Fragment started with postId: "+postId+" : "+refPostTitle);
        }else{
            postId=null;
            refPostTitle=null;
        }

        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        postDescription = (TextView) rootView.findViewById(R.id.post_description);
        postObject = (TextView) rootView.findViewById(R.id.post_object);


        /*postType = (ImageView) rootView.findViewById(R.id.post_type);

        switch(post.getType()){
            case OFFER:
                postType.setImageResource(R.drawable.offer);
                break;
            case WANT:
                postType.setImageResource(R.drawable.want);
                break;
            case ACTIVITY:
                postType.setImageResource(R.drawable.activity);
                break;
            case CHANGE:
                postType.setImageResource(R.drawable.change);
                break;
        }*/

        //******INIT IMAGE PAGER **********
        //Initialize ImagePager
        mImagePagerAdapter = new ImagePagerAdapter(activity.getFragmentManager(),false);

        mImagePager = (ViewPager) rootView.findViewById(R.id.image_pager);
        mImagePager.setSaveFromParentEnabled(false);  //This is necessary because it prevents the ViewPager from being messed up on pagechanges and popbackstack's


        mIconPageIndicator = (IconPageIndicator) rootView.findViewById(R.id.image_pager_indicator);


        //Initialize GMaps
        MapsInitializer.initialize(activity);
        mGeocoder = new Geocoder(activity, Locale.getDefault());

        mMapView = (MapView) rootView.findViewById(R.id.post_map);
        //*********** 'HACK' TO FIX PARCEABLE BUG see darnmason post in http://stackoverflow.com/questions/13900322/badparcelableexception-in-google-maps-code
        Bundle mapState = null;
        if(savedInstanceState != null) {
            mapState = new Bundle();
            mapState.putBundle(MAP_STATE_KEY, savedInstanceState.getBundle(MAP_STATE_KEY));
        }

        mMapView.onCreate(mapState);
        //mMapView.onCreate(savedInstanceState);
        //****************************


        // Gets to GoogleMap from the MapView and does initialization stuff
        if(mMapView!=null)
        {
            map = mMapView.getMap();
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(true);
        }

        Log.d(LOG_TAG,"DONE WITH INITIALIZING THE POST FRAGMENT VIEW");
        return rootView;
    }

    private class CreateTask extends AsyncTask<String, Integer, Post> {
        @Override
        protected Post doInBackground(String... params) {
            if(refPostTitle!=null){
                return activity.getPostService().getMatchById(postId);
            }else{
                return activity.getPostService().getMyPostById(postId);
            }
        }

        @Override
        protected void onCancelled(Post tempPost) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //TODO: INSERT CACHED RESULTS, WITHOUT CALL OF NEW THINGY

            if(tempPost != null) {
                post = tempPost;

                //TODO: SHOW NOTHING IF THERE ARE NO IMAGES PRESENT
                postObject.setText(post.toString());
                postDescription.setText(post.getDescription());

                if(post.getTitleImageUrl()!=null) {
                    mImagePagerAdapter.addItem(post.getTitleImageUrl());
                }
                if(post.getImageUrls()!=null) {
                    for (String imgUrl : post.getImageUrls()) {
                        mImagePagerAdapter.addItem(imgUrl);
                    }
                }
                mImagePager.setAdapter(mImagePagerAdapter);
                mIconPageIndicator.setViewPager(mImagePager);

                styleActionBar();

                try {
                    List<Address> adresses = mGeocoder.getFromLocation(post.getLocation().latitude, post.getLocation().longitude, 1);

                    String address="";

                    if(adresses!=null && adresses.size()>0){
                        address = StringUtils.getFormattedAddress(adresses.get(0));
                    }else{
                        Log.d(LOG_TAG, "No Address found");
                        address=post.getTitle();
                    }

                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(post.getLocation())
                            .title(post.getTitle())
                            .snippet(address)); //TODO: MultiLine Snippet see --> http://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(post.getLocation(), 10);
                    map.animateCamera(cameraUpdate);
                }catch (IOException ioe){
                    //TODO: ERROR TOAST
                    Log.e(LOG_TAG,ioe.getMessage());
                }
            }
        }

        protected void onPostExecute(Post tempPost) {
            post = tempPost;
            styleActionBar();

            //TODO: SHOW NOTHING IF THERE ARE NO IMAGES PRESENT
            postObject.setText(post.toString());
            postDescription.setText(post.getDescription());

            if(post.getTitleImageUrl()!=null) {
                mImagePagerAdapter.addItem(post.getTitleImageUrl());
            }
            if(post.getImageUrls()!=null) {
                for (String imgUrl : post.getImageUrls()) {
                    mImagePagerAdapter.addItem(imgUrl);
                }
            }
            mImagePager.setAdapter(mImagePagerAdapter);
            mIconPageIndicator.setViewPager(mImagePager);

            try {
                List<Address> adresses = mGeocoder.getFromLocation(post.getLocation().latitude, post.getLocation().longitude, 1);

                String address="";

                if(adresses!=null && adresses.size()>0){
                    address = StringUtils.getFormattedAddress(adresses.get(0));
                }else{
                    Log.d(LOG_TAG, "No Address found");
                    address=post.getTitle();
                }

                Marker marker = map.addMarker(new MarkerOptions()
                        .position(post.getLocation())
                        .title(post.getTitle())
                        .snippet(address)); //TODO: MultiLine Snippet see --> http://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(post.getLocation(), 10);
                map.animateCamera(cameraUpdate);
            }catch (IOException ioe){
                //TODO: ERROR TOAST
                Log.e(LOG_TAG,ioe.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG,"ON RESUME");
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onLowMemory() {
        Log.d(LOG_TAG,"ON LOW MEMORY");
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void styleActionBar(){
        ActionBar ab = activity.getActionBar();
        String titleImageUrl = post.getTitleImageUrl();

        activity.setDrawerToggle(false); //DISABLE THE NAVDRAWER -> POSTFRAGMENT IS A LOWLEVEL VIEW
        ab.setTitle(post.getTitle());
        ab.setSubtitle(refPostTitle != null? getString(R.string.to)+" "+refPostTitle: null);

        if(titleImageUrl!=null) {
            Bitmap srcBmp = activity.getImageLoaderService().getBitmap(titleImageUrl);
            Bitmap dstBmp;
            //***********CROP BITMAP
            if (srcBmp.getWidth() >= srcBmp.getHeight()){

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                        0,
                        srcBmp.getHeight(),
                        srcBmp.getHeight()
                );

            }else{

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                        srcBmp.getWidth(),
                        srcBmp.getWidth()
                );
            }
            //**********************

            ab.setIcon(new BitmapDrawable(getResources(), dstBmp));
        }else{
            switch(post.getType()){
                case OFFER:
                    ab.setIcon(R.drawable.offer);
                    break;
                case WANT:
                    ab.setIcon(R.drawable.want);
                    break;
                case ACTIVITY:
                    ab.setIcon(R.drawable.activity);
                    break;
                case CHANGE:
                    ab.setIcon(R.drawable.change);
                    break;
            }
        }
    }
}
