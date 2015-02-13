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

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by fsuda on 25.08.2014.
 */
public class LoginFragment extends Fragment {
    private static final String LOG_TAG = LoginFragment.class.getSimpleName();

    private TextView mErrorText;
    private EditText mUsername;
    private EditText mPassword;
    private Button mLoginButton;

    private MainActivity activity;

    //*******FRAGMENT LIFECYCLE************************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginButton = (Button) rootView.findViewById(R.id.login_login);
        mUsername = (EditText) rootView.findViewById(R.id.login_username);
        mPassword = (EditText) rootView.findViewById(R.id.login_password);

        mErrorText = (TextView) rootView.findViewById(R.id.login_error);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    //*************************************************************************************************

    private void login(){
        //******************************************************************************
        new LoginTask().execute();
        //******************************************************************************
        /*if(!StringUtils.isEmpty(userName.toString()) && !StringUtils.isEmpty(pass.toString())) {//TODO: INVOKE REAL AUTHENTICATION in form of a AsyncTask
            activity.showMainMenu();

        }else{
            mErrorText.setText(getResources().getString(R.string.error_login_failed));
            mErrorText.setVisibility(View.VISIBLE);
        }*/
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            //CALL LOGIN THINGY
            final String url = activity.getString(R.string.base_uri)+ "rest/users/signin";

            RestTemplate restTemplate = new RestTemplate(true, activity.getHttpRequestFactory());

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter()); //TODO: NOT SURE IF NECESSARY
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter()); //TODO: NOT SURE IF NECESSARY

            try{
                Log.d(LOG_TAG, url);

                HttpEntity<User> request = new HttpEntity<User>(new User(mUsername.getText().toString(), mPassword.getText().toString()));
                HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

                /*********************************************************LOG ENTRIES START******/
                for(Map.Entry<String, List<String>> es : response.getHeaders().entrySet()){
                    if(es.getValue()==null){
                        Log.d(LOG_TAG,"Key: "+ es.getKey()+ " EMPTY");
                    }else {
                        for (String value : es.getValue()){
                            Log.d(LOG_TAG,"Key: "+ es.getKey()+ " Value: "+value);
                        }
                    }
                }
                /*********************LOG ENTRIES END***********************************/

                activity.getHttpRequestFactory().setCookieValue(response.getHeaders().get("Set-Cookie").get(0)); //NOT SURE IF GET 0 is VALID AS THE COOKIE apparently cookie value seems to be set already

                retrieveNeeds(); //JUST A TESTWISE THING

                return response.getBody();
            }catch (HttpClientErrorException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
                Log.e(LOG_TAG, e.getResponseBodyAsString(), e);
                return e.getResponseBodyAsString();
            } catch (ResourceAccessException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
                return e.getLocalizedMessage();
            }
        }

        @Override
        protected void onCancelled(String str) {

        }

        protected void onPostExecute(String str) {
            Toast.makeText(activity, str, Toast.LENGTH_LONG).show();
        }

        protected String retrieveNeeds(){
            //CALL LOGIN THINGY
            final String url = activity.getString(R.string.base_uri)+ "rest/needs/";

            RestTemplate restTemplate = new RestTemplate(true, activity.getHttpRequestFactory());

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter()); //TODO: NOT SURE IF NECESSARY
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter()); //TODO: NOT SURE IF NECESSARY

            try{
                Log.d(LOG_TAG, url);

                HttpEntity<String[]> response = restTemplate.getForEntity(url, String[].class);

                /*********************************************************LOG ENTRIES START******/
                for(Map.Entry<String, List<String>> es : response.getHeaders().entrySet()){
                    if(es.getValue()==null){
                        Log.d(LOG_TAG,"Key: "+ es.getKey()+ " EMPTY");
                    }else {
                        for (String value : es.getValue()){
                            Log.d(LOG_TAG,"Key: "+ es.getKey()+ " Value: "+value);
                        }
                    }
                }

                for(String needs : response.getBody()){
                    Log.d(LOG_TAG, needs);
                }
                /*********************LOG ENTRIES END***********************************/

                return "SUCCESS";
            }catch (HttpClientErrorException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
                Log.e(LOG_TAG, e.getResponseBodyAsString(), e);
                return e.getResponseBodyAsString();
            } catch (ResourceAccessException e) {
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
                return e.getLocalizedMessage();
            }
        }
    }
}
