/*
 * Copyright 2015 Research Studios Austria Forschungsges.m.b.H.
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
import at.researchstudio.sat.won.android.won_android_app.app.enums.RepeatType;
import com.google.android.gms.maps.model.LatLng;
import won.protocol.model.BasicNeedType;
import won.protocol.model.NeedState;
import won.protocol.service.impl.WonNodeInformationServiceImpl;

import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static final String TITLEIMAGEINDEX_REF = "post_titleimageindex_ref";
    public static final String LOCATION_LAT_REF = "post_lat_ref";
    public static final String LOCATION_LNG_REF = "post_lng_ref";
    public static final String STARTTIME_REF = "post_starttime_ref";
    public static final String STOPTIME_REF = "post_stoptime_ref";
    public static final String CLOSED_REF = "post_closed_ref";
    public static final String REPEAT_REF = "post_repeat_ref";

    public static final String TAG_SEPARATOR = ",";

    private BasicNeedType type;
    private String title;
    private String description;

    private List<String> tags;
    private int matches;
    private int conversations;
    private int requests;

    private List<String> imageUrls;
    private int titleImageIndex;

    private LatLng location;

    private long startTime;
    private long stopTime;

    private NeedState needState;

    private RepeatType repeat;

    public Post(BasicNeedType type, String title, String description, List<String> tags, int matches, int conversations, int requests, List<String> imageUrls, int titleImageIndex, LatLng location, long startTime, long stopTime, RepeatType repeat, NeedState needState) {
        this(null, type, title, description, tags, matches, conversations, requests, imageUrls, titleImageIndex, location, startTime, stopTime, repeat, needState);
    }

    public Post(URI uri){
        this.setURI(uri);
        this.type = BasicNeedType.DEMAND;
        this.needState = NeedState.ACTIVE;
        this.repeat = RepeatType.NONE;
        this.location = new LatLng(0,0);
        this.imageUrls = new ArrayList<String>();
        this.tags = new ArrayList<String>();
    }

    public Post(Parcel in) {
        Bundle bundle = in.readBundle();
        this.setURI(bundle.getString(URI_REF));

        this.type               = BasicNeedType.values()[bundle.getInt(TYPE_REF)];
        this.title              = bundle.getString(TITLE_REF);
        this.description        = bundle.getString(DESC_REF);
        this.tags               = bundle.getStringArrayList(TAGS_REF);
        this.matches            = bundle.getInt(MATCHES_REF);
        this.conversations      = bundle.getInt(CONVERSATIONS_REF);
        this.requests           = bundle.getInt(REQUESTS_REF);
        this.imageUrls          = bundle.getStringArrayList(IMAGEURLS_REF);
        this.titleImageIndex    = bundle.getInt(TITLEIMAGEINDEX_REF);
        this.location           = new LatLng(bundle.getDouble(LOCATION_LAT_REF),bundle.getDouble(LOCATION_LNG_REF));
        this.startTime          = bundle.getLong(STARTTIME_REF);
        this.stopTime           = bundle.getLong(STOPTIME_REF);
        this.repeat             = RepeatType.values()[bundle.getInt(REPEAT_REF)];
        this.needState          = NeedState.values()[bundle.getInt(CLOSED_REF)];
    }

    public Post(URI uri, BasicNeedType type, String title, String description, List<String> tags, int matches, int conversations, int requests, List<String> imageUrls, int titleImageIndex, LatLng location, long startTime, long stopTime, RepeatType repeat, NeedState needState) {
        if(uri==null){
            this.setURI(new WonNodeInformationServiceImpl().generateNeedURI()); //TODO: do not implement this like that
        }else{
            this.setURI(uri);
        }
        this.type = type;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.matches = matches;
        this.conversations = conversations;
        this.requests = requests;
        this.imageUrls = imageUrls;
        this.titleImageIndex = titleImageIndex;
        this.location = location;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.repeat = repeat;
        this.needState = needState;
    }

    public int getTitleImageIndex() {
        return titleImageIndex;
    }

    public void setTitleImageIndex(int titleImageIndex) {
        this.titleImageIndex = titleImageIndex;
    }

    public BasicNeedType getType() {
        return type;
    }

    public void setType(BasicNeedType type) {
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

    /*
    Returns every Image except the titleImage
     */
    public List<String> getOtherImageUrls() {
        List<String> newImageUrls = new ArrayList<String>();
        if(imageUrls!=null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                if (i != titleImageIndex) {
                    newImageUrls.add(imageUrls.get(i));
                }
            }
        }
        return newImageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getTitleImageUrl() {
        if(imageUrls != null && titleImageIndex < imageUrls.size()){
            return imageUrls.get(titleImageIndex);
        }else{
            return null;
        }
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

    public NeedState getNeedState() {
        return needState;
    }

    public void setNeedState(NeedState needState) {
        this.needState = needState;
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

        int deleteIndex = -1;

        for(int i=0; i<imageUrls.size(); i++){
            String imgUrl = imageUrls.get(i);

            if(url.equals(imgUrl)){
                deleteIndex = i;
                break;
            }
        }

        if(deleteIndex > 0){
            imageUrls.remove(deleteIndex);

            if(titleImageIndex == deleteIndex){
                titleImageIndex = --deleteIndex < 0 ? 0 : deleteIndex;
            }
        }
    }

    public void addImage(String imgUrl){
        imgUrl = imgUrl.trim();
        if(!"".equals(imgUrl)) {
            imageUrls.add(imgUrl);

            if (imageUrls.size() == 1) {
                titleImageIndex = 0;
            }
        }
    }

    public void removeLastAddedImage() {
        if(imageUrls.size()>0) {
            int lastIndex = imageUrls.size()-1;
            if(lastIndex != 0 && lastIndex==titleImageIndex){
                titleImageIndex--;
            }
            imageUrls.remove(imageUrls.size() - 1);
        }
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + getURI() +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", matches=" + matches +
                ", conversations=" + conversations +
                ", requests=" + requests +
                ", imageUrls=" + imageUrls +
                ", titleImageIndex=" + titleImageIndex +
                ", location=" + location +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", needState=" + needState +
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
        bundle.putString(URI_REF, getURI().toString());
        bundle.putInt(TYPE_REF, type.ordinal());
        bundle.putInt(CLOSED_REF, needState.ordinal());
        bundle.putInt(REPEAT_REF, repeat.ordinal());
        bundle.putString(TITLE_REF, title);
        bundle.putStringArrayList(TAGS_REF, new ArrayList<String>(tags));
        bundle.putString(DESC_REF, description);
        bundle.putInt(MATCHES_REF, matches);
        bundle.putInt(CONVERSATIONS_REF, conversations);
        bundle.putInt(REQUESTS_REF, requests);
        bundle.putStringArrayList(IMAGEURLS_REF, new ArrayList<String>(imageUrls));
        bundle.putInt(TITLEIMAGEINDEX_REF, titleImageIndex);
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
