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
import android.text.Editable;
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
import at.researchstudio.sat.won.android.won_android_app.app.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

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
            Editable userName = mUsername.getText();
            Editable pass = mPassword.getText();

            Log.d(LOG_TAG, "Username: "+mUsername.getText());
            Log.d(LOG_TAG, "Password: "+mPassword.getText());
            //CALL LOGIN THINGY
            final String url = activity.getString(R.string.base_uri)+ "rest/users/signin";

            HttpHeaders requestHeaders = new HttpHeaders();

            requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            RestTemplate restTemplate = new RestTemplate(false);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

            try{
                Log.d(LOG_TAG, url);

                HttpEntity<User> request = new HttpEntity<User>(new User(mUsername.getText().toString(), mPassword.getText().toString()), requestHeaders);
                return restTemplate.postForObject(url, request, String.class);

            }catch(HttpMessageNotReadableException e) {
                //TODO: THIS IS DUE TO THE FACT THAT WE DO NOT GET A VALID JSON FROM THE SERVER
                return "LOGGED IN";
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
    }
}
