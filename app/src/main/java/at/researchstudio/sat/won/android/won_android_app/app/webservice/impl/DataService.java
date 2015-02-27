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
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.model.builder.PostModelBuilder;
import at.researchstudio.sat.won.android.won_android_app.app.util.SimpleLinkedDataSource;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.components.WonClientHttpRequestFactory;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.path.PathParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import won.protocol.util.NeedModelBuilder;
import won.protocol.util.RdfUtils;
import won.protocol.util.linkeddata.LinkedDataSource;
import won.protocol.vocabulary.WON;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class DataService {
    private static final String LOG_TAG = DataService.class.getSimpleName();

    private static final String SPARQL_PREFIX = "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>"+
                                                "PREFIX geo:   <http://www.w3.org/2003/01/geo/wgs84_pos#>"+
                                                "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>"+
                                                "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
                                                "PREFIX won:   <http://purl.org/webofneeds/model#>"+
                                                "PREFIX gr:    <http://purl.org/goodrelations/v1#>"+
                                                "PREFIX sioc:  <http://rdfs.org/sioc/ns#>"+
                                                "PREFIX ldp:   <http://www.w3.org/ns/ldp#>";

    private WonClientHttpRequestFactory requestFactory;
    private RestTemplate restTemplate;
    private Context context; //used for string resources
    private LinkedDataSource linkedDataSource = new SimpleLinkedDataSource();

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
            //http://rsa021.researchstudio.at:8080/won/resource/need/1869023001744244700
            //http://rsa021.researchstudio.at:8080/won/resource/need/1135026076691464200

            for(String s : response.getBody()) { //COULD BE IMPLEMENTED IN AN ASYNCHRONOUS WAY
                crawlDataForPost(URI.create(s));
                Post p = getPostById(URI.create(s));

                if(p!=null){
                    myPosts.add(p);
                }
            }

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

    public Post getPostById(URI uri) {
        Dataset needDataset = linkedDataSource.getDataForResource(uri);

        //simple,insecure implementation: iterate over models, try to extract the 'need' data
        final NeedModelBuilder builder = new NeedModelBuilder();
        RdfUtils.visit(needDataset, new RdfUtils.ModelVisitor<NeedModelBuilder>() {
            @Override
            public NeedModelBuilder visit(Model model) {
                try {
                    builder.copyValuesFromProduct(model);
                } catch (Exception e) {
                    return null;
                }
                return null;
            }
        });

        if(builder!=null) {
            PostModelBuilder postModelBuilder = new PostModelBuilder();
            builder.copyValuesToBuilder(postModelBuilder);
            return postModelBuilder.build();
        }
        return null;
    }

    public Dataset crawlDataForPost(URI uri){
        Dataset needDataset = linkedDataSource.getDataForResourceWithPropertyPath(uri,configurePropertyPaths(),8,300,true);
        String queryString = SPARQL_PREFIX +
                "SELECT ?need ?connection ?need2 WHERE {" +
                "   ?need won:hasConnections ?connections ." +
                "?connections rdfs:member ?connection ." +
                "?connection won:hasRemoteConnection ?connection2."+
                "?connection2 won:belongsToNeed ?need2 ." +
                "}";

        Query query = QueryFactory.create(queryString);
        QuerySolutionMap initialBinding = new QuerySolutionMap();
        initialBinding.add("need",needDataset.getDefaultModel().createResource(uri.toString()));
        QueryExecution qExec = QueryExecutionFactory.create(query, needDataset);
        ResultSet results = qExec.execSelect();

        Log.d(LOG_TAG,"need: " + uri);
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            Log.d(LOG_TAG, "con:" + soln.get("?connection") + ", need2:" + soln.get("?need2"));
        }
        qExec.close();
        return needDataset;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private static void verboseLogOutput(HttpEntity<String[]> response){
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

    /***
     * Build the property paths needed for crawling need data
     */

    public static List<Path> configurePropertyPaths(){
        List<Path> propertyPaths = new ArrayList<Path>();
        addPropertyPath(propertyPaths, "<" + WON.HAS_CONNECTIONS + ">");
        addPropertyPath(propertyPaths, "<" + WON.HAS_CONNECTIONS + ">" + "/" + "rdfs:member");
        addPropertyPath(propertyPaths, "<" + WON.HAS_CONNECTIONS + ">" + "/" + "rdfs:member" + "/<" + WON.HAS_REMOTE_CONNECTION + ">");
        addPropertyPath(propertyPaths, "<" + WON.HAS_CONNECTIONS + ">" + "/" + "rdfs:member" + "/<" + WON.HAS_EVENT_CONTAINER + ">/<rdfs:member>");
        addPropertyPath(propertyPaths, "<" + WON.HAS_CONNECTIONS + ">" + "/" + "rdfs:member" + "/<" + WON.HAS_REMOTE_CONNECTION + ">/<" +WON.BELONGS_TO_NEED + ">");
        return propertyPaths;
    }

    public static void addPropertyPath(final List<Path> propertyPaths, String pathString) {
        Path path = PathParser.parse(pathString, PrefixMapping.Standard);
        propertyPaths.add(path);
    }
}
