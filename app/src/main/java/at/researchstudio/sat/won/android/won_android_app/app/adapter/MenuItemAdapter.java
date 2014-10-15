package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import at.researchstudio.sat.won.android.won_android_app.app.model.MenuItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.R;

/**
 * Created by RSA SAT on 22.08.2014.
 */
public class MenuItemAdapter extends ArrayAdapter{
    Context mContext;

    public MenuItemAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    public void addItem(int title, int icon) {
        add(new MenuItemModel(title, icon));
    }

    public void addItem(int title, int icon, int counter) {
        add(new MenuItemModel(title, icon, counter, false));
    }

    public void addItem(int title, int icon, int counter, boolean selected) {
        add(new MenuItemModel(title, icon, counter, selected));
    }

    public void addItem(int title, int icon, boolean selected) {
        add(new MenuItemModel(title, icon, selected));
    }
    public void addItem(MenuItemModel itemModel) {
        add(itemModel);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        //return getItem(position).isHeader ? 0 : 1;
        return 1;
    }

    @Override
    public boolean isEnabled(int position) {
        //return !getItem(position).isHeader;
        return true;
    }

    public static class ViewHolder {
        public final TextView textHolder;
        public final ImageView imageHolder;
        public final TextView textCounterHolder;

        public ViewHolder(TextView text1, ImageView image1,TextView textcounter1) {
            this.textHolder = text1;
            this.imageHolder = image1;
            this.textCounterHolder=textcounter1;
        }
    }


    /*Creates the MenuItems based on the item in the ArrayAdapter*/
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuItemModel item = (MenuItemModel) getItem(position);
        ViewHolder holder = null;
        View view = convertView;

        int menuItemFontColor = 0;
        int menuItemBackgroundColor = 0;

        if(item != null && item.isSelected()){
            //SET COLORS ACCORDINGLY
            menuItemFontColor = mContext.getResources().getColor(R.color.menuitem_text_sel);
            menuItemBackgroundColor = mContext.getResources().getColor(R.color.menuitem_bg_sel);
        }else{
            menuItemFontColor = mContext.getResources().getColor(R.color.menuitem_text);
            menuItemBackgroundColor = mContext.getResources().getColor(R.color.menuitem_bg);
        }

        if (view == null) {
            int layout = R.layout.item_menu;

            view = LayoutInflater.from(getContext()).inflate(layout, null);

            TextView text1 = (TextView) view.findViewById(R.id.menu_item_title);
            ImageView image1 = (ImageView) view.findViewById(R.id.menu_item_icon);
            TextView textcounter1 = (TextView) view.findViewById(R.id.menu_item_counter);
            view.setTag(new ViewHolder(text1, image1,textcounter1));
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
                holder.textHolder.setText(item.title);

            if (holder.textCounterHolder != null){
                if (item.counter > 0){
                    holder.textCounterHolder.setVisibility(View.VISIBLE);
                    holder.textCounterHolder.setText(""+item.counter);
                }else{
                    holder.textCounterHolder.setVisibility(View.GONE);
                }
            }

            if (holder.imageHolder != null) {
                if (item.iconRes > 0) {
                    holder.imageHolder.setVisibility(View.VISIBLE);
                    holder.imageHolder.setImageResource(item.iconRes);
                } else {
                    holder.imageHolder.setVisibility(View.GONE);
                }
            }

            TextView text1 = (TextView) view.findViewById(R.id.menu_item_title);
            ImageView image1 = (ImageView) view.findViewById(R.id.menu_item_icon);

            text1.setTextColor(menuItemFontColor);
            view.setBackgroundColor(menuItemBackgroundColor);

            image1.setColorFilter(menuItemFontColor); //TODO: Maybe change the filter to something else idk
            //SET DIFFERENT ICON
        }

        return view;
    }
}
