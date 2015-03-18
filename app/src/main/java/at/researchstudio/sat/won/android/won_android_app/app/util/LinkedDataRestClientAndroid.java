package at.researchstudio.sat.won.android.won_android_app.app.util;

import android.util.Log;
import com.hp.hpl.jena.query.Dataset;
import org.apache.commons.lang3.time.StopWatch;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import won.protocol.rest.DatasetReaderWriter;
import won.protocol.rest.RDFMediaType;
import won.protocol.util.linkeddata.LinkedDataSource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;

/**
 * Created by fsuda on 24.02.2015.
 */
public class LinkedDataRestClientAndroid {
    private static final String LOG_TAG = LinkedDataSource.class.getSimpleName();

    private ClientConfig cc = new ClientConfig();
    private Client c;

    public LinkedDataRestClientAndroid() {
        cc = new ClientConfig();
        cc.property(ClientProperties.FOLLOW_REDIRECTS, true);
        cc.register(DatasetReaderWriter.class);
        c = ClientBuilder.newClient(cc);
    }

    /**
     * Retrieves RDF for the specified resource URI.
     * Expects that the resource URI will lead to a 303 response, redirecting to the URI where RDF can be downloaded.
     * Paging is not supported.
     *
     * @param resourceURI
     * @return
     */
    public Dataset readResourceData(URI resourceURI){
        assert resourceURI != null : "resource URI must not be null";

        WebTarget r = c.target(resourceURI);
        //TODO: improve error handling
        //If a ClientHandlerException is thrown here complaining that it can't read a Model with MIME media type text/html,
        //it was probably the wrong resourceURI
        Dataset result;

        try {
            result = r.request(RDFMediaType.APPLICATION_TRIG).get(Dataset.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    MessageFormat.format(
                            "caught a clientHandler exception, " +
                                    "which may indicate that the URI that was accessed isn''t a" +
                                    " linked data URI, please check {0}", resourceURI), e);
        }
        return result;
    }
}
