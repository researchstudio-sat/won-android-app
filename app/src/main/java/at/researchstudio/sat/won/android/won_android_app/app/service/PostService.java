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

package at.researchstudio.sat.won.android.won_android_app.app.service;

import android.util.Log;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Constants;
import at.researchstudio.sat.won.android.won_android_app.app.event.*;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.impl.DataService;
import com.google.android.gms.maps.model.LatLng;
import de.greenrobot.event.EventBus;
import won.protocol.model.ConnectionState;
import won.protocol.model.NeedState;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PostService {
    private static final String LOG_TAG = PostService.class.getSimpleName();
    private DataService dataService;

    public PostService(DataService dataService) {
        this.dataService = dataService;
    }

    public void getConversations() {
        List<URI> states = new ArrayList<URI>();
        states.add(ConnectionState.REQUEST_SENT.getURI());
        states.add(ConnectionState.CONNECTED.getURI());
        List<Connection> connections = dataService.getConnectionsByState(states);

        ArrayList<Connection> conversations = new ArrayList<Connection>();

        for(Connection con : connections){
            if(con.getMatchedPost()!= null && con.getMatchedPost().getNeedState() == NeedState.ACTIVE){
                conversations.add(con);
            }
        }
        Log.d(LOG_TAG, "Getting Conversations : ("+connections.size()+" Connections / "+conversations.size()+" Conversations)");

        EventBus.getDefault().post(new ReceivedConversationsEvent(conversations));
    }

    public void getRequestsByPostId(String postId){
        getRequestsByPostId(URI.create(postId));
    }

    public void getRequestsByPostId(URI postId){
        List<Connection> connections = dataService.getConnectionsByPostAndState(postId, Collections.singletonList(ConnectionState.REQUEST_RECEIVED.getURI()));

        ArrayList<Connection> requests = new ArrayList<Connection>();

        for(Connection con : connections){
            if(con.getMatchedPost()!= null && con.getMatchedPost().getNeedState() == NeedState.ACTIVE){
                con.setMessages(getMessagesByConversationId(con.getURI()));
                requests.add(con);
            }
        }
        Log.d(LOG_TAG, "Getting Requests by postid: "+postId+" ("+connections.size()+" Connections / "+requests.size()+" Requests)");

        EventBus.getDefault().post(new ReceivedRequestsEvent(requests));
    }

    public void getMyPosts() {
        Log.d(LOG_TAG, "Retrieve My Posts");
        Map<URI, Post> myPosts = dataService.getMyPosts();
        Log.d(LOG_TAG, "RETRIEVED POSTS SENDING EVENT CALL");
        EventBus.getDefault().post(new ReceivedMyPostsEvent(new ArrayList<Post>(myPosts.values())));
    }

    public void getConversationsByPostId(String postId){
        getConversationsByPostId(URI.create(postId));
    }

    public void getConversationsByPostId(URI postId) {
        List<URI> states = new ArrayList<URI>();
        states.add(ConnectionState.REQUEST_SENT.getURI());
        states.add(ConnectionState.CONNECTED.getURI());
        List<Connection> connections = dataService.getConnectionsByPostAndState(postId, states);

        ArrayList<Connection> conversations = new ArrayList<Connection>();

        for(Connection con : connections){
            if(con.getMatchedPost()!= null && con.getMatchedPost().getNeedState() == NeedState.ACTIVE){
                con.setMessages(getMessagesByConversationId(con.getURI()));
                conversations.add(con);
            }
        }
        Log.d(LOG_TAG, "Getting Conversations by postid: "+postId+" ("+connections.size()+" Connections / "+conversations.size()+" Conversations)");

        EventBus.getDefault().post(new ReceivedConversationsEvent(conversations));
    }

    public ArrayList<MessageItemModel> getMessagesByConversationId(String conversationId){
        return getMessagesByConversationId(URI.create(conversationId));
    }

    public ArrayList<MessageItemModel> getMessagesByConversationId(URI conversationId){
        return dataService.getMessagesByConnectionId(conversationId);
    }

    public void getMatchesByPostId(String postId) {
        getMatchesByPostId(URI.create(postId));
    }

    public void getMatchesByPostId(URI postId) {
        List<Connection> connections = dataService.getConnectionsByPostAndState(postId, Collections.singletonList(ConnectionState.SUGGESTED.getURI()));

        ArrayList<Post> matches = new ArrayList<Post>();

        for(Connection con : connections){
            if(con.getMatchedPost()!= null && con.getMatchedPost().getNeedState() == NeedState.ACTIVE){
                matches.add(con.getMatchedPost());
            }
        }
        Log.d(LOG_TAG, "Getting Matches by postid: "+postId+" ("+connections.size()+" Connections / "+matches.size()+" Matches)");
        EventBus.getDefault().post(new ReceivedMatchesEvent(matches));
    }

    public void getConversationById(String id){
        getConversationById(URI.create(id));
    }

    public void getConversationById(URI id){
        Connection con = dataService.getConnectionById(id);
        con.setMessages(getMessagesByConversationId(id));

        EventBus.getDefault().post(new ReceivedConversationEvent(con));
    }

    public void getMyPostById(String id) { getMyPostById(URI.create(id)); }

    public void getMyPostById(URI id) {
        EventBus.getDefault().post(new ReceivedMyPostEvent(dataService.getMyPostById(id)));
    }

    public void getMatchById(String id){
        getMatchById(URI.create(id));
    }

    public void getMatchById(URI id){
        EventBus.getDefault().post(new ReceivedMatchEvent(dataService.getPostById(id)));
    }

    public void savePost(Post newPost) throws Exception{
        dataService.savePost(newPost);
        EventBus.getDefault().post(new SavePostEvent(newPost)); //TODO remove this
    }

    public Post closePost(String postIdString){
        return closePost(URI.create(postIdString));
    }

    public Post closePost(URI postId){
        return dataService.changePostState(postId, NeedState.INACTIVE); //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post reOpenPost(URI postId){
        return dataService.changePostState(postId, NeedState.ACTIVE); //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post createDraft(URI postId) {
        return createDraft(postId, Constants.TEMP_PLACEHOLDER_URI);
    }

    public Post createDraft(URI postId, URI newId) {
        Post oldPost = dataService.getPostById(postId);
        Post newPost = new Post(newId);

        //TODO: IMPL THIS WHOLE THING CORRECTLY (WITH A BUILDER OR SOMETHING)
        newPost.setTitle(oldPost.getTitle());
        newPost.setTags(new ArrayList<>(oldPost.getTags()));
        newPost.setDescription(oldPost.getDescription());
        newPost.setImageUrls(new ArrayList<>(oldPost.getImageUrls()));
        newPost.setType(oldPost.getType());
        newPost.setRepeat(oldPost.getRepeat());
        newPost.setStartTime(oldPost.getStartTime());
        newPost.setStopTime(oldPost.getStopTime());
        newPost.setLocation(new LatLng(oldPost.getLocation().latitude,oldPost.getLocation().longitude));

        return newPost;
    }
}
