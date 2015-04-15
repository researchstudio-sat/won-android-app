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

import android.app.*;
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
import android.text.format.Time;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.TypeSpinnerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Constants;
import at.researchstudio.sat.won.android.won_android_app.app.enums.RepeatType;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostTypeSpinnerModel;
import at.researchstudio.sat.won.android.won_android_app.app.util.StringUtils;
import be.billington.calendar.recurrencepicker.EventRecurrence;
import be.billington.calendar.recurrencepicker.EventRecurrenceFormatter;
import be.billington.calendar.recurrencepicker.RecurrencePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.viewpagerindicator.IconPageIndicator;
import won.protocol.model.BasicNeedType;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by fsuda on 21.08.2014.
 */
public class CreateFragment extends Fragment implements OnDateSetListener, TimePickerDialog.OnTimeSetListener {
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

    private CheckBox mRecurrenceSwitch;
    private Switch mLocationSwitch;
    private Switch mImagesSwitch;
    private Switch mDateTimeSwitch;
    private Button mRecurrenceDateButton;
    private Button mStartDateTimeButton;
    private Button mEndDateTimeButton;

    private RelativeLayout mLocationContainer;
    private RelativeLayout mImageContainer;
    private LinearLayout mDateTimeContainer;

    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;

    private EditText mLocationText;
    private EditText mTitle;
    private EditText mDescription;
    private EditText mTags;

    private GoogleMap map;
    private Geocoder mGeocoder;

    private ScrollView mScrollView;
    private ImageView transparentImageView;

