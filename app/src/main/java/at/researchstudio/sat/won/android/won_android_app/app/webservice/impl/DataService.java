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
import at.researchstudio.sat.won.android.won_android_app.app.constants.WonQueriesLocal;
import at.researchstudio.sat.won.android.won_android_app.app.enums.MessageType;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.util.AsyncLinkedDataSource;
import at.researchstudio.sat.won.android.won_android_app.app.util.SimpleLinkedDataSource;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.components.WonClientHttpRequestFactory;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.path.PathParser;
import com.hp.hpl.jena.tdb.TDB;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import won.protocol.model.BasicNeedType;
import won.protocol.model.ConnectionState;
import won.protocol.model.NeedState;
import won.protocol.util.RdfUtils;
import won.protocol.util.linkeddata.LinkedDataSource;
import won.protocol.vocabulary.WON;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

public class DataService {
    private static final String LOG_TAG = DataService.class.getSimpleName();

    private WonClientHttpRequestFactory requestFactory;
    private RestTemplate restTemplate;
    private Context context; //used for string resources
    private Dataset initialDataset;
    private List<URI> myneeds;

    private Vector<Dataset> retrievedDatasets;
    private LinkedDataSource linkedDataSourceAsync;
    private LinkedDataSource linkedDataSourceSync;


