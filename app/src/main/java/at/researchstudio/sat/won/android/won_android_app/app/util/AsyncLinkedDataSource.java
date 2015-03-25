/*
 * Copyright 2015 Research Studios Austria Forschungsges.m.b.H.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.util;

import android.util.Log;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueBoolean;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.springframework.web.client.RestClientException;
import won.protocol.rest.LinkedDataRestClient;
import won.protocol.util.RdfUtils;
import won.protocol.util.linkeddata.LinkedDataSource;

import java.net.URI;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by fsuda on 17.03.2015.
 */
public class AsyncLinkedDataSource implements LinkedDataSource {
    private static final String LOG_TAG = AsyncLinkedDataSource.class.getSimpleName();
    private LinkedDataRestClient linkedDataRestClient = new LinkedDataRestClient(1000,5000); //SET A TIMEOUT OF A SECOND FOR THOSE URIS

    private ConcurrentHashMap<URI, Object> myCache = new ConcurrentHashMap<URI, Object>();

    @Override
    public Dataset getDataForResource(URI resourceURI) {
        assert resourceURI != null : "resource must not be null";

        Object dataset = myCache.get(resourceURI);

        if (dataset == null) {
            try {
                dataset = linkedDataRestClient.readResourceData(resourceURI);
                Log.d(LOG_TAG, "PUT uri: " + resourceURI + " into cache");
                myCache.put(resourceURI, dataset);
            }catch(RestClientException e){
                Log.e(LOG_TAG, "Error while retrieving from URI: "+resourceURI);
                return null;
            }
        } else {
            Log.d(LOG_TAG, "GOT uri: " + resourceURI + " from cache");
        }

        if (dataset instanceof Dataset) return (Dataset) dataset;
        throw new IllegalStateException(
                new MessageFormat("The underlying linkedDataCache should only contain Datasets, but we got a {0} for URI {1}")
                        .format(new Object[]{dataset.getClass(), resourceURI}));
    }

    @Override
    public Dataset getDataForResource(URI resourceURI, List<URI> properties, int maxRequest, int maxDepth) {
        Set<URI> crawledURIs = new HashSet<URI>();
        Set<URI> newlyDiscoveredURIs = new HashSet<URI>();
        Set<URI> urisToCrawl = null;
        newlyDiscoveredURIs.add(resourceURI);
        int depth = 0;
        int requests = 0;

        Dataset dataset = getDataForResource(resourceURI);

        if(dataset!=null) {
            OUTER:
            while (newlyDiscoveredURIs.size() > 0 && depth < maxDepth && requests < maxRequest) {
                urisToCrawl = newlyDiscoveredURIs;
                newlyDiscoveredURIs = new HashSet<URI>();
                for (URI currentURI : urisToCrawl) {
                    //add all models from urisToCrawl
                    Dataset currentModel = getDataForResource(currentURI);
                    if (currentModel != null) {
                        RdfUtils.addDatasetToDataset(dataset, currentModel, true);
                        newlyDiscoveredURIs.addAll(getURIsToCrawl(currentModel, crawledURIs, properties));
                    }
                    crawledURIs.add(currentURI);
                    requests++;
                    if (requests >= maxRequest) break OUTER;

                }
                depth++;
            }
        }
        return dataset;
    }

    @Override
     public Dataset getDataForResourceWithPropertyPath(URI resourceURI, List<Path> properties, int maxRequest, int maxDepth, boolean moveAllTriplesInDefaultGraph){
        Set<URI> crawledURIs = new HashSet<URI>();
        Set<URI> newlyDiscoveredURIs = new HashSet<URI>();
        Set<URI> urisToCrawl = null;
        newlyDiscoveredURIs.add(resourceURI);
        int depth = 0;
        int requests = 0;

        Dataset resultDataset = makeDataset();

        OUTER: while (newlyDiscoveredURIs.size() > 0 && depth < maxDepth && requests < maxRequest){
            //ExecutorService es = Executors.newCachedThreadPool();
            ExecutorService es = Executors.newFixedThreadPool(10);
            urisToCrawl = newlyDiscoveredURIs;
            newlyDiscoveredURIs = new HashSet<URI>();

            ConcurrentHashMap<URI, Dataset> retrievedDatasets = new ConcurrentHashMap<URI, Dataset>();

            for(URI currentURI: urisToCrawl){
                RetrievalThread rt = new RetrievalThread(currentURI, retrievedDatasets, linkedDataRestClient);
                es.execute(rt);
            }

            es.shutdown();
            try {
                boolean finished = es.awaitTermination(30, TimeUnit.MINUTES);
            }catch (InterruptedException e){
                Log.e(LOG_TAG, e.getLocalizedMessage(), e);
            }

            for(Map.Entry<URI,Dataset> entry : retrievedDatasets.entrySet()){
                //add all models from urisToCrawl

                Dataset currentDataset =  entry.getValue();
                //logger.debug("current dataset: {} "+RdfUtils.toString(currentModel));
                if(currentDataset!=null) {
                    if (moveAllTriplesInDefaultGraph) {
                        RdfUtils.copyDatasetTriplesToModel(currentDataset, resultDataset.getDefaultModel());
                    } else {
                        RdfUtils.addDatasetToDataset(resultDataset, currentDataset, true);
                    }
                    newlyDiscoveredURIs.addAll(getURIsToCrawlWithPropertyPath(resultDataset, resourceURI, crawledURIs, properties));
                }
                crawledURIs.add(entry.getKey());
                requests++;
                if (requests >= maxRequest) break OUTER;
            }
            depth++;
        }

        Log.d(LOG_TAG, "Crawled URI: "+resourceURI+" - requests made: "+requests);
        return resultDataset;
    }

