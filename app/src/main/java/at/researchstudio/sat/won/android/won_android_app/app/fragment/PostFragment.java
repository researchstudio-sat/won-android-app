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

    private TextView postDescription; //the description of the post

    private ImageView postType; //the post type icon
    private TextView postTypeText; //the written type of the post

    private LinearLayout postTagHolder; //the tag holder //TODO: CHANGE THIS TO A FLOWLAYOUT (COSTUMLAYOUT)
    private TextView postDate; //a nicely formatted date/time string
    private ImageView postDateType; //Binds either a Calendar Icon or a recurring circle icon

    private ImagePagerAdapter mImagePagerAdapter;
    private ViewPager mImagePager;
    private IconPageIndicator mIconPageIndicator;
    private MapView mMapView;
    private ScrollView mScrollView;
    private LetterTileProvider tileProvider;

    private RelativeLayout mapLayout;

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

        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        postDescription = (TextView) rootView.findViewById(R.id.post_description);
        postType = (ImageView) rootView.findViewById(R.id.post_type);
        postTypeText = (TextView) rootView.findViewById(R.id.post_type_text);
        postTagHolder = (LinearLayout) rootView.findViewById(R.id.post_tag_holder);
        postDate = (TextView) rootView.findViewById(R.id.post_calendar_time);
        postDateType = (ImageView) rootView.findViewById(R.id.post_calendar_icon);
        mImagePager = (ViewPager) rootView.findViewById(R.id.image_pager);
        mIconPageIndicator = (IconPageIndicator) rootView.findViewById(R.id.image_pager_indicator);
        mapLayout = (RelativeLayout) rootView.findViewById(R.id.map_layout);
        mMapView = (MapView) rootView.findViewById(R.id.post_map);


        mScrollView = (ScrollView) rootView.findViewById(R.id.post_scrollview);
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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        activity.showLoading();
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
            //TODO: IMPLEMENT MENU FOR MYPOST AND FOR MATCHED POST
            /*getActivity().getMenuInflater().inflate(R.menu.list, menu);
            MenuItem searchViewItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchViewItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mPostListItemAdapter.getFilter().filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mPostListItemAdapter.getFilter().filter(newText);
                    return true;
                }
            });*/
        }
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
            ab.setIcon(new BitmapDrawable(getResources(), activity.getImageLoaderService().getCroppedBitmap(titleImageUrl)));
        }else{
            final int tileSize = getResources().getDimensionPixelSize(R.dimen.letter_tile_size);
            final Bitmap letterTile = tileProvider.getLetterTile(post.getTitle(), post.getTitle(), tileSize, tileSize);

            ab.setIcon(new BitmapDrawable(getResources(), letterTile));
        }
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
            //TODO: INSERT CACHED RESULTS, WITHOUT CALL OF NEW THINGY
            putPostInView(tempPost);
        }

        protected void onPostExecute(Post tempPost) {
            putPostInView(tempPost);
        }

        private void putPostInView(Post tempPost) {
            post = tempPost;
            styleActionBar();

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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            params.setMargins(10,10,10,10);

            int margin = (int) getResources().getDimension(R.dimen.create_edit_margin_lr);
            params.setMargins(margin, margin, margin, margin);

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


            RelativeLayout imageContainer = ((RelativeLayout) activity.findViewById(R.id.image_container));

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
            activity.hideLoading();
        }
    }
}
