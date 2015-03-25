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

package at.researchstudio.sat.won.android.won_android_app.app.webservice.impl;

import android.content.Context;
import android.util.Log;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.components.WonClientHttpRequestFactory;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.constants.ResponseCode;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class AuthenticationService{
    private static final String LOG_TAG = AuthenticationService.class.getSimpleName();

    private WonClientHttpRequestFactory requestFactory;
    private RestTemplate restTemplate;
    private Context context; //used for string resources

    public AuthenticationService(Context context){
        this.context = context; //used for stringresource retrieval
        requestFactory = new WonClientHttpRequestFactory(); //used for cookie handling within connections
        restTemplate = new RestTemplate(true, requestFactory);

        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public int login(User user){
        final String url = context.getString(R.string.base_uri) + context.getString(R.string.login_path);

        try{
            Log.d(LOG_TAG, url);

            HttpEntity<User> request = new HttpEntity<User>(user);
            HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            verboseLogOutput(response);

            requestFactory.setCookieValue(response.getHeaders().get("Set-Cookie").get(0)); //NOT SURE IF GET 0 is VALID AS THE COOKIE apparently cookie value seems to be set already

            return ResponseCode.LOGIN_SUCCESS;
        }catch (HttpClientErrorException e) {
            verboseLogClientError(e);

            if(e.getStatusCode() == HttpStatus.FORBIDDEN){
                return ResponseCode.LOGIN_NOUSER;
            }else{
                return ResponseCode.CONNECTION_ERR;
            }
        } catch (ResourceAccessException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
            return ResponseCode.CONNECTION_ERR;
        }
    }

    public int register(User user){
        final String url = context.getString(R.string.base_uri) + context.getString(R.string.register_path);

        try{
            Log.d(LOG_TAG, url);
            user.setPasswordAgain(user.getPassword());

            HttpEntity<User> request = new HttpEntity<User>(user);
            HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            verboseLogOutput(response);

            return this.login(user);
        }catch (HttpClientErrorException e) {
            verboseLogClientError(e);

            if(e.getStatusCode() == HttpStatus.FORBIDDEN){
                return ResponseCode.LOGIN_NOUSER;
            }else if(e.getStatusCode() == HttpStatus.CONFLICT){
                return ResponseCode.REGISTER_USEREXISTS;
            }else{
                return ResponseCode.CONNECTION_ERR;
            }
        } catch (ResourceAccessException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
            return ResponseCode.CONNECTION_ERR;
        }
    }

    public int login(String username, String password) {
        return login(new User(username, password));
    }

    public int logout() {
        final String url = context.getString(R.string.base_uri) + context.getString(R.string.logout_path);

        try{
            Log.d(LOG_TAG, url);

            HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class); //TODO: IDK if "null" is a valid postparameter

            verboseLogOutput(response);

            requestFactory.setCookieValue(response.getHeaders().get("Set-Cookie").get(0)); //NOT SURE IF GET 0 is VALID AS THE COOKIE apparently cookie value seems to be set already

            return ResponseCode.LOGOUT_SUCCESS;
        }catch (HttpClientErrorException e) {
            verboseLogClientError(e);

            if(e.getStatusCode() == HttpStatus.FORBIDDEN){
                return ResponseCode.LOGIN_NOUSER;
            }else{
                return ResponseCode.CONNECTION_ERR;
            }
        } catch (ResourceAccessException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage());
            return ResponseCode.CONNECTION_ERR;
        }
    }

    private void verboseLogOutput(HttpEntity<String> response){
        for(Map.Entry<String, List<String>> es : response.getHeaders().entrySet()){
            if(es.getValue()==null){
                Log.d(LOG_TAG,"Key: "+ es.getKey()+ " EMPTY");
            }else {
                for (String value : es.getValue()){
                    Log.d(LOG_TAG,"Key: "+ es.getKey()+ " Value: "+value);
                }
            }
        }
    }

    private void verboseLogClientError(HttpClientErrorException e){
        Log.e(LOG_TAG, "StatusCode: "+e.getStatusCode());
        Log.e(LOG_TAG, "StatusText: "+e.getStatusText());
        Log.e(LOG_TAG, "LocMessage: "+e.getLocalizedMessage());
        Log.e(LOG_TAG, "Resp.BodySt "+e.getResponseBodyAsString());
    }

    public WonClientHttpRequestFactory getRequestFactory() {
        return requestFactory;
    }

    public void setRequestFactory(WonClientHttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
