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

import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.webservice.impl.DataService;
import com.google.android.gms.maps.model.LatLng;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostService {
    private static final String LOG_TAG = PostService.class.getSimpleName();
    private DataService dataService;

    public PostService(DataService dataService) {
        this.dataService = dataService;
        //TODO REFACTOR THIS AWAY FROM HERE THIS BLOCKS EVERYTHING ONLY HERE FOR VIEW TESTING PURPOSES
        //TODO: REFACTOR THIS MOCK METHOD
        //MOCK DATA RETRIEVAL
        //THIS IS ALSO THE CAUSE WHY THE CHOREOGRAPHER DROPS SOME FRAMES DURING INITIALIZING THE ACTIVITY AND AFTER RESUMING FROM CAMERA INTENT
        //Mock.fillMyMockMatches();
        //Mock.fillMyMockPosts();
        //Mock.fillMyMockConnections();
        //Mock.setNotificationCounters();
    }

    public ArrayList<Connection> getConversations() {
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getConversations();
    }

    public ArrayList<Connection> getRequestsByPostId(String postId){
        return getRequestsByPostId(URI.create(postId));
    }

    public ArrayList<Connection> getRequestsByPostId(URI postId){
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getRequestsByPostId(postId);
    }

    public ArrayList<Post> getMyPosts() {
        return dataService.getMyPosts();
    }

    public ArrayList<Connection> getConversationsByPostId(String postId){
        return getConversationsByPostId(URI.create(postId));
    }

    public ArrayList<Connection> getConversationsByPostId(URI postId) {
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getConversationsByPostId(postId);
    }

    public ArrayList<MessageItemModel> getMessagesByConversationId(String conversationId){
        return getMessagesByConversationId(URI.create(conversationId));
    }

    public ArrayList<MessageItemModel> getMessagesByConversationId(URI conversationId){
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getMessagesByConversationId(conversationId);
    }

    public ArrayList<Post> getMatchesByPostId(String postId) {
        return getMatchesByPostId(URI.create(postId));
    }

    public ArrayList<Post> getMatchesByPostId(URI postId) {
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getMatchesByPostId(postId);
    }

    public Connection getConversationById(String id){
        return getConversationById(URI.create(id));
    }

    public Connection getConversationById(URI id){
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.myMockConversations.get(id);
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
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.myMockMatches.get(id);
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
        post.setClosed(true);
        //TODO: SAVE THIS POST N STUFF REFACTOR THIS IT IS ONLY MOCKED NOW
        return post; //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post reOpenPost(URI postId){
        Post post = Mock.myMockPosts.get(postId);
        post.setClosed(false);
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
