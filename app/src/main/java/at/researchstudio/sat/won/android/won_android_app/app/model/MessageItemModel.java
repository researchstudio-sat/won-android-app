package at.researchstudio.sat.won.android.won_android_app.app.model;

/**
 * Created by fsuda on 14.10.2014.
 */
public class MessageItemModel {
    public MessageType type;
    public String text;

    public MessageItemModel(MessageType type, String text) {
        this.type = type;
        this.text = text;
    }
}
