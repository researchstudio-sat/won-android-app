package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostListItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.service.ImageLoaderService;

/**
 * Created by fsuda on 24.09.2014.
 */
public class PostListItemAdapter extends ArrayAdapter {
    private static final String LOG_TAG = PostListItemAdapter.class.getSimpleName();
    private ImageLoaderService mImgLoader;

    public PostListItemAdapter(Context context) {
        super(context, 0);
        mImgLoader = new ImageLoaderService(context);
    }

    public void addItem(PostListItemModel needListItem) {
        add(needListItem);
    }

    public static class ViewHolder {
        public final TextView titleHolder;
        public final TextView descriptionHolder;
        public final TextView matchesHolder;
        public final TextView conversationsHolder;
        public final TextView requestHolder;
        public final ImageView imageHolder;
        public final ImageView typeHolder;
        public final TextView tagHolder;
        public final TableLayout notificationTable;

        public ViewHolder(TextView text1, TextView descriptionHolder, TextView matchesHolder, TextView requestHolder, TextView conversationsHolder, ImageView imageHolder, TextView tagHolder, ImageView typeHolder, TableLayout notificationTable) {
            this.titleHolder = text1;
            this.descriptionHolder = descriptionHolder;
            this.imageHolder = imageHolder;
            this.matchesHolder = matchesHolder;
            this.tagHolder = tagHolder;
            this.conversationsHolder = conversationsHolder;
            this.requestHolder = requestHolder;
            this.typeHolder = typeHolder;
            this.notificationTable = notificationTable;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        PostListItemModel item = (PostListItemModel) getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        if(view == null) {
            int layout = R.layout.post_list_item;

            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView title = (TextView) view.findViewById(R.id.need_list_title);
            TextView description = (TextView) view.findViewById(R.id.need_list_description);
            TextView tag = (TextView) view.findViewById(R.id.need_list_tags);
            TextView matches = (TextView) view.findViewById(R.id.need_list_matches);
            TextView requests = (TextView) view.findViewById(R.id.need_list_requests);
            TextView conversations = (TextView) view.findViewById(R.id.need_list_conversations);
            ImageView image = (ImageView) view.findViewById(R.id.need_list_image);
            ImageView type = (ImageView) view.findViewById(R.id.need_list_type);
            TableLayout notificationTable = (TableLayout) view.findViewById(R.id.postlist_item_notifications);
            view.setTag(new ViewHolder(title,description,matches,requests,conversations,image,tag,type,notificationTable));
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

            if (holder.descriptionHolder != null)
                holder.descriptionHolder.setText(item.description);

            if (holder.tagHolder != null){
                if (item.tags != null && item.tags.size() > 0){
                    holder.tagHolder.setVisibility(View.VISIBLE);
                    holder.tagHolder.setText(item.getTagsAsString());

                    holder.descriptionHolder.setMaxLines(1);
                }else{
                    holder.tagHolder.setVisibility(View.GONE);
                    holder.descriptionHolder.setMaxLines(3);
                }
            }

            if(item.hasNotifications()) {
                setCountersVisible(holder);
                setCounter(holder.matchesHolder, item.matches, 9);
                setCounter(holder.requestHolder, item.requests, 9);
                setCounter(holder.conversationsHolder, item.conversations, 9);
            }else{
                setCountersInvisible(holder);
            }

            if (holder.imageHolder != null){
                if (item.imageUrl == null){
                    //TODO: Implement this for GMAIL APP STYLE DEFAULT ICONS: http://stackoverflow.com/questions/23122088/colored-boxed-with-letters-a-la-gmail
                    holder.imageHolder.setImageResource(R.drawable.image_placeholder_donotcommit);
                }else {
                    mImgLoader.displayImage(item.imageUrl, R.drawable.image_placeholder_donotcommit, holder.imageHolder);
                }
            }

            if (holder.typeHolder != null) {
                switch(item.type){
                    case OFFER:
                        holder.typeHolder.setImageResource(R.drawable.offer);
                        break;
                    case WANT:
                        holder.typeHolder.setImageResource(R.drawable.want);
                        break;
                    case ACTIVITY:
                        holder.typeHolder.setImageResource(R.drawable.activity);
                        break;
                    case CHANGE:
                        holder.typeHolder.setImageResource(R.drawable.change);
                        break;
                }
            }
        }
        return view;
    }

    private void setCounter(TextView counter, int value, int maxVisibleValue){
        if (counter != null){
            if(value > maxVisibleValue){
                counter.setAlpha(1);
                //counter.setVisibility(View.VISIBLE);
                counter.setText(maxVisibleValue+"+");
            }else if (value > 0){
                counter.setAlpha(1);
                //counter.setVisibility(View.VISIBLE);
                counter.setText(""+value);
            }else{
                counter.setAlpha(0.2f);
                //counter.setVisibility(View.INVISIBLE); //HINT: View.GONE 'removes' the item from the view View.INVISIBLE only sets it invisible
                counter.setText("");
            }
        }
    }

    private void setCountersVisible(ViewHolder holder){
        holder.notificationTable.setVisibility(View.VISIBLE);
    }

    private void setCountersInvisible(ViewHolder holder){
        holder.notificationTable.setVisibility(View.GONE);
    }
}
