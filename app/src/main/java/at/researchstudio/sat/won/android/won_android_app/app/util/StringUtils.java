package at.researchstudio.sat.won.android.won_android_app.app.util;

import android.location.Address;
import android.util.Log;

/**
 * Created by fsuda on 15.10.2014.
 */
public class StringUtils {
    private static final String LOG_TAG = StringUtils.class.getSimpleName();

    /**
     * Processes All Addresslines into one string, every line is separated by a new line
     * @param address
     * @return one adress string
     */
    public static String getFormattedAddress(Address address){
        StringBuilder formattedAddress = new StringBuilder();

        for(int i = 0; i<=address.getMaxAddressLineIndex();i++) {
            formattedAddress.append(address.getAddressLine(i)).append("\n");
        }
        return formattedAddress.toString().trim();
    }
}
