package at.researchstudio.sat.won.android.won_android_app.app.model;

import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.RepeatType;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by fsuda on 10.10.2014.
 */
public class Post extends Model {
    public static final String ID_REF = "post_id_ref";
    public static final String TAG_SEPARATOR = ", ";

    private PostType type;
    private String title;
    private String description;

    private List<String> tags;
    private int matches;
    private int conversations;
    private int requests;

    private List<String> imageUrls;
    private String titleImageUrl;

    private LatLng location;

    private long startTime;
    private long stopTime;

    private RepeatType repeat;

    public Post() {
        super(null);
    }

    public Post(PostType type, String title, String description, List<String> tags, int matches, int conversations, int requests, List<String> imageUrls, String titleImageUrl, LatLng location, long startTime, long stopTime, RepeatType repeat) {
        this(null, type, title, description, tags, matches, conversations, requests, imageUrls, titleImageUrl, location, startTime, stopTime, repeat);
    }

    public Post(UUID uuid, PostType type, String title, String description, List<String> tags, int matches, int conversations, int requests, List<String> imageUrls, String titleImageUrl, LatLng location, long startTime, long stopTime, RepeatType repeat) {
        super(uuid);
        this.type = type;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.matches = matches;
        this.conversations = conversations;
        this.requests = requests;
        this.imageUrls = imageUrls;
        this.titleImageUrl = titleImageUrl;
        this.location = location;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.repeat = repeat;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getTitleImageUrl() {
        return titleImageUrl;
    }

    public void setTitleImageUrl(String titleImageUrl) {
        this.titleImageUrl = titleImageUrl;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public RepeatType getRepeat() {
        return repeat;
    }

    public void setRepeat(RepeatType repeat) {
        this.repeat = repeat;
    }

    public void setTags(String tagString){
        List<String> tags = new ArrayList<String>();

        for(String tag : tagString.split(",")){
            tags.add(tag.trim());
        }

        this.tags = tags;
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
        return "Post{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", matches=" + matches +
                ", conversations=" + conversations +
                ", requests=" + requests +
                ", imageUrls=" + imageUrls +
                ", titleImageUrl='" + titleImageUrl + '\'' +
                ", location=" + location +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", repeat=" + repeat +
                '}';
    }
}
