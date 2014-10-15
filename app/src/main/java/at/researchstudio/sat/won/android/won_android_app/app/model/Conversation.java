package at.researchstudio.sat.won.android.won_android_app.app.model;

import at.researchstudio.sat.won.android.won_android_app.app.enums.MessageType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;

import java.util.List;
import java.util.UUID;

/**
 * Created by fsuda on 14.10.2014.
 */
public class Conversation extends Model {
    public static final String ID_REF = "conversation_id_ref";

    private Post myPost;
    private Post matchedPost;

    private List<MessageItemModel> messages;

    public Conversation(UUID uuid) {
        super(uuid);
    }

    public Conversation() {
        super(null);
    }

    public Conversation(Post myPost, Post matchedPost, List<MessageItemModel> messages){
        super(null);
        this.myPost = myPost;
        this.matchedPost = matchedPost;
        this.messages = messages;
    }

    public Post getMyPost() {
        return myPost;
    }

    public void setMyPost(Post myPost) {
        this.myPost = myPost;
    }

    public Post getMatchedPost() {
        return matchedPost;
    }

    public void setMatchedPost(Post matchedPost) {
        this.matchedPost = matchedPost;
    }

    public List<MessageItemModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageItemModel> messages) {
        this.messages = messages;
    }

    public String getTitle(){
        return (matchedPost==null) ? "Anonymous Conversation" : matchedPost.getTitle();
    }

    public String getReferenceTitle(){
        return (myPost == null) ? "Anonymous Conversation" : myPost.getTitle();
    }

    public PostType getType() {
        return (matchedPost == null) ? PostType.CHANGE : matchedPost.getType(); //TODO: SET DEFAULT VALUE IF NO POST
    }

    public String getTitleImageUrl() {
        return (matchedPost == null) ? null : matchedPost.getTitleImageUrl();
    }

    public PostType getReferenceType() {
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

}
