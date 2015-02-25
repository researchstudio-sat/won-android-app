package at.researchstudio.sat.won.android.won_android_app.app.util;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sparql.path.Path;
import won.protocol.rest.LinkedDataRestClient;
import won.protocol.util.linkeddata.LinkedDataSource;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by fsuda on 24.02.2015.
 */
public class SimpleLinkedDataSourceImpl implements LinkedDataSource {
    private LinkedDataRestClientAndroid linkedDataRestClient = new LinkedDataRestClientAndroid();

    @Override
    public Dataset getDataForResource(URI resource) {
        assert resource != null : "resource must not be null";
        Object dataset = linkedDataRestClient.readResourceData(resource);
        if (dataset instanceof Dataset) return (Dataset) dataset;
        throw new IllegalStateException(
                new MessageFormat("The underlying linkedDataCache should only contain Datasets, but we got a {0} for URI {1}")
                        .format(new Object[]{dataset.getClass(), resource}));
    }

    @Override
    public Dataset getDataForResource(URI resource, List<URI> properties, int maxRequest, int maxDepth) {
        return null;
    }

    @Override
    public Dataset getDataForResourceWithPropertyPath(URI resource, List<Path> properties, int maxRequest, int maxDepth, boolean moveAllTriplesInDefaultGraph) {
        return null;
    }
}
