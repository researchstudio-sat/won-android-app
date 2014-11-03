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

import at.researchstudio.sat.won.android.won_android_app.app.enums.ConnectionType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.MessageType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by fsuda on 14.10.2014.
 */
public class Connection extends Model {
    public static final String ID_REF = "connection_id_ref";
    public static final String TYPE_RECEIVED_ONLY_REF = "connection_received_only";

    private Post myPost;
    private Post matchedPost;

    private ConnectionType type;

    private ArrayList<MessageItemModel> messages;

    public Connection(UUID uuid) {
        super(uuid);
    }

    public Connection() {
        super(null);
    }

    public Connection(Post myPost, Post matchedPost, ArrayList<MessageItemModel> messages, ConnectionType type){
        super(null);
        this.myPost = myPost;
        this.matchedPost = matchedPost;
        this.messages = messages;
        this.type = type;
    }

    public Post getMyPost() {
        return myPost;
    }

    public void setMyPost(Post myPost) {
        this.myPost = myPost;
    }

    public void setType(ConnectionType type) {
        this.type = type;
    }

    public ConnectionType getType() {
        return type;
    }

    public Post getMatchedPost() {
        return matchedPost;
    }

    public void setMatchedPost(Post matchedPost) {
        this.matchedPost = matchedPost;
    }

    public ArrayList<MessageItemModel> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<MessageItemModel> messages) {
        this.messages = messages;
    }

    public String getTitle(){
        return (matchedPost==null) ? "Anonymous Conversation" : matchedPost.getTitle();
    }

    public String getReferenceTitle(){
        return (myPost == null) ? "Anonymous Conversation" : myPost.getTitle();
    }

    public PostType getPostType() {
        return (matchedPost == null) ? PostType.CHANGE : matchedPost.getType(); //TODO: SET DEFAULT VALUE IF NO POST
    }

    public String getTitleImageUrl() {
        return (matchedPost == null) ? null : matchedPost.getTitleImageUrl();
    }

    public PostType getReferencePostType() {
        return (myPost == null) ? PostType.CHANGE : myPost.getType(); //TODO: SET DEFAULT VALUE IF NO POST
    }

    public int getMessageCount() {
        return messages == null ? 0 : messages.size();
    }

    public MessageItemModel getLastUserMessage() {

        if (messages != null && messages.size() > 0) {
            for(int i = messages.size()-1; i >= 0; i--){
                MessageItemModel message = messages.get(i);

                if(message.type != MessageType.SYSTEM) {
                    return message;
                }
            }
        }
        return null; //TODO: WHAT TO DO WHEN NO MESSAGE AVAILABLE?
    }

    public MessageType getLastUserMessageType() {
        return getLastUserMessage().type;
    }

    public String getLastUserMessageString() {
        return getLastUserMessage().text;
    }

    public boolean contains(String filterSeq){
        //TODO: Filter for everything inside this connection
        return this.getTitle().toLowerCase().contains(filterSeq) || this.getReferenceTitle().toLowerCase().contains(filterSeq) || (getLastUserMessage() != null && getLastUserMessage().text.toLowerCase().contains(filterSeq));
    }
}
