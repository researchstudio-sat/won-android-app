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

package at.researchstudio.sat.won.android.won_android_app.app.webservice.components;

import android.util.Log;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class WonClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
    private static final String LOG_TAG = WonClientHttpRequestFactory.class.getSimpleName();

    private String cookieValue; //TODO: RETRIEVE THIS FROM SHAREDPREFERENCES OR SOMETHING

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        Log.d(LOG_TAG,"PREPARE CONNECTION");
        Log.d(LOG_TAG, "Connection: " + connection.toString() + " Method: "+httpMethod);

        connection.setRequestProperty("accept-charset", "UTF-8");
        
        if(cookieValue!=null) {
            connection.setRequestProperty("Cookie", cookieValue);
        }else{
            Log.d(LOG_TAG, "COOKIE IS STILL NULL");
        }
        for(Map.Entry<String, List<String>> es : connection.getRequestProperties().entrySet()){
            if(es.getValue()==null){
                Log.d(LOG_TAG,"Key: "+ es.getKey()+ " EMPTY");
            }else {
                for (String value : es.getValue()){
                    Log.d(LOG_TAG,"Key: "+ es.getKey()+ " Value: "+value);
                }
            }
        }
        Log.d(LOG_TAG,"");
        super.prepareConnection(connection, httpMethod);
    }

    public String getCookieValue() {
        return cookieValue;
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }
}
