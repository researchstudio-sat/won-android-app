package at.researchstudio.sat.won.android.won_android_app.app.model.builder;

import android.util.Log;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import com.google.android.gms.maps.model.LatLng;
import won.protocol.model.NeedState;
import won.protocol.util.NeedBuilderBase;
import won.protocol.util.NeedModelBuilder;

import java.util.Arrays;

/**
 * Created by fsuda on 25.02.2015.
 */
public class PostModelBuilder extends NeedBuilderBase<Post> {
    //TODO: FULL IMPL
    private static final String LOG_TAG = PostModelBuilder.class.getSimpleName();

    @Override
    public Post build() {
        Post post = new Post(getURI());
        post.setDescription(getDescription());
        post.setTitle(getTitle());
        Float lat = getAvailableAtLocationLatitude();
        Float lng = getAvailableAtLocationLongitude();

        if(lat != null && lng != null) {
            post.setLocation(new LatLng(getAvailableAtLocationLatitude(), getAvailableAtLocationLongitude()));
        }

        post.setTags(Arrays.asList(getTagsArray()));
        post.setType(getBasicNeedTypeBNT());
        post.setNeedState(getStateNS());

        return post;
    }

    @Override
    public void copyValuesFromProduct(Post post) {
        setUri(post.getURI());
        setDescription(post.getDescription());
        setTitle(post.getTitle());
        setTags(post.getTags().toArray(new String[post.getTags().size()]));
        setState(post.getNeedState());
        setBasicNeedType(post.getType());
        setState(post.getNeedState());
        if(post.getLocation()!=null) {
            setAvailableAtLocation((float) post.getLocation().latitude, (float) post.getLocation().longitude); //TODO: NOT SURE IF LOCATION IS LOST SOMEHOW DUE TO DOUBLE TO FLOAT CAST
        }
    }
}
