package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.TypeSpinnerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.WelcomeScreenPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.model.TypeSpinnerModel;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fsuda on 21.08.2014.
 */
public class CreateFragment extends Fragment {
    private MapView mMapView;
    private Spinner mTypeSpinner;
    private TypeSpinnerAdapter mTypeSpinnerAdapter;

    private ImagePagerAdapter mImagePagerAdapter; //TODO: CHANGE THIS TO IMAGE PAGER
    private ViewPager mImagePager; //TODO: CHANGE THIS TO IMAGE PAGER

    private GoogleMap map;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);

        getActivity().setTitle(R.string.mi_createpost);

        //Initialize TypeSpinner
        mTypeSpinner = (Spinner)rootView.findViewById(R.id.typespinner);
        mTypeSpinnerAdapter = new TypeSpinnerAdapter(getActivity());

        mTypeSpinnerAdapter.addItem(new TypeSpinnerModel(R.string.create_type_spinner_want, R.drawable.want));
        mTypeSpinnerAdapter.addItem(new TypeSpinnerModel(R.string.create_type_spinner_offer, R.drawable.offer));
        mTypeSpinnerAdapter.addItem(new TypeSpinnerModel(R.string.create_type_spinner_activity, R.drawable.activity));
        mTypeSpinnerAdapter.addItem(new TypeSpinnerModel(R.string.create_type_spinner_change, R.drawable.change));

        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);

        //TODO: SPINNER HANDLING  --> EDIT HELPTEXTS

        //Initialize ImagePager
        mImagePagerAdapter = new ImagePagerAdapter(getActivity().getFragmentManager());

        mImagePager = (ViewPager) rootView.findViewById(R.id.create_image_pager);
        mImagePager.setAdapter(mImagePagerAdapter);
        mImagePager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mImagePager", "clicked");
            }
        });

        //Initialize GMaps
        MapsInitializer.initialize(getActivity());

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        if(mMapView!=null && LocationService.getCurrentLocation() != null)
        {
            map = mMapView.getMap();
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.setMyLocationEnabled(true);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(LocationService.getCurrentLocation().getLatitude(), LocationService.getCurrentLocation().getLongitude()), 10);
            map.animateCamera(cameraUpdate);

        }

        return rootView;
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
