package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;

/**
 * Created by fsuda on 13.10.2014.
 */
public class MessageListItemAdapter extends ArrayAdapter {
    public MessageListItemAdapter(Context context){
        super(context, 0);
    }

    public void addItem(MessageItemModel messageItem){
        add(messageItem);
    }

    private static class ViewHolder {
        public final TextView titleHolder;

        private ViewHolder(TextView titleHolder) {
            this.titleHolder = titleHolder;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageItemModel item = (MessageItemModel) getItem(position);
        ViewHolder holder = null;
        View view;


        int layout=0;

        switch(item.type){
            case SEND:
                layout = R.layout.item_message_send;
                break;
            case RECEIVE:
                layout = R.layout.item_message_receive;
                break;
            case SYSTEM:
                layout = R.layout.item_message_system;
                break;
        }

        view = LayoutInflater.from(getContext()).inflate(layout, null);

        TextView title = (TextView) view.findViewById(R.id.item_message_text);

        view.setTag(new ViewHolder(title));


        if (holder == null && view != null) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }

        if(item != null && holder != null) {
            if (holder.titleHolder != null)
                holder.titleHolder.setText(item.text);
        }

        return view;
    }
}
