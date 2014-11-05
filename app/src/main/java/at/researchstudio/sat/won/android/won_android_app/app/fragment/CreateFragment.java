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
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.TypeSpinnerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostTypeSpinnerModel;
import at.researchstudio.sat.won.android.won_android_app.app.util.StringUtils;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.IconPageIndicator;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by fsuda on 21.08.2014.
 */
public class CreateFragment extends Fragment {
    private static final String LOG_TAG = CreateFragment.class.getSimpleName();
    private static final String MAP_STATE_KEY = "CREATE_MAP_STATE";
    private static final String IMAGE_URLS = "IMAGE_URLS";

    private CreateTask createTask;

    private MainActivity activity;
    private MapView mMapView;
    private Spinner mTypeSpinner;
    private TypeSpinnerAdapter mTypeSpinnerAdapter;

    private ImagePagerAdapter mImagePagerAdapter;
    private ViewPager mImagePager;
    private IconPageIndicator mIconPageIndicator;

    private Button mStartDateTimeButton;
    private Button mEndDateTimeButton;

    private EditText mLocationText;
    private EditText mTitle;
    private EditText mDescription;
    private EditText mTags;

    private GoogleMap map;
    private Geocoder mGeocoder;

    private ScrollView mScrollView;

    //***********FRAGMENT LIFECYCLE****************************************************************
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);

        mTypeSpinner = (Spinner)rootView.findViewById(R.id.typespinner);
        mImagePager = (ViewPager) rootView.findViewById(R.id.image_pager);
        mIconPageIndicator = (IconPageIndicator) rootView.findViewById(R.id.image_pager_indicator);
        mLocationText = (EditText) rootView.findViewById(R.id.create_location);
        mMapView = (MapView) rootView.findViewById(R.id.post_map);
        mStartDateTimeButton = (Button) rootView.findViewById(R.id.create_startdatetime_button);
        mEndDateTimeButton = (Button) rootView.findViewById(R.id.create_enddatetime_button);
        mTitle = (EditText) rootView.findViewById(R.id.create_title);
        mDescription = (EditText) rootView.findViewById(R.id.create_description);
        mTags = (EditText) rootView.findViewById(R.id.create_tags);

        mScrollView = (ScrollView) rootView.findViewById(R.id.create_scrollview);
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


        mStartDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                System.out.println("the selected " + mDay);

                //TODO: MAYBE IMPLEMENT THIS https://github.com/flavienlaurent/datetimepicker
                //TODO: How to reset Date/Time + Time Picker
                //TODO: Initialize with set values from view
                DatePickerDialog dialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mStartDateTimeButton.setText(dayOfMonth + "." + monthOfYear + "." + year);
                            }
                        }, mYear, mMonth, mDay);
                dialog.show();
            }
        });

        mEndDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                System.out.println("the selected " + mDay);
                //TODO: MAYBE IMPLEMENT THIS https://github.com/flavienlaurent/datetimepicker
                //TODO: How to reset Date/Time + Time Picker
                //TODO: Initialize with set values from view
                DatePickerDialog dialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mEndDateTimeButton.setText(dayOfMonth + "." + monthOfYear + "." + year);
                            }
                        }, mYear, mMonth, mDay);
                dialog.show();
            }
        });

        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activity.getTempPost().setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activity.getTempPost().setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activity.getTempPost().setTags(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                PostType newType = PostType.values()[position];
                activity.getTempPost().setType(newType);

                setPostTypeHints(newType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d(LOG_TAG,"nothing selected");
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        styleActionBar();

        mTypeSpinnerAdapter = new TypeSpinnerAdapter(activity);

        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_want, R.drawable.want, PostType.WANT));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_offer, R.drawable.offer, PostType.OFFER));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_activity, R.drawable.activity, PostType.ACTIVITY));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_change, R.drawable.change, PostType.CHANGE));

        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);

        //Initialize ImagePager
        mImagePagerAdapter = new ImagePagerAdapter(activity);

        mImagePager.setSaveFromParentEnabled(false); //This is necessary because it prevents the ViewPager from being messed up on pagechanges and popbackstack's
        mImagePager.setAdapter(mImagePagerAdapter);


        mIconPageIndicator.setViewPager(mImagePager);

        MapsInitializer.initialize(activity);
        mGeocoder = new Geocoder(activity, Locale.getDefault());

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
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setMyLocationEnabled(true);
        }

        mLocationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    //TODO: MAKE ASYNCTASK like here http://developer.android.com/training/location/display-address.html
                    try {
                        map.clear();
                        Log.d(LOG_TAG,"ENTERED TEXT:"+ v.getText());

                        List<Address> addressList = mGeocoder.getFromLocationName(v.getText().toString(), 1);

                        for(Address a : addressList){
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(new LatLng(a.getLatitude(), a.getLongitude()))
                                    .title(getString(R.string.create_location))
                                    .snippet(StringUtils.getFormattedAddress(a)) //TODO: MultiLine Snippet see --> http://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet
                                    .draggable(false)); //TODO: DRAG MARKER IMPLEMENTATION
                            Log.d(LOG_TAG,a.toString());
                            activity.getTempPost().setLocation(new LatLng(a.getLatitude(), a.getLongitude())); //SET LOCATION OF TEMPPOST
                        }

                        if(addressList.size() > 0) {
                            Address usedAddress = addressList.get(0);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(usedAddress.getLatitude(), usedAddress.getLongitude()), 10);
                            map.animateCamera(cameraUpdate);
                        }
                        //Hides the keyboard after search
                        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                        handled = true;
                    }catch (IOException ioe){
                        //TODO ERROR TOAST
                        Log.e(LOG_TAG,ioe.getMessage());
                    }

                }
                return handled;
            }
        });

        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //TODO: SET MARKER OR PRESENT TOAST ON CLICK IF LOCATIONSERVICE IS SET, if false then show toast

                return false;
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
    //*********************************************************************************************
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(activity.isDrawerOpen()){
            super.onCreateOptionsMenu(menu, inflater);
        }else {
            menu.clear(); //THIS IS ALL A LITTLE WEIRD STILL NOT SURE IF THIS IS AT ALL BEST PRACTICE
            inflater.inflate(R.menu.create, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case (R.id.action_save):
                displaySaveDialog();
                return true;
            case (R.id.action_dismiss):
                displayDismissDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLowMemory() {
        Log.d(LOG_TAG,"ON LOW MEMORY");
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void styleActionBar() {
        activity.setDrawerToggle(true);
        ActionBar ab = activity.getActionBar();

        ab.setTitle(getString(R.string.mi_createpost));
        ab.setSubtitle(null);
        ab.setIcon(R.drawable.ic_launcher);
    }

    private void displaySaveDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_create_save));
        builder.setTitle(getString(R.string.dialog_create_save_title));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: IMPLEMENT SAVE STUFF
                Toast.makeText(activity, getString(R.string.toast_create_saved), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: IMPLEMENT STUFF TO DO HERE
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayDismissDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_create_dismiss));
        builder.setTitle(getString(R.string.dialog_create_dismiss_title));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.setTempPost(new Post());
                createTask = new CreateTask();
                createTask.execute();
                Toast.makeText(activity, getString(R.string.toast_create_dismiss), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: IMPLEMENT STUFF TO DO HERE
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setPostTypeHints(PostType newType){
        switch(newType){
            case ACTIVITY:
                mTags.setHint(R.string.create_tags_together_hint);
                mTitle.setHint(R.string.create_title_together_hint);
                mDescription.setHint(R.string.create_description_together_hint);
                mLocationText.setHint(R.string.create_location_together_hint);
                break;
            case OFFER:
                mTags.setHint(R.string.create_tags_supply_hint);
                mTitle.setHint(R.string.create_title_supply_hint);
                mDescription.setHint(R.string.create_description_supply_hint);
                mLocationText.setHint(R.string.create_location_supply_hint);
                break;
            case WANT:
                mTags.setHint(R.string.create_tags_demand_hint);
                mTitle.setHint(R.string.create_title_demand_hint);
                mDescription.setHint(R.string.create_description_demand_hint);
                mLocationText.setHint(R.string.create_location_demand_hint);
                break;
            case CHANGE:
                mTags.setHint(R.string.create_tags_change_hint);
                mTitle.setHint(R.string.create_title_change_hint);
                mDescription.setHint(R.string.create_description_change_hint);
                mLocationText.setHint(R.string.create_location_change_hint);
                break;
        }
    }

    private class CreateTask extends AsyncTask<String, Integer, Post> {
        @Override
        protected Post doInBackground(String... params) {
            return activity.getTempPost();
        }

        @Override
        protected void onCancelled(Post tempPost) {
            putPostInView(tempPost);
            //TODO: SHOW PROCESS WAS CANCELLED
        }

        @Override
        protected void onPostExecute(Post tempPost) {
            putPostInView(tempPost);
        }

        private void putPostInView(Post tempPost){
            mTitle.setText(tempPost.getTitle());
            mDescription.setText(tempPost.getDescription());
            mTags.setText(tempPost.getTagsAsString());
            mTypeSpinner.setSelection(tempPost.getType().ordinal());

            if(tempPost.getLocation().latitude != 0.0 && tempPost.getLocation().longitude != 0.0) {
                try {
                    List<Address> adresses = mGeocoder.getFromLocation(tempPost.getLocation().latitude, tempPost.getLocation().longitude, 1);

                    String address;

                    if (adresses != null && adresses.size() > 0) {
                        address = StringUtils.getFormattedAddress(adresses.get(0));
                        mLocationText.setText(address);
                    } else {
                        Log.d(LOG_TAG, "No Address found");
                        address = tempPost.getTitle();
                    }

                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(tempPost.getLocation())
                            .title(tempPost.getTitle())
                            .snippet(address)); //TODO: MultiLine Snippet see --> http://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(tempPost.getLocation(), 10);
                    map.animateCamera(cameraUpdate);
                } catch (IOException ioe) {
                    //TODO: ERROR TOAST
                    Log.e(LOG_TAG, ioe.getMessage());
                }
            }

            if(tempPost.getTitleImageUrl()!=null && tempPost.getTitleImageUrl().trim().length()>0) {
                mImagePagerAdapter.addItem(tempPost.getTitleImageUrl());
            }

            if(tempPost.getImageUrls() != null) {
                for (String imgUrl : tempPost.getImageUrls()) {
                    imgUrl = imgUrl.trim();
                    if (imgUrl.length() > 0) {
                        mImagePagerAdapter.addItem(imgUrl);
                    }
                }
            }

            mImagePagerAdapter.notifyDataSetChanged();
            mIconPageIndicator.notifyDataSetChanged();
        }
    }
}
