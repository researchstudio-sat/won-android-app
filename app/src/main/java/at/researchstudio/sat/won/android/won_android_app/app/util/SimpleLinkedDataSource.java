package at.researchstudio.sat.won.android.won_android_app.app.util;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.path.Path;
import won.protocol.rest.LinkedDataRestClient;
import won.protocol.util.RdfUtils;
import won.protocol.util.linkeddata.LinkedDataSource;

import java.net.URI;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fsuda on 24.02.2015.
 */
public class SimpleLinkedDataSource implements LinkedDataSource {
    private LinkedDataRestClientAndroid linkedDataRestClient = new LinkedDataRestClientAndroid();

    //private ConcurrentHashMap<URI, Object> myCache = new ConcurrentHashMap<URI, Object>();

    @Override
    public Dataset getDataForResource(URI resourceURI) {
        assert resourceURI != null : "resource must not be null";
        Object dataset = linkedDataRestClient.readResourceData(resourceURI);
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


        OUTER: while (newlyDiscoveredURIs.size() > 0 && depth < maxDepth && requests < maxRequest){
            urisToCrawl = newlyDiscoveredURIs;
            newlyDiscoveredURIs = new HashSet<URI>();
            for (URI currentURI: urisToCrawl) {
                //add all models from urisToCrawl
                Dataset currentModel =  getDataForResource(currentURI);
                RdfUtils.addDatasetToDataset(dataset, currentModel);
                newlyDiscoveredURIs.addAll(getURIsToCrawl(currentModel, crawledURIs, properties));
                crawledURIs.add(currentURI);
                requests++;
                if (requests >= maxRequest) break OUTER;

            }
            depth++;
        }
        return dataset;
    }

    @Override
    public Dataset getDataForResourceWithPropertyPath(URI resourceURI, List<Path> properties, int maxRequest, int maxDepth, boolean moveAllTriplesInDefaultGraph) {
        //TODO: IMPL THIS
        Set<URI> crawledURIs = new HashSet<URI>();
        Set<URI> newlyDiscoveredURIs = new HashSet<URI>();
        Set<URI> urisToCrawl = null;
        newlyDiscoveredURIs.add(resourceURI);
        int depth = 0;
        int requests = 0;

        Dataset resultDataset = DatasetFactory.createMem();

        OUTER: while (newlyDiscoveredURIs.size() > 0 && depth < maxDepth && requests < maxRequest){
            urisToCrawl = newlyDiscoveredURIs;
            newlyDiscoveredURIs = new HashSet<URI>();
            for (URI currentURI: urisToCrawl) {
                //add all models from urisToCrawl

                Dataset currentDataset =  getDataForResource(currentURI);
                if (moveAllTriplesInDefaultGraph){
                    RdfUtils.copyDatasetTriplesToModel(currentDataset, resultDataset.getDefaultModel());
                } else {
                    RdfUtils.addDatasetToDataset(resultDataset, currentDataset);
                }
                newlyDiscoveredURIs.addAll(getURIsToCrawlWithPropertyPath(resultDataset, resourceURI, crawledURIs, properties));
                crawledURIs.add(currentURI);
                requests++;
                if (requests >= maxRequest) break OUTER;

            }
            depth++;
        }
        return resultDataset;

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
            Iterator<URI> newURIs = RdfUtils.getURIsForPropertyPath(dataset,
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
}
