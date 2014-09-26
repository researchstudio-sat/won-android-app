package at.researchstudio.sat.won.android.won_android_app.app.model;

/**
 * Created by fsuda on 26.09.2014.
 */
public class TypeSpinnerModel {
    private int titleRes;
    private int iconRes;

    public TypeSpinnerModel(int titleRes, int iconRes) {
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}
