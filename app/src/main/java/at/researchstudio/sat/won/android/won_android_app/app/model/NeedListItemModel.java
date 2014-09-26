package at.researchstudio.sat.won.android.won_android_app.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsuda on 24.09.2014.
 */
public class NeedListItemModel {
    public static final String TAG_SEPARATOR = ", ";
    public String title;
    public List<String> tags;
    public int matches;
    public int imageRes; //PROBABLY NEEDS TO CHANGE TYPE AND STUFF

    public NeedListItemModel(String title) {
        this(title, new ArrayList<String>(), 0, 0);
    }

    public NeedListItemModel(String title, List<String> tags, int matches, int imageRes) {
        this.title = title;
        this.tags = tags;
        this.matches = matches;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public String getTagsAsString() {
        StringBuilder sb = new StringBuilder();

        if(tags!=null) {
            for (String tag : tags) {
                sb.append(tag).append(", ");
            }

            return sb.substring(0, sb.length()-TAG_SEPARATOR.length()); //return without last Separator
        }else{
            return "";
        }
    }

    @Override
    public String toString() {
        return "NeedListItemModel{" +
                "title='" + title + '\'' +
                ", tags=" + tags +
                ", matches=" + matches +
                '}';
    }
}
