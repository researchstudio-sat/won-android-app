package at.researchstudio.sat.won.android.won_android_app.app.model.builder;

import android.util.Log;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import com.google.android.gms.maps.model.LatLng;
import won.protocol.model.NeedState;
import won.protocol.util.NeedBuilderBase;

import java.util.Arrays;

/**
 * Created by fsuda on 25.02.2015.
 */
public class PostModelBuilder extends NeedBuilderBase<Post> {
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

        Log.d(LOG_TAG, ""+getURI());

        switch (getStateNS()){
            case ACTIVE:
                post.setClosed(false);
                break;
            case INACTIVE:
                post.setClosed(true);
                break;
        }
        //TODO: FULL IMPLEMENTATION
        return post;
    }

    @Override
    public void copyValuesFromProduct(Post post) {
        setDescription(post.getDescription());
        setTitle(post.getTitle());
        setTags((String[])post.getTags().toArray());
        //setAvailableAtLocation(post.getLocation().latitude,post.getLocation().longitude);

        setState(post.isClosed()? NeedState.INACTIVE : NeedState.ACTIVE);
        //TODO: FULL IMPLEMENTATION
        setBasicNeedType(post.getType());
        setUri(post.getURI());
    }
}
