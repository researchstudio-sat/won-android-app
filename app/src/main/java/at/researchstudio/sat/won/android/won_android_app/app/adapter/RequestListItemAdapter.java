package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.model.RequestListItemModel;

/**
 * Created by fsuda on 13.10.2014.
 */
public class RequestListItemAdapter extends ArrayAdapter {
    public RequestListItemAdapter(Context context){
        super(context, 0);
    }

    public void addItem(RequestListItemModel requestListItem){
        add(requestListItem);
    }

    private static class ViewHolder {
        public final TextView titleHolder;

        private ViewHolder(TextView titleHolder) {
            this.titleHolder = titleHolder;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RequestListItemModel item = (RequestListItemModel) getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        if(view == null){
            int layout = R.layout.item_request_list;

            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView title = (TextView) view.findViewById(R.id.item_request_list_title);

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
