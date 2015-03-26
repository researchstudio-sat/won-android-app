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
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.impl.DataService;
import com.google.android.gms.maps.model.LatLng;
import org.apache.commons.lang3.time.StopWatch;
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

    public ArrayList<Connection> getConversations() {
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

        return conversations;
    }

    public ArrayList<Connection> getRequestsByPostId(String postId){
        return getRequestsByPostId(URI.create(postId));
    }

    public ArrayList<Connection> getRequestsByPostId(URI postId){
        List<Connection> connections = dataService.getConnectionsByPostAndState(postId, Collections.singletonList(ConnectionState.REQUEST_RECEIVED.getURI()));

        ArrayList<Connection> requests = new ArrayList<Connection>();

        for(Connection con : connections){
            if(con.getMatchedPost()!= null && con.getMatchedPost().getNeedState() == NeedState.ACTIVE){
                con.setMessages(getMessagesByConversationId(con.getURI()));
                requests.add(con);
            }
        }
        Log.d(LOG_TAG, "Getting Requests by postid: "+postId+" ("+connections.size()+" Connections / "+requests.size()+" Requests)");

        return requests;
    }

    public ArrayList<Post> getMyPosts() {
        Log.d(LOG_TAG, "Retrieve My Posts");
        Map<URI, Post> myPosts = dataService.getMyPosts();

        return new ArrayList<Post>(myPosts.values());
    }

    public ArrayList<Connection> getConversationsByPostId(String postId){
        return getConversationsByPostId(URI.create(postId));
    }

    public ArrayList<Connection> getConversationsByPostId(URI postId) {
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

        return conversations;
    }

    public ArrayList<MessageItemModel> getMessagesByConversationId(String conversationId){
        return getMessagesByConversationId(URI.create(conversationId));
    }

    public ArrayList<MessageItemModel> getMessagesByConversationId(URI conversationId){
        return dataService.getMessagesByConnectionId(conversationId);
    }

    public ArrayList<Post> getMatchesByPostId(String postId) {
        return getMatchesByPostId(URI.create(postId));
    }

    public ArrayList<Post> getMatchesByPostId(URI postId) {
        List<Connection> connections = dataService.getConnectionsByPostAndState(postId, Collections.singletonList(ConnectionState.SUGGESTED.getURI()));

        ArrayList<Post> matches = new ArrayList<Post>();

        for(Connection con : connections){
            if(con.getMatchedPost()!= null && con.getMatchedPost().getNeedState() == NeedState.ACTIVE){
                matches.add(con.getMatchedPost());
            }
        }
        Log.d(LOG_TAG, "Getting Matches by postid: "+postId+" ("+connections.size()+" Connections / "+matches.size()+" Matches)");
        return matches;
    }

    public Connection getConversationById(String id){
        return getConversationById(URI.create(id));
    }

    public Connection getConversationById(URI id){
        return dataService.getConnectionById(id);
    }

    public Post getMyPostById(String id) {
        return getMyPostById(URI.create(id));
    }

    public Post getMyPostById(URI id) {
        return dataService.getPostById(id);
    }

    public Post getMatchById(String id){
        return getMatchById(URI.create(id));
    }

    public Post getMatchById(URI id){
        return dataService.getPostById(id);
    }

    public Post savePost(Post newPost){
        Mock.myMockPosts.put(newPost.getURI(),newPost);
        //TODO: SAVE THIS POST N STUFF REFACTOR THIS IT IS ONLY MOCKED NOW
        return newPost; //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post closePost(String postIdString){
        return closePost(URI.create(postIdString));
    }

    public Post closePost(URI postId){
        Post post = Mock.myMockPosts.get(postId);
        post.setNeedState(NeedState.INACTIVE);
        //TODO: SAVE THIS POST N STUFF REFACTOR THIS IT IS ONLY MOCKED NOW
        return post; //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post reOpenPost(URI postId){
        Post post = Mock.myMockPosts.get(postId);
        post.setNeedState(NeedState.ACTIVE);
        //TODO: SAVE THIS POST N STUFF REFACTOR THIS IT IS ONLY MOCKED NOW
        return post; //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post createDraft(URI postId, URI newId) {
        Post oldPost = dataService.getPostById(postId);
        Post newPost = new Post(newId);

        //TODO: IMPL THIS WHOLE THING CORRECTLY (WITH A BUILDER OR SOMETHING)
        newPost.setTitle(oldPost.getTitle());
        newPost.setTags(new ArrayList<String>(oldPost.getTags()));
        newPost.setDescription(oldPost.getDescription());
        newPost.setImageUrls(new ArrayList<String>(oldPost.getImageUrls()));
        newPost.setType(oldPost.getType());
        newPost.setRepeat(oldPost.getRepeat());
        newPost.setStartTime(oldPost.getStartTime());
        newPost.setStopTime(oldPost.getStopTime());
        newPost.setLocation(new LatLng(oldPost.getLocation().latitude,oldPost.getLocation().longitude));

        return newPost;
    }
}
