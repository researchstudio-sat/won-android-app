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
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.UUID;

public class PostService {
    private static final String LOG_TAG = PostService.class.getSimpleName();

    public PostService() {
        //TODO REFACTOR THIS AWAY FROM HERE THIS BLOCKS EVERYTHING ONLY HERE FOR VIEW TESTING PURPOSES
        //TODO: REFACTOR THIS MOCK METHOD
        //MOCK DATA RETRIEVAL
        //THIS IS ALSO THE CAUSE WHY THE CHOREOGRAPHER DROPS SOME FRAMES DURING INITIALIZING THE ACTIVITY AND AFTER RESUMING FROM CAMERA INTENT
        Mock.fillMyMockMatches();
        Mock.fillMyMockPosts();
        Mock.fillMyMockConnections();
        Mock.setNotificationCounters();

    }

    public ArrayList<Connection> getConversations() {
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getConversations();
    }

    public ArrayList<Connection> getRequestsByPostId(String postId){
        return getRequestsByPostId(UUID.fromString(postId));
    }

    public ArrayList<Connection> getRequestsByPostId(UUID postId){
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getRequestsByPostId(postId);
    }

    public ArrayList<Connection> getConversationsByPostId(String postId){
        return getConversationsByPostId(UUID.fromString(postId));
    }

    public ArrayList<Connection> getConversationsByPostId(UUID postId) {
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getConversationsByPostId(postId);
    }

    public ArrayList<MessageItemModel> getMessagesByConversationId(String conversationId){
        return getMessagesByConversationId(UUID.fromString(conversationId));
    }

    public ArrayList<MessageItemModel> getMessagesByConversationId(UUID conversationId){
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getMessagesByConversationId(conversationId);
    }

    public ArrayList<Post> getMatchesByPostId(String postId) {
        return getMatchesByPostId(UUID.fromString(postId));
    }

    public ArrayList<Post> getMatchesByPostId(UUID postId) {
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.getMatchesByPostId(postId);
    }

    public ArrayList<Post> getMyPosts(){
        //TODO: REFACTOR THIS MOCK METHOD
        return new ArrayList<Post>(Mock.myMockPosts.values());
    }

    public Connection getConversationById(String id){
        return getConversationById(UUID.fromString(id));
    }

    public Connection getConversationById(UUID id){
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.myMockConversations.get(id);
    }

    public Post getMyPostById(String id) {
        return getMyPostById(UUID.fromString(id));
    }

    public Post getMyPostById(UUID id) {
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.myMockPosts.get(id);
    }

    public Post getMatchById(String id){
        return getMatchById(UUID.fromString(id));
    }

    public Post getMatchById(UUID id){
        //TODO: REFACTOR THIS MOCK METHOD
        return Mock.myMockMatches.get(id);
    }

    public Post savePost(Post newPost){
        Mock.myMockPosts.put(newPost.getUuid(),newPost);
        //TODO: SAVE THIS POST N STUFF REFACTOR THIS IT IS ONLY MOCKED NOW
        return newPost; //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post closePost(String postIdString){
        return closePost(UUID.fromString(postIdString));
    }

    public Post closePost(UUID postId){
        Post post = Mock.myMockPosts.get(postId);
        post.setClosed(true);
        //TODO: SAVE THIS POST N STUFF REFACTOR THIS IT IS ONLY MOCKED NOW
        return post; //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post reOpenPost(UUID postId){
        Post post = Mock.myMockPosts.get(postId);
        post.setClosed(false);
        //TODO: SAVE THIS POST N STUFF REFACTOR THIS IT IS ONLY MOCKED NOW
        return post; //RETURN STMT SHOULD RETURN THE NEW POST SO WE KNOW WHICH ID IT ACTUALLY HAD
    }

    public Post createDraft(UUID postId) {
        Post oldPost = Mock.myMockPosts.get(postId);

        if(oldPost==null){
            oldPost = Mock.myMockMatches.get(postId);
        }
        Post newPost = new Post();

        //TODO: IMPL THIS WHOLE THING CORRECTLY
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
