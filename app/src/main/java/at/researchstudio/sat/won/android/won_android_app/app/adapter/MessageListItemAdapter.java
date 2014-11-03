package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsuda on 13.10.2014.
 */
public class MessageListItemAdapter extends ArrayAdapter {
    private List<MessageItemModel> objects;

    private Filter filter;

    public MessageListItemAdapter(Context context){
        super(context, 0);
        objects = new ArrayList<MessageItemModel>();
    }

    public void addItem(MessageItemModel messageItem){
        add(messageItem);
        objects.add(messageItem);
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

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new MessageListFilter<MessageItemModel>(objects);
        }
        return filter;
    }

    private class MessageListFilter<T> extends Filter {
        private ArrayList<T> sourceObjects;

        public MessageListFilter(List<T> objects) {
            sourceObjects = new ArrayList<T>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterSeq = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq != null && filterSeq.length() > 0) {
                ArrayList<T> filter = new ArrayList<T>();

                for (T object : sourceObjects) {
                    if(((MessageItemModel)object).contains(filterSeq)) {
                        filter.add(object);
                    }
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++) {
                add(filtered.get(i));
            }
            notifyDataSetInvalidated();
        }
    }
}
