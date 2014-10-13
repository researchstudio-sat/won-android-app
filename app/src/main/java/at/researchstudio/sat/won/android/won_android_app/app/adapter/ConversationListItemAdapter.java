package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.model.ConversationListItemModel;

/**
 * Created by fsuda on 13.10.2014.
 */
public class ConversationListItemAdapter extends ArrayAdapter {
    public ConversationListItemAdapter(Context context){
        super(context, 0);
    }

    public void addItem(ConversationListItemModel conversationListItem){
        add(conversationListItem);
    }

    private static class ViewHolder {
        public final TextView titleHolder;

        private ViewHolder(TextView titleHolder) {
            this.titleHolder = titleHolder;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConversationListItemModel item = (ConversationListItemModel) getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        if(view == null){
            int layout = R.layout.conversation_list_item;

            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView title = (TextView) view.findViewById(R.id.conversation_list_item_title);

            view.setTag(new ViewHolder(title));
        }

        if (holder == null && view != null) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }

        if(item != null && holder != null) {
            if (holder.titleHolder != null)
                holder.titleHolder.setText(item.title);
        }

        return view;
    }
}
