package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.model.TypeSpinnerModel;

/**
 * Created by fsuda on 26.09.2014.
 */
public class TypeSpinnerAdapter extends ArrayAdapter {
    Context mContext;

    public TypeSpinnerAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    public void addItem(TypeSpinnerModel spinnerModel) {
        add(spinnerModel);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    public static class ViewHolder {
        public final TextView textHolder;
        public final ImageView imageHolder;

        public ViewHolder(TextView text1, ImageView image1) {
            this.textHolder = text1;
            this.imageHolder = image1;
        }
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        TypeSpinnerModel item = (TypeSpinnerModel) getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        if (view == null) {
            int layout = R.layout.type_spinner_item;

            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView text1 = (TextView) view.findViewById(R.id.spinner_type_text);
            ImageView image1 = (ImageView) view.findViewById(R.id.spinner_type_img);
            view.setTag(new ViewHolder(text1, image1));
        }

        if (holder == null && view != null) {
            Object tag = view.getTag();
            if (tag instanceof ViewHolder) {
                holder = (ViewHolder) tag;
            }
        }

        if(item != null && holder != null)
        {
            if (holder.textHolder != null)
                holder.textHolder.setText(item.getTitleRes());

            if (holder.imageHolder != null) {
                if (item.getIconRes() > 0) {
                    holder.imageHolder.setVisibility(View.VISIBLE);
                    holder.imageHolder.setImageResource(item.getIconRes());
                } else {
                    holder.imageHolder.setVisibility(View.GONE);
                }
            }
        }

        return view;
    }
}
