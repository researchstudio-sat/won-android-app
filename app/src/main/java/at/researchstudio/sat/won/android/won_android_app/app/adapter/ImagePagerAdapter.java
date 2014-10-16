
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

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.ImageFragment;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.WelcomeScreenFragment;
import com.viewpagerindicator.IconPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by fsuda on 26.09.2014.
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter{
    private ArrayList<ImageFragment> imageFragments;

    private boolean editable = true;


    public ImagePagerAdapter(FragmentManager fm) {
        super(fm);
        imageFragments = new ArrayList<ImageFragment>();
    }

    public ImagePagerAdapter(FragmentManager fm, boolean editable) {
        this(fm);
        this.editable = editable;
    }

    public void addItem(String imageUrl){
        if(imageUrl!=null && !"".equals(imageUrl.trim())) {
            ImageFragment fragment = new ImageFragment();

            Bundle args = new Bundle();

            args.putString(ImageFragment.ARG_IMAGE_URL, imageUrl);
            args.putBoolean(ImageFragment.ARG_IMAGE_EDITABLE, editable);
            fragment.setArguments(args);

            imageFragments.add(fragment);
        }
    }

    public void removeItem(int i){
        imageFragments.remove(i);
    }

    @Override
    public Fragment getItem(int i) {
        if(i < imageFragments.size()) {
            return imageFragments.get(i);
        }else{
            return new ImageFragment();
        }
    }

    @Override
    public int getIconResId(int i) {
        return i == imageFragments.size() ?  R.drawable.pager_indicator_plus : R.drawable.pager_indicator_dot;
    }

    @Override
    public int getCount() {
        return editable ? imageFragments.size()+1 : imageFragments.size();
    }

    //Implement this like http://stackoverflow.com/questions/13664155/dynamically-add-and-remove-view-to-viewpager
    //On Click should also be set   --> on click on existing image invokes delete dialog
    //                              --> on click on placeholder should invoke image picker (camera / gallery)
}
