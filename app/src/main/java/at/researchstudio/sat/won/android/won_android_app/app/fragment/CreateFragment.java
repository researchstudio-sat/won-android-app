package at.researchstudio.sat.won.android.won_android_app.app.fragment;

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
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.TypeSpinnerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostTypeSpinnerModel;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"ON CREATE VIEW");
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);

        getActivity().setTitle(R.string.mi_createpost);

        //Initialize TypeSpinner
        mTypeSpinner = (Spinner)rootView.findViewById(R.id.typespinner);
        mTypeSpinnerAdapter = new TypeSpinnerAdapter(getActivity());

        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_want, R.drawable.want, PostType.WANT));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_offer, R.drawable.offer, PostType.OFFER));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_activity, R.drawable.activity, PostType.ACTIVITY));
        mTypeSpinnerAdapter.addItem(new PostTypeSpinnerModel(R.string.create_type_spinner_change, R.drawable.change, PostType.CHANGE));

        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);

        //TODO: SPINNER HANDLING  --> EDIT HELPTEXTS

        //Initialize ImagePager
        mImagePagerAdapter = new ImagePagerAdapter(getActivity().getFragmentManager());

        mImagePager = (ViewPager) rootView.findViewById(R.id.create_image_pager);
        mImagePager.setAdapter(mImagePagerAdapter);

        mIconPageIndicator = (IconPageIndicator) rootView.findViewById(R.id.create_image_pager_indicator);

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
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
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
                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
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
        MapsInitializer.initialize(getActivity());

        mLocationText = (EditText) rootView.findViewById(R.id.create_location);



        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        if(mMapView!=null)
        {
            map = mMapView.getMap();
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setMyLocationEnabled(true);


        }
        //PART CAN BE REMOVED
        if(LocationService.getCurrentLocation() != null)
        {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(LocationService.getCurrentLocation().getLatitude(), LocationService.getCurrentLocation().getLongitude()), 10);
            map.animateCamera(cameraUpdate);
        }
        //*******************

        mLocationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    //TODO: MAKE ASYNCTASK like here http://developer.android.com/training/location/display-address.html
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        map.clear();
                        Log.d(LOG_TAG,"ENTERED TEXT:"+ v.getText());

                        List<Address> addressList = geocoder.getFromLocationName(v.getText().toString(), 100);

                        for(Address a : addressList){
                            Marker marker = map.addMarker(new MarkerOptions()
                                    .position(new LatLng(a.getLatitude(), a.getLongitude()))
                                    .title(a.getAddressLine(0)) //TODO: PARSE CORRECT ADRESS LINES
                                    .snippet("WTF IS A SNIPPET"));
                            Log.d(LOG_TAG,a.toString());
                        }



                        if(addressList.size() > 0) {
                            Address usedAddress = addressList.get(0);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(usedAddress.getLatitude(), usedAddress.getLongitude()), 10);
                            map.animateCamera(cameraUpdate);
                        }
                        //Hides the keyboard after search
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                        handled = true;
                    }catch (IOException ioe){
                        Log.e("geocoder",ioe.getMessage());
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
}