    public DataService(AuthenticationService authService){
        this.context = authService.getContext(); //used for stringresource retrieval
        requestFactory = authService.getRequestFactory(); //used for cookie handling within connections
        linkedDataSourceAsync = new AsyncLinkedDataSource();
        linkedDataSourceSync = new SimpleLinkedDataSource();
        restTemplate = new RestTemplate(true, requestFactory);
        retrievedDatasets = new Vector<Dataset>();

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter()); //TODO: NOT SURE IF NECESSARY
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter()); //TODO: NOT SURE IF NECESSARY
    }

    public void retrieveInitialDataset(){
        Log.d(LOG_TAG,"Retrieve initial Dataset");
        //CALL LOGIN THINGY
        final String url = context.getString(R.string.base_uri) + context.getString(R.string.needs_path);

        try{
            HttpEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
            verboseLogOutput(response);
            verboseLogOutput(response);
            verboseLogOutput(response);

            initialDataset = SimpleLinkedDataSource.makeDataset();
            myneeds = new ArrayList<URI>();

            /*ExecutorService es = Executors.newFixedThreadPool(2);
            for(String uriString : response.getBody()) { //COULD BE IMPLEMENTED IN AN ASYNCHRONOUS WAY
                URI uri = URI.create(uriString);
                myneeds.add(uri);
                Log.d(LOG_TAG, "retrieve data for uri: "+uriString);

                RetrievalThread rt = new RetrievalThread(uri, linkedDataSourceAsync);
                es.execute(rt);
            }
            es.shutdown();
            boolean finished = es.awaitTermination(30, TimeUnit.MINUTES);

            for(Dataset ds : retrievedDatasets){
                RdfUtils.addDatasetToDataset(initialDataset, ds);
                //RdfUtils.addDatasetToDataset(initialDataset, ds, true); //TODO: CHANGE ONCE NEW WON APP IS IN ARTIFACTORY
            }*/
        }catch (HttpClientErrorException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            Log.e(LOG_TAG, e.getResponseBodyAsString(), e);
        } catch (ResourceAccessException e) {
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
        } /*catch (InterruptedException e){
            Log.e(LOG_TAG, e.getLocalizedMessage(), e);
        }*/
    }

    class RetrievalThread extends Thread{
        private LinkedDataSource linkedDataSource;
        private URI uri;

        public RetrievalThread(URI uri, LinkedDataSource linkedDataSource){
            this.linkedDataSource = linkedDataSource;
            this.uri= uri;
        }

        public void run(){
            retrievedDatasets.add(linkedDataSource.getDataForResourceWithPropertyPath(uri, configurePropertyPaths(), 10000, 4, false));
            //retrievedDatasets.add(linkedDataSource.getDataForResourceWithPropertyPath(uri, configurePropertyPaths(), 10000, 4, true)); //TODO: CHANGE ONCE NEW WON APP IS IN ARTIFACTORY
        }
    }

    public void resetDataset(){
        initialDataset = null;
    }

    public Map<URI, Post> getMyPosts(){
        if(initialDataset == null){
            retrieveInitialDataset();
        }

        Map<URI,Post> postMap = new HashMap<URI,Post>();

        if(myneeds.size()>0) {
            for (QuerySolution soln : executeQuery(initialDataset, RdfUtils.setSparqlVars(WonQueriesLocal.SPARQL_NEEDS_FILTERED_BY_URI, "need", myneeds))) {
                Post p = new Post(URI.create(soln.get("need").toString()));
                p.setTitle(soln.get("title").toString());
                p.setDescription(soln.get("desc").toString());
                p.setTags(soln.get("tag").toString());
                p.setType(BasicNeedType.fromURI(URI.create(soln.get("type").asResource().getURI())));
                p.setNeedState(NeedState.fromURI(URI.create(soln.get("state").asResource().getURI())));
                //TODO: SET THE OTHER VARIABLES AS WELL
                postMap.put(p.getURI(), p);
            }
        }
        return postMap;
    }

    public ArrayList<Connection> getConnectionsByPostAndState(URI uri, List<URI> states){
        return getConnectionsByPostAndState(Collections.singletonList(uri), states, getMyPosts());
    }

    public ArrayList<Connection> getConnectionsByPostAndState(List<URI> uris, List<URI> states, Map<URI, Post> myPosts){
        if(initialDataset == null){
            retrieveInitialDataset();
        }

        ArrayList<Connection> connectionList = new ArrayList<Connection>();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("need", uris);
        params.put("state", states);

        if(myneeds.size()>0) {
            List<QuerySolution> solutions = executeQuery(initialDataset, RdfUtils.setSparqlVars(WonQueriesLocal.SPARQL_CONNECTIONS_FILTERED_BY_NEED_URI_AND_CONNECTION_STATE, params));

            for (QuerySolution soln : solutions) {
                try {
                    Post myPost = myPosts.get(URI.create(soln.get("localNeed").asResource().getURI()));

                    Connection c = new Connection(URI.create(soln.get("connection").toString()),
                            myPost == null ? getPostById(URI.create(soln.get("localNeed").asResource().getURI())) : myPost,
                            getPostById(URI.create(soln.get("remoteNeed").asResource().getURI())),
                            getMessagesByConnectionId(URI.create(soln.get("connection").toString())),
                            ConnectionState.fromURI(URI.create(soln.get("state").asResource().getURI()))
                    );
                    connectionList.add(c);
                } catch (IllegalArgumentException e) {
                    Log.e(LOG_TAG, "URI OF SOLUTION WAS INVALID, SOLUTION WILL BE SKIPPED, value of solution: " + soln.toString());
                }
            }
        }

        return connectionList;
    }

    public ArrayList<MessageItemModel> getMessagesByConnectionId(URI uri) {
        ArrayList<MessageItemModel> messageList = new ArrayList<MessageItemModel>();

        List<QuerySolution> solutions = executeQuery(initialDataset, RdfUtils.setSparqlVars(WonQueriesLocal.SPARQL_EVENTS_BY_CONNECTION_URI, "connection", uri));

        for(QuerySolution soln : solutions){
            try {
                //TODO: CHANGE THIS
                RDFNode msgText = soln.get("msgText");
                RDFNode msgType = soln.get("msgType");
                MessageItemModel msg;
                if(msgText!=null) {
                    msg = new MessageItemModel(MessageType.RECEIVE, msgText.toString());
                }else{
                    msg = new MessageItemModel(MessageType.SYSTEM, msgType==null? "NO TYPE" :msgType.asResource().getURI());
                }

                messageList.add(msg);
            }catch (IllegalArgumentException e){
                Log.e(LOG_TAG, "URI OF SOLUTION WAS INVALID, SOLUTION WILL BE SKIPPED, value of solution: "+ soln.toString());
            }
        }
        return messageList;
    }

    public ArrayList<Connection> getConnectionsByState(List<URI> states) {
        return getConnectionsByPostAndState(myneeds, states, getMyPosts());
    }

    public Post getPostById(URI uri) {
        if(initialDataset == null){
            retrieveInitialDataset();
        }

        ArrayList<Post> postList = new ArrayList<Post>();

        for(QuerySolution soln : executeQuery(initialDataset, RdfUtils.setSparqlVars(WonQueriesLocal.SPARQL_NEEDS_FILTERED_BY_URI, "need", uri))){
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = soln.varNames();

            Post p = new Post(URI.create(soln.get("need").toString()));
            p.setTitle(soln.get("title").toString());
            p.setDescription(soln.get("desc").toString());
            p.setTags(soln.get("tag").toString());
            p.setType(BasicNeedType.fromURI(URI.create(soln.get("type").asResource().getURI())));
            p.setNeedState(NeedState.fromURI(URI.create(soln.get("state").asResource().getURI())));
            //TODO: SET THE OTHER VARIABLES AS WELL
            postList.add(p);
        }

        if (postList.size() == 0) {
            Log.d(LOG_TAG, "Getting Post by id with linkedDataSource: "+uri);
            //TODO: THIS RECURSION DOES NOT SEEM TO BE THAT GREAT
            //RdfUtils.addDatasetToDataset(initialDataset, linkedDataSourceSync.getDataForResourceWithPropertyPath(uri, configurePropertyPaths(), 10000, 1, false)); //TODO: find a good depth, and also find a better caching algorithm thingy instead of ehcache
            RdfUtils.addDatasetToDataset(initialDataset, linkedDataSourceSync.getDataForResource(uri));

            return getPostById(uri);
        }

        return postList.get(0);
    }

    public Connection getConnectionById(URI uri) {
        if(initialDataset == null){
            retrieveInitialDataset();
        }

        ArrayList<Connection> connectionList = new ArrayList<Connection>();

        for(QuerySolution soln : executeQuery(initialDataset, RdfUtils.setSparqlVars(WonQueriesLocal.SPARQL_CONNECTION_FILTERED_BY_CONNECTION_URI, "connection", uri))){
            Connection c = new Connection(URI.create(soln.get("connection").toString()),
                    getPostById(URI.create(soln.get("localNeed").asResource().getURI())),
                    getPostById(URI.create(soln.get("remoteNeed").asResource().getURI())),
                    getMessagesByConnectionId(uri),
                    ConnectionState.fromURI(URI.create(soln.get("state").asResource().getURI()))
            );
            connectionList.add(c);
        }

        if (connectionList.size() == 0) {
            Log.d(LOG_TAG, "Getting Connection by id with linkedDataSource: "+uri);
            //TODO: THIS RECURSION DOES NOT SEEM TO BE THAT GREAT
            //RdfUtils.addDatasetToDataset(initialDataset, linkedDataSourceSync.getDataForResourceWithPropertyPath(uri, configurePropertyPaths(), 10000, 1, false)); //TODO: find a good depth, and also find a better caching algorithm thingy instead of ehcache
            RdfUtils.addDatasetToDataset(initialDataset, linkedDataSourceSync.getDataForResource(uri));

            return getConnectionById(uri);
        }

        return connectionList.get(0);
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
        addPropertyPath(propertyPaths, "<" + WON.HAS_CONNECTIONS + ">" + "/" + "rdfs:member" + "/<" + WON.HAS_EVENT_CONTAINER + ">/rdfs:member");
        addPropertyPath(propertyPaths, "<" + WON.HAS_CONNECTIONS + ">" + "/" + "rdfs:member" + "/<" + WON.HAS_REMOTE_CONNECTION + ">/<" +WON.BELONGS_TO_NEED + ">");
        return propertyPaths;
    }

    public static void addPropertyPath(final List<Path> propertyPaths, String pathString) {
        Path path = PathParser.parse(pathString, PrefixMapping.Standard);
        propertyPaths.add(path);
    }


    public static List<QuerySolution> executeQuery(Dataset dataset, String statement){
        return executeQuery(dataset, statement, new QuerySolutionMap());
    }

    public static List<QuerySolution> executeQuery(Dataset dataset, String statement, QuerySolutionMap initialBinding){
        ArrayList<QuerySolution> results = new ArrayList<QuerySolution>();
        QueryExecution qExec = null;

        try{
            Query query = QueryFactory.create(statement);

            //QuerySolutionMap initialBinding = new QuerySolutionMap();
            // InitialBindings are used to set filters on the resultset
            //initialBinding.add("need", needDataset.getDefaultModel().createResource(uri.toString()));
            qExec = QueryExecutionFactory.create(query, dataset, initialBinding);

            qExec.getContext().set(TDB.symUnionDefaultGraph, true);
            ResultSet rs = qExec.execSelect();

            while(rs != null && rs.hasNext()){
                results.add(rs.nextSolution());
            }
        } catch (QueryParseException e) {
            Log.e(LOG_TAG, "INVALID SPARQL-QUERY: " + e.getMessage());
        }finally {
            if(qExec!=null && !qExec.isClosed()) {
                qExec.close();
            }
        }
        return results;
    }

    //TODO: METHODS BELOW ARE ALREADY IN THE NEWER VERSION OF THE ONE LIBS REMOVE THOSE
    private void printResults(ResultSet results){
        Log.d(LOG_TAG, "---------------------------RESULTS-----------------------------------");
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();

            StringBuilder sb = new StringBuilder();
            Iterator<String> it = soln.varNames();

            while(it.hasNext()){
                String var = it.next();
                sb.append(var).append(": ").append(soln.get(var)).append(" ");
            }
            Log.d(LOG_TAG, sb.toString());
        }
        Log.d(LOG_TAG, "---------------------------------------------------------------------");
    }
}
