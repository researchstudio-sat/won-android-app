package at.researchstudio.sat.won.android.won_android_app.app.service;

import android.location.Location;
import com.google.android.gms.location.LocationClient;

/**
 * Created by fsuda on 01.09.2014.
 */
public class LocationService {
    private static LocationClient mLocationClient;

    public static void init(LocationClient locationClient) {
        mLocationClient = locationClient;
    }

    public static Location getCurrentLocation(){
        return mLocationClient.getLastLocation();
    }

    public static void disconnect(){
        mLocationClient.disconnect();
    }

    public static void connect(){
        mLocationClient.connect();
    }
}
