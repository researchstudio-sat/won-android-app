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

package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.components.LetterTileProvider;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.service.ImageLoaderService;
import won.protocol.model.NeedState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsuda on 24.09.2014.
 */
public class PostListItemAdapter extends ArrayAdapter {
    private static final String LOG_TAG = PostListItemAdapter.class.getSimpleName();
    private ImageLoaderService mImgLoader;
    private List<Post> objects;
    private Context context;

    private Filter filter;

    public PostListItemAdapter(Context context) {
        super(context, 0);
        this.context = context;
        mImgLoader = new ImageLoaderService(context);
        this.objects = new ArrayList<Post>();
    }

    public void addItem(Post postListItem) {
        add(postListItem);
        objects.add(postListItem);
    }

    public void swapItem(Post postListItem, int position) {
        super.remove(postListItem);
        super.insert(postListItem, position);
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
        public final RelativeLayout closedHolder;

        public ViewHolder(TextView text1, TextView descriptionHolder, TextView matchesHolder, TextView requestHolder, TextView conversationsHolder, ImageView imageHolder, TextView tagHolder, ImageView typeHolder, TableLayout notificationTable, RelativeLayout closedHolder) {
            this.titleHolder = text1;
            this.descriptionHolder = descriptionHolder;
            this.imageHolder = imageHolder;
            this.matchesHolder = matchesHolder;
            this.tagHolder = tagHolder;
            this.conversationsHolder = conversationsHolder;
            this.requestHolder = requestHolder;
            this.typeHolder = typeHolder;
            this.notificationTable = notificationTable;
            this.closedHolder = closedHolder;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Post item = (Post) getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        if(view == null) {
            int layout = R.layout.item_post_list;

            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView title = (TextView) view.findViewById(R.id.item_post_list_title);
            TextView description = (TextView) view.findViewById(R.id.item_post_list_description);
            TextView tag = (TextView) view.findViewById(R.id.item_post_list_tags);
            TextView matches = (TextView) view.findViewById(R.id.item_post_list_matches);
            TextView requests = (TextView) view.findViewById(R.id.item_post_list_requests);
            TextView conversations = (TextView) view.findViewById(R.id.item_post_list_conversations);
            ImageView image = (ImageView) view.findViewById(R.id.item_post_list_image);
            ImageView type = (ImageView) view.findViewById(R.id.item_post_list_type);
            TableLayout notificationTable = (TableLayout) view.findViewById(R.id.postlist_item_notifications);
            RelativeLayout closedHolder = (RelativeLayout) view.findViewById(R.id.item_post_list_closed);
            view.setTag(new ViewHolder(title,description,matches,requests,conversations,image,tag,type,notificationTable,closedHolder));
        }

        if (holder == null && view != null) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }


        if(item != null && holder != null) {
            if (holder.titleHolder != null) {
                holder.titleHolder.setText(item.getTitle());
            }

            if(holder.closedHolder != null){
                holder.closedHolder.setVisibility(item.getNeedState() == NeedState.INACTIVE ? View.VISIBLE : View.GONE);
            }

            if (holder.descriptionHolder != null)
                holder.descriptionHolder.setText(item.getDescription());

            if (holder.tagHolder != null){
                if (item.getTags() != null && item.getTags().size() > 0){
                    holder.tagHolder.setVisibility(View.VISIBLE);
                    holder.tagHolder.setText(item.getTagsAsString());

                    holder.descriptionHolder.setMaxLines(1);
                }else{
                    holder.tagHolder.setVisibility(View.GONE);
                    holder.descriptionHolder.setMaxLines(3);
                }
            }

            if(item.getNeedState() == NeedState.ACTIVE && item.hasNotifications()) {
                setCountersVisible(holder);
                setCounter(holder.matchesHolder, item.getMatches(), 9);
                setCounter(holder.requestHolder, item.getRequests(), 9);
                setCounter(holder.conversationsHolder, item.getConversations(), 9);
            }else{
                setCountersInvisible(holder);
            }

            if (holder.imageHolder != null){
                if (item.getTitleImageUrl() == null){
                    final int tileSize = context.getResources().getDimensionPixelSize(R.dimen.letter_tile_size);
                    final LetterTileProvider tileProvider = new LetterTileProvider(context);
                    final Bitmap letterTile = tileProvider.getLetterTile(item.getTitle(), item.getTitle(), tileSize, tileSize);

                    holder.imageHolder.setImageBitmap(letterTile);
                }else {
                    mImgLoader.displayImage(item.getTitleImageUrl(), R.drawable.image_placeholder_donotcommit, holder.imageHolder);
                }
            }

            if (holder.typeHolder != null) {
                switch(item.getType()){
                    case SUPPLY:
                        holder.typeHolder.setImageResource(R.drawable.offer);
                        break;
                    case DEMAND:
                        holder.typeHolder.setImageResource(R.drawable.want);
                        break;
                    case DO_TOGETHER:
                        holder.typeHolder.setImageResource(R.drawable.activity);
                        break;
                    case CRITIQUE:
                        holder.typeHolder.setImageResource(R.drawable.change);
                        break;
                }
            }
        }
        return view;
    }

    private static void setCounter(TextView counter, int value, int maxVisibleValue){
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

    private static void setCountersVisible(ViewHolder holder){
        holder.notificationTable.setVisibility(View.VISIBLE);
    }

    private static void setCountersInvisible(ViewHolder holder){
        holder.notificationTable.setVisibility(View.GONE);
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new PostListFilter<Post>(objects);
        }
        return filter;
    }

    private class PostListFilter<T> extends Filter {
        private ArrayList<T> sourceObjects;

        public PostListFilter(List<T> objects) {
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
                    if(((Post)object).contains(filterSeq)) {
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
