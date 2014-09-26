package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.model.NeedListItemModel;

/**
 * Created by fsuda on 24.09.2014.
 */
public class NeedListItemAdapter extends ArrayAdapter {
    private Context mContext;

    public NeedListItemAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    public void addItem(String title) {
        addItem(new NeedListItemModel(title));
    }

    public void addItem(NeedListItemModel needListItem) {
        add(needListItem);
    }

    public static class ViewHolder {
        public final TextView titleHolder;
        public final TextView matchesHolder;
        public final ImageView imageHolder;
        public final TextView tagHolder;

        public ViewHolder(TextView text1, TextView matchesHolder, ImageView imageHolder, TextView tagHolder) {
            this.titleHolder = text1;
            this.imageHolder = imageHolder;
            this.matchesHolder = matchesHolder;
            this.tagHolder = tagHolder;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        NeedListItemModel item = (NeedListItemModel) getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        if(view == null) {
            int layout = R.layout.need_list_item;

            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView title = (TextView) view.findViewById(R.id.need_list_title);
            TextView tag = (TextView) view.findViewById(R.id.need_list_tags);
            TextView matches = (TextView) view.findViewById(R.id.need_list_matches);
            ImageView image = (ImageView) view.findViewById(R.id.need_list_image);

            view.setTag(new ViewHolder(title,matches,image,tag));
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

            if (holder.tagHolder != null){
                if (item.tags != null && item.tags.size() > 0){
                    holder.tagHolder.setVisibility(View.VISIBLE);
                    holder.tagHolder.setText(item.getTagsAsString());
                }else{
                    holder.tagHolder.setVisibility(View.GONE);
                }
            }

            if (holder.matchesHolder != null){
                if (item.matches > 0){
                    holder.matchesHolder.setVisibility(View.VISIBLE);
                    holder.matchesHolder.setText(""+item.matches);
                }else{
                    holder.matchesHolder.setVisibility(View.GONE);
                }
            }

            if (holder.imageHolder != null){
                if (item.imageRes == 0){
                    holder.imageHolder.setImageResource(R.drawable.image_placeholder_donotcommit);
                }else {
                    holder.imageHolder.setImageResource(item.imageRes);
                }
            }
        }

        //TODO: Implement this view correctly
        return view;
    }
}
