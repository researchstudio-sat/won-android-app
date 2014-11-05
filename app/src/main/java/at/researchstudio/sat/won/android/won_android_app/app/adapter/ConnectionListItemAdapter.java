/*
 * Copyright 2014 Research Studios Austria Forschungsges.m.b.H.
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
import at.researchstudio.sat.won.android.won_android_app.app.enums.MessageType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.service.ImageLoaderService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsuda on 13.10.2014.
 */
public class ConnectionListItemAdapter extends ArrayAdapter {
    private ImageLoaderService mImgLoader;
    private boolean includeReference;
    private boolean receivedRequestsOnly;
    private List<Connection> objects;
    private Context context;
    private LetterTileProvider tileProvider;

    private Filter filter;

    public ConnectionListItemAdapter(Context context, boolean includeReference, boolean receivedRequestsOnly){
        super(context, 0);
        this.context = context;
        tileProvider = new LetterTileProvider(context);
        mImgLoader = new ImageLoaderService(context);
        this.includeReference = includeReference;
        this.receivedRequestsOnly = receivedRequestsOnly;
        this.objects = new ArrayList<Connection>();
    }

    public void addItem(Connection connection){
        add(connection);
        objects.add(connection);
    }

    private static class ViewHolder {
        public final TextView titleHolder;
        public final TextView counterHolder;
        public final ImageView typeHolder;
        public final TextView refTitleHolder;
        public final ImageView refTypeHolder;
        public final ImageView messageTypeHolder;
        public final ImageView imageHolder;
        public final TextView messageHolder;
        public final RelativeLayout refLayout;

        private ViewHolder(TextView titleHolder, TextView refTitleHolder, TextView counterHolder, ImageView typeHolder, ImageView refTypeHolder, ImageView messageTypeHolder, TextView messageHolder, ImageView imageHolder, RelativeLayout refLayout) {
            this.titleHolder = titleHolder;
            this.refTitleHolder = refTitleHolder;
            this.counterHolder = counterHolder;
            this.typeHolder = typeHolder;
            this.refTypeHolder = refTypeHolder;
            this.messageTypeHolder = messageTypeHolder;
            this.messageHolder = messageHolder;
            this.imageHolder = imageHolder;
            this.refLayout = refLayout;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Connection item = (Connection) getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        if(view == null){
            int layout = R.layout.item_conversation_list;

            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView title = (TextView) view.findViewById(R.id.item_conversation_list_title);
            TextView refTitle = (TextView) view.findViewById(R.id.item_conversation_list_reference_title);
            TextView counterHolder = (TextView) view.findViewById(R.id.item_conversation_list_message_counter);
            ImageView typeHolder = (ImageView) view.findViewById(R.id.item_conversation_list_type);
            ImageView refTypeHolder = (ImageView) view.findViewById(R.id.item_conversation_list_reference_type);
            ImageView messageTypeHolder = (ImageView) view.findViewById(R.id.item_conversation_list_message_type);
            TextView messageHolder = (TextView) view.findViewById(R.id.item_conversation_list_message_text);
            ImageView imageHolder = (ImageView) view.findViewById(R.id.item_conversation_list_image);
            RelativeLayout referenceLayout = (RelativeLayout) view.findViewById(R.id.item_conversation_list_reference);

            view.setTag(new ViewHolder(title, refTitle, counterHolder, typeHolder, refTypeHolder, messageTypeHolder, messageHolder, imageHolder, referenceLayout));
        }

        if (holder == null && view != null) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }

        if(item != null && holder != null) {
            setText(holder.titleHolder, item.getTitle());
            setCounter(holder.counterHolder, receivedRequestsOnly? 0 : item.getMessageCount(), 99);
            setType(holder.typeHolder, item.getPostType());

            if(includeReference) {
                if(holder.messageHolder != null) {
                    holder.messageHolder.setMaxLines(1);
                }

                if(holder.refLayout != null) {
                    holder.refLayout.setVisibility(View.VISIBLE);
                }

                setText(holder.refTitleHolder,item.getReferenceTitle());
                setType(holder.refTypeHolder, item.getReferencePostType());
            }else{
                if(holder.refLayout != null) {
                    holder.refLayout.setVisibility(View.GONE);
                }
                if(holder.messageHolder != null) {
                    holder.messageHolder.setMaxLines(2);
                }
            }

            setMsgType(holder.messageTypeHolder, item.getLastUserMessageType());
            setText(holder.messageHolder,item.getLastUserMessageString());

            if (holder.imageHolder != null){
                if (item.getTitleImageUrl() == null){
                    final int tileSize = context.getResources().getDimensionPixelSize(R.dimen.letter_tile_size);
                    final Bitmap letterTile = tileProvider.getLetterTile(item.getTitle(), item.getTitle(), tileSize, tileSize);

                    holder.imageHolder.setImageBitmap(letterTile);
                }else {
                    mImgLoader.displayImage(item.getTitleImageUrl(), R.drawable.image_placeholder_donotcommit, holder.imageHolder);
                }
            }
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new ConnectionListFilter<Connection>(objects);
        }
        return filter;
    }

    private static void setCounter(TextView counter, int value, int maxVisibleValue){
        if (counter != null){
            if(value > maxVisibleValue){
                counter.setVisibility(View.VISIBLE);
                counter.setText(maxVisibleValue+"+");
            }else if (value > 0){
                counter.setVisibility(View.VISIBLE);
                counter.setText(""+value);
            }else{
                counter.setVisibility(View.GONE);
                counter.setText("");
            }
        }
    }

    private static void setType(ImageView holder, PostType type) {
        if(holder != null) {
            switch (type) {
                case OFFER:
                    holder.setImageResource(R.drawable.offer);
                    break;
                case WANT:
                    holder.setImageResource(R.drawable.want);
                    break;
                case ACTIVITY:
                    holder.setImageResource(R.drawable.activity);
                    break;
                case CHANGE:
                    holder.setImageResource(R.drawable.change);
                    break;
            }
        }
    }

    private static void setMsgType(ImageView holder, MessageType type) {
        if(holder != null) {
            switch (type) {
                case SEND:
                    holder.setImageResource(R.drawable.message_send);
                    break;
                case RECEIVE:
                    holder.setImageResource(R.drawable.message_receive);
                    break;
                case SYSTEM:
                    //DO NOTHING BECAUSE THAT CASE SHOULD NOT HAPPEN ANYWAY
                    break;
            }
        }
    }

    private static void setText(TextView holder, String text) {
        if(holder!=null) {
            holder.setText(text);
        }
    }

    private class ConnectionListFilter<T> extends Filter {
        private ArrayList<T> sourceObjects;

        public ConnectionListFilter(List<T> objects) {
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
                    if(((Connection)object).contains(filterSeq)) {
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
