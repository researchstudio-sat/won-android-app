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
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.TypeSpinnerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostTypeSpinnerModel;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;
import at.researchstudio.sat.won.android.won_android_app.app.util.StringUtils;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.IconPageIndicator;

import java.io.IOException;
import java.util.ArrayList;
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

    private GoogleMap map;
    private Geocoder mGeocoder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"ON CREATE VIEW");
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);

        activity = (MainActivity) getActivity();

        styleActionBar();

        //Initialize TypeSpinner
        mTypeSpinner = (Spinner)rootView.findViewById(R.id.typespinner);
        mTypeSpinnerAdapter = new TypeSpinnerAdapter(activity);

        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_want, R.drawable.want, PostType.WANT));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_offer, R.drawable.offer, PostType.OFFER));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_activity, R.drawable.activity, PostType.ACTIVITY));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_change, R.drawable.change, PostType.CHANGE));

        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);

        //TODO: SPINNER HANDLING  --> EDIT HELPTEXTS

        //Initialize ImagePager
        mImagePagerAdapter = new ImagePagerAdapter(activity.getFragmentManager());

        mImagePager = (ViewPager) rootView.findViewById(R.id.image_pager);
        mImagePager.setAdapter(mImagePagerAdapter);

        mIconPageIndicator = (IconPageIndicator) rootView.findViewById(R.id.image_pager_indicator);

        mIconPageIndicator.setViewPager(mImagePager);

        //Initialize DateTime Picker
        mStartDateTimeButton = (Button) rootView.findViewById(R.id.create_startdatetime_button);
        mEndDateTimeButton = (Button) rootView.findViewById(R.id.create_enddatetime_button);

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
                                mEndDateTimeButton.setText(dayOfMonth+"."+monthOfYear+"."+year);
                            }
                        }, mYear, mMonth, mDay);
                dialog.show();
            }
        });

        //Initialize GMaps
        MapsInitializer.initialize(activity);
        mGeocoder = new Geocoder(activity, Locale.getDefault());

        mLocationText = (EditText) rootView.findViewById(R.id.create_location);



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

    private void styleActionBar() {
        activity.setDrawerToggle(true);
        ActionBar ab = activity.getActionBar();

        ab.setTitle(getString(R.string.mi_createpost));
        ab.setSubtitle(null);
        ab.setIcon(R.drawable.ic_launcher);
    }
}