    class RetrievalThread extends Thread{
        private URI uri;
        private ConcurrentHashMap<URI, Dataset> retrievedDatasets;

        public RetrievalThread(URI uri, ConcurrentHashMap<URI, Dataset> retrievedDatasets, LinkedDataRestClient linkedDataRestClient){
            this.uri= uri;
            this.retrievedDatasets = retrievedDatasets;
        }

        public void run(){
            Log.d(LOG_TAG, this.getId() + " CrawlThread started");
            try{
                retrievedDatasets.put(uri, getDataForResource(uri));
            }catch(NullPointerException e){
                Log.e(LOG_TAG, "NPE! URI OR DATASETMAP NULL: uri: " + uri + " ds: " + retrievedDatasets);
            }
            Log.d(LOG_TAG, this.getId() + "CrawlThread done.");
        }

        public Dataset getDataForResource(URI resourceURI) {
            LinkedDataRestClient linkedDataRestClient = new LinkedDataRestClient();
            assert resourceURI != null : "resource must not be null";

            Object dataset = myCache.get(resourceURI);
            //TODO: PUT IF ABSENT

            if (dataset == null) {
                try {
                    dataset = linkedDataRestClient.readResourceData(resourceURI);
                    Log.d(LOG_TAG, this.getId() + " PUT uri: " + resourceURI + " into cache");
                    myCache.put(resourceURI, dataset);
                }catch(RestClientException e){
                    Log.e(LOG_TAG, this.getId() + " Error while retrieving from URI: "+resourceURI);
                    return null;
                }
            } else {
                Log.d(LOG_TAG, this.getId() + " GOT uri: " + resourceURI + " from cache");
            }

            if (dataset instanceof Dataset) return (Dataset) dataset;
            /*throw new IllegalStateException(
                    new MessageFormat("The underlying linkedDataCache should only contain Datasets, but we got a {0} for URI {1}")
                            .format(new Object[]{dataset.getClass(), resourceURI}));*/
            return null;
        }
    }

    /**
     * For the specified resourceURI, evaluates the specified property paths and adds the identified
     * resources to the returned set if they are not contained in the specified exclude set.
     * @param dataset
     * @param resourceURI
     * @param excludedUris
     * @param properties
     * @return
     */
    private Set<URI> getURIsToCrawlWithPropertyPath(Dataset dataset, URI resourceURI, Set<URI> excludedUris, List<Path> properties){
        Set<URI> toCrawl = new HashSet<URI>();
        for (int i = 0; i<properties.size();i++){
            Iterator<URI> newURIs = RdfUtils.getURIsForPropertyPathByQuery(dataset,
                    resourceURI,
                    properties.get(i));
            while (newURIs.hasNext()){
                URI newUri = newURIs.next();
                if (!excludedUris.contains(newUri)) {
                    toCrawl.add(newUri);
                }
            }
        }
        return toCrawl;
    }

    /**
     * For the specified properties, finds their objects and adds the identified
     * resources to the returned set if they are not contained in the specified exclude set.
     * @param dataset
     @param excludedUris
      * @param properties
     * @return
     */
    private Set<URI> getURIsToCrawl(Dataset dataset, Set<URI> excludedUris, final List<URI> properties) {
        Set<URI> toCrawl = new HashSet<URI>();
        for (int i = 0; i<properties.size();i++){
            final URI property = properties.get(i);
            NodeIterator objectIterator = RdfUtils.visitFlattenedToNodeIterator(dataset, new RdfUtils.ModelVisitor<NodeIterator>()
            {
                @Override
                public NodeIterator visit(final Model model) {
                    final Property p = model.createProperty(property.toString());
                    return model.listObjectsOfProperty(p);
                }
            });
            for (;objectIterator.hasNext();){
                RDFNode objectNode = objectIterator.next();

                if (objectNode.isURIResource()) {
                    URI discoveredUri = URI.create(objectNode.asResource().getURI());
                    if (!excludedUris.contains(discoveredUri)){
                        toCrawl.add(discoveredUri);
                    }
                }
            }

        }
        return toCrawl;
    }

    public static Dataset makeDataset() {
        Log.d(LOG_TAG,"Creating tdb dataset...");
        DatasetGraph dsg = TDBFactory.createDatasetGraph();
        dsg.getContext().set(TDB.symUnionDefaultGraph, new NodeValueBoolean(true));
        return DatasetFactory.create(dsg);
    }
}
