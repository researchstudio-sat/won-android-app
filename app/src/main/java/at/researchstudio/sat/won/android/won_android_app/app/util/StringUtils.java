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

package at.researchstudio.sat.won.android.won_android_app.app.util;

import android.location.Address;

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

    public static boolean isEmpty(String string){
        return string==null || string.length()==0;
    }
}
