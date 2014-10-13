package at.researchstudio.sat.won.android.won_android_app.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsuda on 24.09.2014.
 */
public class PostListItemModel {
    public static final String TAG_SEPARATOR = ", ";
    public String title;
    public String description;
    public List<String> tags;
    public int matches;
    public int conversations;
    public int requests;
    public String imageUrl; //PROBABLY NEEDS TO CHANGE TYPE AND STUFF
    public PostType type;

    public PostListItemModel(String title, String description, List<String> tags, int matches, int requests, int conversations, String imageUrl, PostType type) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.matches = matches;
        this.requests = requests;
        this.conversations = conversations;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
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

    public int getConversations() {
        return conversations;
    }

    public void setConversations(int conversations) {
        this.conversations = conversations;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public boolean hasNotifications(){
        return matches != 0 || conversations != 0 || requests != 0;
    }

    @Override
    public String toString() {
        return "PostListItemModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", matches=" + matches +
                ", conversations=" + conversations +
                ", requests=" + requests +
                ", imageUrl=" + imageUrl +
                ", type=" + type +
                '}';
    }
}
