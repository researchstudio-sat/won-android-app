/*
 * Copyright 2014 Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.RepeatType;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by fsuda on 10.10.2014.
 */
public class Post extends Model implements Parcelable {
    public static final String ID_REF = "post_id_ref";
    public static final String TYPE_REF = "post_type_ref";
    public static final String TITLE_REF = "post_title_ref";
    public static final String DESC_REF = "post_description_ref";
    public static final String TAGS_REF = "post_tags_ref";
    public static final String MATCHES_REF = "post_matches_ref";
    public static final String CONVERSATIONS_REF = "post_conversations_ref";
    public static final String REQUESTS_REF = "post_requests_ref";
    public static final String IMAGEURLS_REF = "post_imageurl_ref";
    public static final String TITLEIMAGEURL_REF = "post_titleimageurl_ref";
    public static final String LOCATION_LAT_REF = "post_lat_ref";
    public static final String LOCATION_LNG_REF = "post_lng_ref";
    public static final String STARTTIME_REF = "post_starttime_ref";
    public static final String STOPTIME_REF = "post_stoptime_ref";
    public static final String CLOSED_REF = "post_closed_ref";
    public static final String REPEAT_REF = "post_repeat_ref";

    public static final String TAG_SEPARATOR = ",";

    private PostType type;
    private String title;
    private String description;

    private List<String> tags;
    private int matches;
    private int conversations;
    private int requests;

    private List<String> imageUrls;
    //TODO: REFACTOR TITLEIMAGEURL to titleimageindex, and store the url within the imageurl list
    private String titleImageUrl;

    private LatLng location;

    private long startTime;
    private long stopTime;

    private boolean closed;

    private RepeatType repeat;

    public Post() {
        super(null);
        this.type = PostType.WANT;
        this.repeat = RepeatType.NONE;
        this.location = new LatLng(0,0);
        this.imageUrls = new ArrayList<String>();
        this.tags = new ArrayList<String>();
    }

    public Post(PostType type, String title, String description, List<String> tags, int matches, int conversations, int requests, List<String> imageUrls, String titleImageUrl, LatLng location, long startTime, long stopTime, RepeatType repeat, boolean closed) {
        this(null, type, title, description, tags, matches, conversations, requests, imageUrls, titleImageUrl, location, startTime, stopTime, repeat, closed);
    }

    public Post(Parcel in) {
        super(null);
        Bundle bundle = in.readBundle();
        setUuid(bundle.getString(UUID_REF));

        this.type           = PostType.values()[bundle.getInt(TYPE_REF)];
        this.title          = bundle.getString(TITLE_REF);
        this.description    = bundle.getString(DESC_REF);
        this.tags           = bundle.getStringArrayList(TAGS_REF);
        this.matches        = bundle.getInt(MATCHES_REF);
        this.conversations  = bundle.getInt(CONVERSATIONS_REF);
        this.requests       = bundle.getInt(REQUESTS_REF);
        this.imageUrls      = bundle.getStringArrayList(IMAGEURLS_REF);
        this.titleImageUrl  = bundle.getString(TITLEIMAGEURL_REF);
        this.location       = new LatLng(bundle.getDouble(LOCATION_LAT_REF),bundle.getDouble(LOCATION_LNG_REF));
        this.startTime      = bundle.getLong(STARTTIME_REF);
        this.stopTime       = bundle.getLong(STOPTIME_REF);
        this.repeat         = RepeatType.values()[bundle.getInt(REPEAT_REF)];
        this.closed         = bundle.getBoolean(CLOSED_REF);
    }

    public Post(UUID uuid, PostType type, String title, String description, List<String> tags, int matches, int conversations, int requests, List<String> imageUrls, String titleImageUrl, LatLng location, long startTime, long stopTime, RepeatType repeat, boolean closed) {
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
        this.closed = closed;
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

        for(String tag : tagString.split(TAG_SEPARATOR)){
            tag = tag.trim();
            if(tag.length()>0) {
                tags.add(tag);
            }
        }

        this.tags = tags;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getTagsAsString() {
        StringBuilder sb = new StringBuilder();

        if(tags!=null && tags.size() > 0) {
            for (String tag : tags) {
                sb.append(tag).append(TAG_SEPARATOR).append(" ");
            }

            return sb.substring(0, sb.length()-(TAG_SEPARATOR.length()+1)); //return without last Separator and space
        }else{
            return "";
        }
    }

    public String getFormattedDate(){
        //TODO: Implement this accordingly
        switch(repeat){
            case NONE:
            default:
                DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT);
                return df.format(new Date(startTime)) + " - "+df.format(new Date(stopTime));
            case WEEKLY:
                return "every tuesday";
            case MONTHLY:
                return "every 3rd Monday";
        }
    }

    public boolean hasNotifications(){
        return matches != 0 || conversations != 0 || requests != 0;
    }

    public void removeImage(String url){
        url = url.trim();

        if(titleImageUrl!= null && titleImageUrl.equals(url)){
            titleImageUrl = "";
        }
        ArrayList<String> newImages = new ArrayList<String>();

        for(String imgUrl : imageUrls){
            if(!imgUrl.equals(url)){
                newImages.add(imgUrl);
            }
        }
    }

    public void addImage(String imgUrl){
        if(this.getTitleImageUrl() == null || "".equals(this.getTitleImageUrl().trim())){
            this.setTitleImageUrl(imgUrl);
        }else{
            this.getImageUrls().add(imgUrl);
        }
    }

    public void removeLastAddedImage() {
        //TODO; Implement this better
        if(imageUrls == null && imageUrls.size()==0){
            if(titleImageUrl != null && !"".equals(titleImageUrl.trim())){
                titleImageUrl = "";
            }
        }else{
            imageUrls.remove(imageUrls.size()-1);
        }
    }

    @Override
    public String toString() {
        return "Post{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + getTagsAsString() +
                ", matches=" + matches +
                ", conversations=" + conversations +
                ", requests=" + requests +
                ", imageUrls=" + imageUrls +
                ", titleImageUrl='" + titleImageUrl + '\'' +
                ", location=" + location +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", closed=" + closed +
                ", repeat=" + repeat +
                '}';
    }

    public boolean contains(String filterSeq){
        //TODO: Filter for everything inside this post
        return this.getTitle().toLowerCase().contains(filterSeq) || this.getDescription().toLowerCase().contains(filterSeq) || getTagsAsString().toLowerCase().contains(filterSeq);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(UUID_REF, getUuid().toString());
        bundle.putInt(TYPE_REF, type.ordinal());
        bundle.putInt(REPEAT_REF, repeat.ordinal());
        bundle.putString(TITLE_REF, title);
        bundle.putStringArrayList(TAGS_REF, new ArrayList<String>(tags));
        bundle.putString(DESC_REF, description);
        bundle.putInt(MATCHES_REF, matches);
        bundle.putInt(CONVERSATIONS_REF, conversations);
        bundle.putInt(REQUESTS_REF, requests);
        bundle.putStringArrayList(IMAGEURLS_REF, new ArrayList<String>(imageUrls));
        bundle.putString(TITLEIMAGEURL_REF, titleImageUrl);
        bundle.putDouble(LOCATION_LAT_REF,location.latitude);
        bundle.putDouble(LOCATION_LNG_REF, location.longitude);
        bundle.putLong(STARTTIME_REF,startTime);
        bundle.putLong(STOPTIME_REF, stopTime);

        dest.writeBundle(bundle);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
