package at.researchstudio.sat.won.android.won_android_app.app.model;

import won.protocol.model.BasicNeedType;

/**
 * Created by fsuda on 26.09.2014.
 */
public class PostTypeSpinnerModel {
    private int titleRes;
    private int iconRes;
    private BasicNeedType type;

    public PostTypeSpinnerModel(int titleRes, int iconRes, BasicNeedType type) {
        this.titleRes = titleRes;
        this.iconRes = iconRes;
        this.type = type;
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

    public BasicNeedType getType() {
        return type;
    }

    public void setType(BasicNeedType type) {
        this.type = type;
    }
}