    //TODO: IDK WHAT THESE IS DOES
    private String recurrenceRule;
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    //***********FRAGMENT LIFECYCLE****************************************************************
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);

        mTypeSpinner = (Spinner)rootView.findViewById(R.id.typespinner);
        mImagePager = (ViewPager) rootView.findViewById(R.id.image_pager);
        mIconPageIndicator = (IconPageIndicator) rootView.findViewById(R.id.image_pager_indicator);

        mMapView = (MapView) rootView.findViewById(R.id.post_map);
        mRecurrenceSwitch = (CheckBox) rootView.findViewById(R.id.create_recurrence_switch);
        mLocationSwitch = (Switch) rootView.findViewById(R.id.create_location_switch);
        mImagesSwitch = (Switch) rootView.findViewById(R.id.create_images_switch);
        mDateTimeSwitch = (Switch) rootView.findViewById(R.id.create_datetime_switch);
        mLocationText = (EditText) rootView.findViewById(R.id.create_location);
        mLocationContainer = (RelativeLayout) rootView.findViewById(R.id.create_location_container);
        mImageContainer = (RelativeLayout) rootView.findViewById(R.id.create_image_container);
        mDateTimeContainer = (LinearLayout) rootView.findViewById(R.id.create_datetime_container);

        mRecurrenceDateButton = (Button) rootView.findViewById(R.id.create_recurrencedate_button);
        mStartDateTimeButton = (Button) rootView.findViewById(R.id.create_startdatetime_button);
        mEndDateTimeButton = (Button) rootView.findViewById(R.id.create_enddatetime_button);

        mTitle = (EditText) rootView.findViewById(R.id.create_title);
        mDescription = (EditText) rootView.findViewById(R.id.create_description);
        mTags = (EditText) rootView.findViewById(R.id.create_tags);

        transparentImageView = (ImageView) rootView.findViewById(R.id.transparent_image);
        mScrollView = (ScrollView) rootView.findViewById(R.id.create_scrollview);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(LOG_TAG,"onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        activity.showLoading();
        styleActionBar();

        mTypeSpinnerAdapter = new TypeSpinnerAdapter(activity);

        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.type_want, R.drawable.want, BasicNeedType.DEMAND));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.type_offer, R.drawable.offer, BasicNeedType.SUPPLY));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.type_activity, R.drawable.activity, BasicNeedType.DO_TOGETHER));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.type_change, R.drawable.change, BasicNeedType.CRITIQUE));

        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);

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

        final Calendar calendar = Calendar.getInstance();

        mDatePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        mTimePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

        // Gets to GoogleMap from the MapView and does initialization stuff
        if(mMapView!=null)
        {
            map = mMapView.getMap();
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG,"onStart");
        super.onStart();
        createTask = new CreateTask();
        createTask.execute();
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG,"onResume");
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG,"onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG,"onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG,"onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG,"onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
        if(createTask != null && createTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d(LOG_TAG,"cancelAsyncTask");
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
        builder.setMessage(getString(R.string.dialog_create_save))
                .setTitle(getString(R.string.dialog_create_save_title))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(LOG_TAG, "Saving the post");
                        SaveTask saveTask = new SaveTask();
                        saveTask.execute();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //DO NOTHING
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void displayDismissDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.dialog_create_dismiss))
                .setTitle(getString(R.string.dialog_create_dismiss_title))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            activity.setTempPost(new Post());
                            createTask = new CreateTask();
                            createTask.execute();
                            Toast.makeText(activity, getString(R.string.toast_create_dismiss), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //DO NOTHING
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setPostTypeHints(BasicNeedType newType){
        switch(newType){
            case DO_TOGETHER:
                mTags.setHint(R.string.create_tags_together_hint);
                mTitle.setHint(R.string.create_title_together_hint);
                mDescription.setHint(R.string.create_description_together_hint);
                mLocationText.setHint(R.string.create_location_together_hint);
                break;
            case SUPPLY:
                mTags.setHint(R.string.create_tags_supply_hint);
                mTitle.setHint(R.string.create_title_supply_hint);
                mDescription.setHint(R.string.create_description_supply_hint);
                mLocationText.setHint(R.string.create_location_supply_hint);
                break;
            case DEMAND:
                mTags.setHint(R.string.create_tags_demand_hint);
                mTitle.setHint(R.string.create_title_demand_hint);
                mDescription.setHint(R.string.create_description_demand_hint);
                mLocationText.setHint(R.string.create_location_demand_hint);
                break;
            case CRITIQUE:
                mTags.setHint(R.string.create_tags_change_hint);
                mTitle.setHint(R.string.create_title_change_hint);
                mDescription.setHint(R.string.create_description_change_hint);
                mLocationText.setHint(R.string.create_location_change_hint);
                break;
        }
    }

    private class SaveTask extends AsyncTask<String, Integer, Post> {
        @Override
        protected Post doInBackground(String... params) {
            Log.d(LOG_TAG, "Trying to save Post started AsyncTask");
            if(!mDateTimeSwitch.isChecked()){
                activity.getTempPost().setStartTime(0);
                activity.getTempPost().setStopTime(0);
                activity.getTempPost().setRepeat(RepeatType.NONE);
            }else {
                //TODO: SET RECURRENCE THING
            }

            if(!mImagesSwitch.isChecked()) {
                activity.getTempPost().setImageUrls(null);
            }

            if(!mLocationSwitch.isChecked()){
                activity.getTempPost().setLocation(null);
            }

            return activity.getPostService().savePost(activity.getTempPost());
        }

        @Override
        protected void onPostExecute(Post post) {
            Log.d(LOG_TAG, "Post Saved");
            Toast.makeText(activity, getString(R.string.toast_create_saved), Toast.LENGTH_SHORT).show();
            activity.setTempPost(new Post());

            Fragment fragment;

            Bundle args = new Bundle();

            fragment = new MyPostFragment();

            args.putString(Post.ID_REF, post.getURIString());

            fragment.setArguments(args);
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private class CreateTask extends AsyncTask<String, Integer, Post> {

        @Override
        protected Post doInBackground(String... params) {
            Log.d(LOG_TAG,"createTask -> doInBackground");
            return activity.getTempPost();
        }

        @Override
        protected void onCancelled(Post tempPost) {
            Log.d(LOG_TAG, "createTask -> onCancelled");
        }

        @Override
        protected void onPostExecute(Post tempPost) {
            Log.d(LOG_TAG,"createTask -> onPostExecute");
            addListeners();
            putPostInView(tempPost);
        }

        private void addListeners(){
            Log.d(LOG_TAG,"createTask -> addListeners");
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

            mDateTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        showDateViews();
                    }else{
                        hideDateViews();
                    }
                }
            });


            mRecurrenceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        showRecurrenceView();
                    }else{
                        hideRecurrenceView();
                    }
                }
            });

            mLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        showLocationViews();
                    }else{
                        hideLocationViews();
                    }
                }
            });

            mImagesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        showImageViews();
                    }else{
                        hideImageViews();
                    }
                }
            });

            mStartDateTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: How to reset Date/Time + Time Picker
                    //TODO: Initialize with set values from view

                    mDatePickerDialog.setVibrate(false);
                    mDatePickerDialog.setYearRange(1985, 2028);
                    mDatePickerDialog.setCloseOnSingleTapDay(false);
                    mDatePickerDialog.show(activity.getSupportFragmentManager(), DATEPICKER_TAG);
                    //TODO: FIGURE OUT HOW TO CONNECT DATE AND TIME UI
                }
            });

            mEndDateTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: How to reset Date/Time + Time Picker
                    //TODO: Initialize with set values from view

                    mDatePickerDialog.setVibrate(false);
                    mDatePickerDialog.setYearRange(1985, 2028);
                    mDatePickerDialog.setCloseOnSingleTapDay(false);
                    mDatePickerDialog.show(activity.getSupportFragmentManager(), DATEPICKER_TAG);
                    //TODO: FIGURE OUT HOW TO CONNECT DATE AND TIME UI
                    //INSERT A SECOND INSTANCE OF DATEPICKER TO SET END DATES
                }
            });

            mRecurrenceDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecurrencePickerDialog recurrencePickerDialog = new RecurrencePickerDialog();

                    if (recurrenceRule != null && recurrenceRule.length() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString(RecurrencePickerDialog.BUNDLE_RRULE, recurrenceRule);
                        recurrencePickerDialog.setArguments(bundle);
                    }

                    recurrencePickerDialog.setOnRecurrenceSetListener(new RecurrencePickerDialog.OnRecurrenceSetListener() {
                        @Override
                        public void onRecurrenceSet(String rrule) {
                            recurrenceRule = rrule;

                            if (recurrenceRule != null && recurrenceRule.length() > 0) {
                                EventRecurrence recurrenceEvent = new EventRecurrence();
                                recurrenceEvent.setStartDate(new Time("" + new Date().getTime()));
                                recurrenceEvent.parse(rrule);
                                String srt = EventRecurrenceFormatter.getRepeatString(activity, getResources(), recurrenceEvent, true);
                                mRecurrenceDateButton.setText(srt);
                                //TODO: SET RECURRENCE IN TEMP POST
                            } else {
                                mRecurrenceDateButton.setText("No recurrence"); //TODO: IMPL THIS
                            }
                        }
                    });
                    recurrencePickerDialog.show(activity.getSupportFragmentManager(), "recurrencePicker");
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

                    BasicNeedType newType = BasicNeedType.values()[position];
                    activity.getTempPost().setType(newType);

                    setPostTypeHints(newType);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    Log.d(LOG_TAG,"nothing selected");
                }
            });

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
            Log.d(LOG_TAG,"createTask -> addListeners DONE");
        }

        private void putPostInView(Post tempPost){
            Log.d(LOG_TAG,"CreateTask -> putPostInView");
            mTitle.setText(tempPost.getTitle());
            mDescription.setText(tempPost.getDescription());
            mTags.setText(tempPost.getTagsAsString());
            mTypeSpinner.setSelection(tempPost.getType().ordinal());

            if(tempPost.getLocation().latitude != 0.0 && tempPost.getLocation().longitude != 0.0) {
                mLocationSwitch.setChecked(true);
                showLocationViews();
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
                            .snippet(address)
                            .draggable(true)); //TODO: MultiLine Snippet see --> http://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(tempPost.getLocation(), 10);
                    map.animateCamera(cameraUpdate);
                } catch (IOException ioe) {
                    Log.e(LOG_TAG, ioe.getMessage());
                }
            }else{
                mLocationSwitch.setChecked(false);
                hideLocationViews();
                mLocationText.setText("");
                map.clear();
            }

            //Initialize ImagePager
            mImagePagerAdapter = new ImagePagerAdapter(activity);
            mImagePager.setSaveFromParentEnabled(false); //This is necessary because it prevents the ViewPager from being messed up on pagechanges and popbackstack's

            if(tempPost.getImageUrls()!= null && tempPost.getImageUrls().size()>0){
                mImagesSwitch.setChecked(true);
                showImageViews();

                if(tempPost.getTitleImageUrl()!=null && tempPost.getTitleImageUrl().trim().length()>0) {
                    Log.d(LOG_TAG,"Adding Image To Create Post: "+tempPost.getTitleImageUrl());
                    mImagePagerAdapter.addItem(tempPost.getTitleImageUrl(), true);
                }


                for (String imgUrl : tempPost.getOtherImageUrls()) {
                    imgUrl = imgUrl.trim();
                    if (imgUrl.length() > 0) {
                        Log.d(LOG_TAG,"Adding Image To Create Post: "+imgUrl);
                        mImagePagerAdapter.addItem(imgUrl);
                    }
                }
            }else{
                mImagesSwitch.setChecked(false);
                hideImageViews();
            }

            mImagePager.setAdapter(mImagePagerAdapter);
            mIconPageIndicator.setViewPager(mImagePager);
            mIconPageIndicator.notifyDataSetChanged();

            if(tempPost.getImageUrls()==null || tempPost.getImageUrls().size()==0){
                mIconPageIndicator.setVisibility(View.GONE);
            }else{
                mIconPageIndicator.setVisibility(View.VISIBLE);
            }

            //TODO: SET DATE ACCORDING TO THE GIVEN POST

            activity.hideLoading();
            Log.d(LOG_TAG,"createTask -> putPostInView DONE");
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Toast.makeText(activity, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Toast.makeText(activity, "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
    }

    //TODO: Implement hide/show methods for groups that can be disabled
    private void showRecurrenceView(){
        //mRecurrenceSwitch.setTextColor(getResources().getColor(R.color.create_text_enabled));
        mRecurrenceDateButton.setVisibility(View.VISIBLE);
        mDateTimeContainer.setVisibility(View.GONE);
    }

    private void hideRecurrenceView(){
        //mRecurrenceSwitch.setTextColor(getResources().getColor(R.color.create_text_disabled));
        mRecurrenceDateButton.setVisibility(View.GONE);
        mDateTimeContainer.setVisibility(View.VISIBLE);
    }

    private void hideLocationViews(){
        mLocationSwitch.setTextColor(getResources().getColor(R.color.create_text_disabled));
        mLocationContainer.setVisibility(View.GONE);
        mLocationText.setVisibility(View.GONE);
    }

    private void showLocationViews(){
        mLocationSwitch.setTextColor(getResources().getColor(R.color.create_text_enabled));
        mLocationContainer.setVisibility(View.VISIBLE);
        mLocationText.setVisibility(View.VISIBLE);
    }

    private void hideImageViews(){
        mImagesSwitch.setTextColor(getResources().getColor(R.color.create_text_disabled));
        mImageContainer.setVisibility(View.GONE);
    }

    private void showImageViews(){
        mImagesSwitch.setTextColor(getResources().getColor(R.color.create_text_enabled));
        mImageContainer.setVisibility(View.VISIBLE);
    }

    private void hideDateViews(){
        mDateTimeSwitch.setTextColor(getResources().getColor(R.color.create_text_disabled));
        mRecurrenceSwitch.setVisibility(View.GONE);
        mRecurrenceDateButton.setVisibility(View.GONE);
        mDateTimeContainer.setVisibility(View.GONE);
    }

    private void showDateViews(){
        mDateTimeSwitch.setTextColor(getResources().getColor(R.color.create_text_enabled));
        mRecurrenceSwitch.setVisibility(View.VISIBLE);

        if(mRecurrenceSwitch.isChecked()){
            showRecurrenceView();
        }else{
            hideRecurrenceView();
        }
    }
}
