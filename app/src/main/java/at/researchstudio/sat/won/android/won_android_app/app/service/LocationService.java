package at.researchstudio.sat.won.android.won_android_app.app.service;

import android.location.Location;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by fsuda on 01.09.2014.
 */
public class LocationService {
    private static GoogleApiClient mLocationClient;
    private static LocationRequest mLocationRequest;

    public static void init(GoogleApiClient locationClient) {
        mLocationClient = locationClient;
        mLocationRequest = LocationRequest.create();
    }

    public static Location getCurrentLocation(){
        return LocationServices.FusedLocationApi.getLastLocation(mLocationClient);
    }

    public static void disconnect(){
        mLocationClient.disconnect();
    }

    public static void connect(){
        mLocationClient.connect();
    }
}
