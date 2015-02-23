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
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.components.WonClientHttpRequestFactory;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.StatementCollector;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataService {
    private static final String LOG_TAG = AuthenticationService.class.getSimpleName();

    private WonClientHttpRequestFactory requestFactory;
    private RestTemplate restTemplate;
    private Context context; //used for string resources

    public DataService(AuthenticationService authService){
        this.context = authService.getContext(); //used for stringresource retrieval
        requestFactory = authService.getRequestFactory(); //used for cookie handling within connections
        restTemplate = new RestTemplate(true, requestFactory);

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter()); //TODO: NOT SURE IF NECESSARY
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter()); //TODO: NOT SURE IF NECESSARY
    }

    public ArrayList<Post> getMyPosts(){
        //CALL LOGIN THINGY
        final String url = context.getString(R.string.base_uri)+ context.getString(R.string.needs_path);

        try{
            ArrayList<Post> myPosts = new ArrayList<Post>();
            Log.d(LOG_TAG, url);

            HttpEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
            verboseLogOutput(response);

            //**************************************************************************

            try {
                java.net.URL documentUrl = new URL("http://rsa021.researchstudio.at:8080/won/resource/need/1869023001744244700"); //TODO: SET ACCEPT HEADER TO SET FORMAT
                InputStream inputStream = documentUrl.openStream();

                RDFParser rdfParser = Rio.createParser(RDFFormat.JSONLD);

                org.openrdf.model.Model myGraph = new org.openrdf.model.impl.LinkedHashModel();
                rdfParser.setRDFHandler(new StatementCollector(myGraph));

                rdfParser.parse(inputStream, documentUrl.toString());
            }catch(IOException e){
                e.printStackTrace();
            }catch(RDFParseException e){
                e.printStackTrace();
            }catch(RDFHandlerException e){
                e.printStackTrace();
            }

            //http://rsa021.researchstudio.at:8080/won/resource/need/1869023001744244700
            //http://rsa021.researchstudio.at:8080/won/resource/need/1135026076691464200
            for(String s : response.getBody()) {
                Post p = new Post();

                /*Resource need = model.createResource(s);
                Log.d(LOG_TAG, need.toString());*/
                //TODO: IMPL THIS
                Log.d(LOG_TAG, s);
                p.setTitle(s);
                myPosts.add(p);
            }

            myPosts.addAll(Mock.myMockPosts.values()); //REMOVE THIS LATER

            return myPosts;
        }catch (HttpClientErrorException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            Log.e(LOG_TAG, e.getResponseBodyAsString(), e);
            return new ArrayList<Post>();
        } catch (ResourceAccessException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            return new ArrayList<Post>();
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void verboseLogOutput(HttpEntity<String[]> response){
        for(Map.Entry<String, List<String>> es : response.getHeaders().entrySet()){
            if(es.getValue()==null){
                Log.d(LOG_TAG, "Key: " + es.getKey() + " EMPTY");
            }else {
                for (String value : es.getValue()){
                    Log.d(LOG_TAG,"Key: "+ es.getKey()+ " Value: "+value);
                }
            }
        }
    }
}
