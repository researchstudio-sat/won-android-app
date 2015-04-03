package at.researchstudio.sat.won.android.won_android_app.app.model;

import java.net.URI;

/**
 * Created by fsuda on 14.10.2014.
 */
public abstract class Model {
    protected static final String URI_REF ="URI";
    private URI uri;

    public URI getURI(){
        return uri;
    }

    public String getURIString(){
        return uri.toString();
    }

    public void setURI(URI uri){
        this.uri = uri;
    }

    public void setURI(String uri){
        this.uri = URI.create(uri);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Model model = (Model) o;

        if (!uri.equals(model.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }
}
