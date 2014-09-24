package at.researchstudio.sat.won.android.won_android_app.app.model;

/**
 * Created by fsuda on 22.08.2014.
 */
public class MenuItemModel {
    public int title;
    public int iconRes;
    public int counter;
    public boolean selected;

    public MenuItemModel(int title, int iconRes) {
        this(title, iconRes, false);
    }

    public MenuItemModel(int title, int iconRes, boolean selected) {
        this(title, iconRes, 0, selected);
    }

    public MenuItemModel(int title, int iconRes, int counter, boolean selected) {
        this.title = title;
        this.iconRes = iconRes;
        this.counter = counter;
        this.selected = selected;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
