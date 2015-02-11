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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.components.LetterTileProvider;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.util.StringUtils;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.IconPageIndicator;
import com.wefika.flowlayout.FlowLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by fsuda on 21.08.2014.
 */
public class PostFragment extends Fragment {
    private static final String LOG_TAG = PostFragment.class.getSimpleName();
    private static final String MAP_STATE_KEY = "POST_MAP_STATE";

    private CreateTask createTask;

    private String postId;
    private String refPostTitle; //title of the reference post

    private View rootView;
    private TextView postDescription; //the description of the post

    private ImageView postType; //the post type icon
    private TextView postTypeText; //the written type of the post

    private FlowLayout postTagHolder; //the tag holder
    private TextView postDate; //a nicely formatted date/time string
    private ImageView postDateType; //Binds either a Calendar Icon or a recurring circle icon

    private ImagePagerAdapter mImagePagerAdapter;
    private ViewPager mImagePager;
    private IconPageIndicator mIconPageIndicator;
    private MapView mMapView;
    private ScrollView mScrollView;
    private LetterTileProvider tileProvider;

    private RelativeLayout mapLayout;
    private RelativeLayout imageContainer;

    private MainActivity activity;

    private Geocoder mGeocoder;
    private GoogleMap map;

    private Post post;

    //*******FRAGMENT LIFECYCLE************************************************************************************
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
            refPostTitle=args.getString(Post.TITLE_REF);
        }else{
            postId=null;
            refPostTitle=null;
        }

        rootView = inflater.inflate(R.layout.fragment_post, container, false);

        postDescription = (TextView) rootView.findViewById(R.id.post_description);
        postType = (ImageView) rootView.findViewById(R.id.post_type);
        postTypeText = (TextView) rootView.findViewById(R.id.post_type_text);
        postTagHolder = (FlowLayout) rootView.findViewById(R.id.post_tag_holder);
        postDate = (TextView) rootView.findViewById(R.id.post_calendar_time);
        postDateType = (ImageView) rootView.findViewById(R.id.post_calendar_icon);
        mImagePager = (ViewPager) rootView.findViewById(R.id.image_pager);
        mIconPageIndicator = (IconPageIndicator) rootView.findViewById(R.id.image_pager_indicator);
        mapLayout = (RelativeLayout) rootView.findViewById(R.id.create_location_container);
        mMapView = (MapView) rootView.findViewById(R.id.post_map);
        imageContainer = (RelativeLayout) rootView.findViewById(R.id.image_container);

        mScrollView = (ScrollView) rootView.findViewById(R.id.post_scrollview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        activity.showLoading();//TODO: BUG SHOWS LOADING IF AN ADJACENT TAB IS CURRENTLY VISIBLE DUE TO THE PAGER HOLDING 3 VIEWS AT A TIME

        tileProvider = new LetterTileProvider(activity);

        //Initialize GMaps
        MapsInitializer.initialize(activity);
        mGeocoder = new Geocoder(activity, Locale.getDefault());

        //*********** 'HACK' TO FIX PARCEABLE BUG see darnmason post in http://stackoverflow.com/questions/13900322/badparcelableexception-in-google-maps-code
        Bundle mapState = null;
        if(savedInstanceState != null) {
            mapState = new Bundle();
            mapState.putBundle(MAP_STATE_KEY, savedInstanceState.getBundle(MAP_STATE_KEY));
        }

        mMapView.onCreate(mapState);
        //****************************

        // Gets to GoogleMap from the MapView and does initialization stuff
        if(mMapView!=null)
        {
            map = mMapView.getMap();
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(true);
        }

        ImageView transparentImageView = (ImageView) rootView.findViewById(R.id.transparent_image);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
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
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if(createTask != null && createTask.getStatus() == AsyncTask.Status.RUNNING) {
            createTask.cancel(true);
        }
    }
    //*************************************************************************************************************

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(activity.isDrawerOpen()){
            super.onCreateOptionsMenu(menu, inflater);
        }else {
            menu.clear(); //THIS IS ALL A LITTLE WEIRD STILL NOT SURE IF THIS IS AT ALL BEST PRACTICE

            if(isMyPost()) {
                getActivity().getMenuInflater().inflate(R.menu.mypost, menu);
            }else{
                getActivity().getMenuInflater().inflate(R.menu.post, menu);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case (R.id.action_close_post):
                displayCloseDialog();
                return true;
            case (R.id.action_draft_from_post):
                activity.createDraft(postId);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isMyPost(){
        return refPostTitle == null || "".equals(refPostTitle.trim());
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
        ab.setSubtitle(!isMyPost()? getString(R.string.to)+" "+refPostTitle : null);

        if(titleImageUrl!=null) {
            ab.setIcon(new BitmapDrawable(getResources(), activity.getImageLoaderService().getCroppedBitmap(titleImageUrl)));
        }else{
            final int tileSize = getResources().getDimensionPixelSize(R.dimen.letter_tile_size);
            final Bitmap letterTile = tileProvider.getLetterTile(post.getTitle(), post.getTitle(), tileSize, tileSize);

            ab.setIcon(new BitmapDrawable(getResources(), letterTile));
        }
    }

    private void displayCloseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_close_mypost));
        builder.setTitle(getString(R.string.dialog_close_mypost_title));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.setTempPost(activity.getPostService().closePost(postId)); //TODO: DOES NOT REALLY CLOSE THE POST YET...
                Toast.makeText(activity, getString(R.string.toast_close_mypost), Toast.LENGTH_SHORT).show();
                activity.popBackStackIfPossible();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DO NOTHING
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class CreateTask extends AsyncTask<String, Integer, Post> {
        @Override
        protected Post doInBackground(String... params) {
            return !isMyPost()? activity.getPostService().getMatchById(postId) : activity.getPostService().getMyPostById(postId);
        }

        @Override
        protected void onCancelled(Post tempPost) {
            //TODO: INSERT CACHED RESULTS, WITHOUT CALL OF NEW THINGY
            putPostInView(tempPost);
        }

        protected void onPostExecute(Post tempPost) {
            putPostInView(tempPost);
            /*TODO: BUG putPostInView is called even though the view isnt visible --> (probably due to some backStack issue)
            leads to the styleActionBar() method even though its not supposed to execute anything --> happens on app resume (postfragment view must have been visible once already)*/
        }

        private void putPostInView(Post tempPost) {
            post = tempPost;

            //Set PostType
            switch(post.getType()){
                case OFFER:
                    postType.setImageResource(R.drawable.offer_light);
                    postTypeText.setText(R.string.type_offer);
                    break;
                case WANT:
                    postType.setImageResource(R.drawable.want_light);
                    postTypeText.setText(R.string.type_want);
                    break;
                case ACTIVITY:
                    postType.setImageResource(R.drawable.activity_light);
                    postTypeText.setText(R.string.type_activity);
                    break;
                case CHANGE:
                    postType.setImageResource(R.drawable.change_light);
                    postTypeText.setText(R.string.type_change);
                    break;
            }

            //Set RepeatType
            switch(post.getRepeat()){
                case NONE:
                    postDateType.setImageResource(R.drawable.calendar);
                    break;
                case WEEKLY:
                    postDateType.setImageResource(R.drawable.calendar_repeat);
                    break;
                case MONTHLY:
                    postDateType.setImageResource(R.drawable.calendar_repeat);
                    break;
            }
            postDate.setText(post.getFormattedDate());

            //Set Tags
            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            params.setMargins(10,10,10,10);

            int margin = (int) getResources().getDimension(R.dimen.create_edit_margin_lr);
            params.setMargins(margin, margin, margin, margin);

            if(postTagHolder.getChildCount()>0) {
                postTagHolder.removeAllViews();
            }

            for(String tag : post.getTags()) {
                TextView tv = new TextView(activity);
                tv.setText(tag);
                tv.setTextColor(getResources().getColor(R.color.post_tag_text));
                tv.setLayoutParams(params);
                tv.setBackgroundResource(R.drawable.tag_bg);
                postTagHolder.addView(tv);
            }

            //Set Description
            postDescription.setText(post.getDescription());

            //Set Images
            mImagePagerAdapter = new ImagePagerAdapter(activity, false);
            mImagePager.setSaveFromParentEnabled(false); //This is necessary because it prevents the ViewPager from being messed up on pagechanges and popbackstack's

            boolean imgsPresent = false;
            int imageCount = 0;


            if(post.getTitleImageUrl()!=null && tempPost.getTitleImageUrl().trim().length()>0) {
                mImagePagerAdapter.addItem(post.getTitleImageUrl());
                imageCount++;
            }

            for (String imgUrl : post.getOtherImageUrls()) {
                mImagePagerAdapter.addItem(imgUrl);
                imageCount++;
            }

            if(imageCount>0) {
                imageContainer.setVisibility(View.VISIBLE);
                mIconPageIndicator.setVisibility(imageCount>1? View.VISIBLE : View.GONE);
                mImagePager.setAdapter(mImagePagerAdapter);
                mIconPageIndicator.setViewPager(mImagePager);
                mIconPageIndicator.notifyDataSetChanged();
                imageContainer.setBackgroundResource(R.color.post_images);
            }else{
                imageContainer.setVisibility(View.GONE);
            }

            //Set Location
            try {
                LatLng lng = post.getLocation();
                if(lng == null || (lng.latitude == 0.0 && lng.longitude == 0.0)){
                    Log.d(LOG_TAG,"the adress is null do not show the map");
                    mapLayout.setVisibility(View.GONE);
                }else{
                    mapLayout.setVisibility(View.VISIBLE);
                    List<Address> adresses = mGeocoder.getFromLocation(post.getLocation().latitude, post.getLocation().longitude, 1);

                    String address;

                    if(adresses!=null && adresses.size()>0){
                        address = StringUtils.getFormattedAddress(adresses.get(0));
                    }else{
                        Log.d(LOG_TAG, "No Address found");
                        address=post.getTitle();
                    }

                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(post.getLocation())
                            .title(post.getTitle())
                            .snippet(address)
                            .draggable(false)); //TODO: MultiLine Snippet see --> http://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(post.getLocation(), 10);
                    map.animateCamera(cameraUpdate);
                }
            }catch (IOException ioe){
                mapLayout.setVisibility(View.GONE); //No ErrorToast just pretend there was never a location anyway
                Log.e(LOG_TAG,ioe.getMessage());
            }
            styleActionBar();
            activity.hideLoading();
        }
    }
}
