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

import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
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
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.util.StringUtils;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.IconPageIndicator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by fsuda on 21.08.2014.
 */
public class PostFragment extends Fragment {
    private static final String LOG_TAG = PostFragment.class.getSimpleName();
    private static final String MAP_STATE_KEY = "POST_MAP_STATE";

    private String postId;

    private TextView postTitle;
    private TextView postObject; //TODO: REMOVE THIS STUB
    private TextView postDescription;
    private ImageView postType;
    private ImagePagerAdapter mImagePagerAdapter;
    private ViewPager mImagePager;
    private IconPageIndicator mIconPageIndicator;
    private MapView mMapView;

    private Geocoder mGeocoder;
    private GoogleMap map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"ON CREATE VIEW");
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
            Log.d(LOG_TAG,"Fragment started with postId: "+postId);
        }

        Post post = Mock.myMockPosts.get(UUID.fromString(postId)); //TODO: REMOVE THIS STUFF FROM THE CREATEVIEW PART
        if(post==null){
            post = Mock.myMockMatches.get(UUID.fromString(postId)); //TODO: REMOVE THIS STUFF FROM THE CREATEVIEW PART
        }
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        postTitle = (TextView) rootView.findViewById(R.id.post_title);
        postTitle.setText(post.getTitle());

        postDescription = (TextView) rootView.findViewById(R.id.post_description);
        postDescription.setText(post.getDescription());

        postObject = (TextView) rootView.findViewById(R.id.post_object);
        postObject.setText(post.toString());

        postType = (ImageView) rootView.findViewById(R.id.post_type);

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
        }

        ((MainActivity) getActivity()).setTitle(post.getTitle()); //TODO: SET CORRECT TITLE

        //******INIT IMAGE PAGER **********
        //Initialize ImagePager
        mImagePagerAdapter = new ImagePagerAdapter(getActivity().getFragmentManager(),false);

        //TODO: SHOW NOTHING IF THERE ARE NO IMAGES PRESENT
        if(post.getTitleImageUrl()!=null) {
            mImagePagerAdapter.addItem(post.getTitleImageUrl());
        }
        if(post.getImageUrls()!=null) {
            for (String imgUrl : post.getImageUrls()) {
                mImagePagerAdapter.addItem(imgUrl);
            }
        }

        mImagePager = (ViewPager) rootView.findViewById(R.id.image_pager);
        mImagePager.setAdapter(mImagePagerAdapter);

        mIconPageIndicator = (IconPageIndicator) rootView.findViewById(R.id.image_pager_indicator);

        mIconPageIndicator.setViewPager(mImagePager);


        //Initialize GMaps
        MapsInitializer.initialize(getActivity());
        mGeocoder = new Geocoder(getActivity(), Locale.getDefault());

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


        Log.d(LOG_TAG,"DONE WITH INITIALIZING THE POST FRAGMENT VIEW");
        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG,"ON RESUME");
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onDestroy() {
        Log.d(LOG_TAG,"ON DESTROY");
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        Log.d(LOG_TAG,"ON LOW MEMORY");
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
