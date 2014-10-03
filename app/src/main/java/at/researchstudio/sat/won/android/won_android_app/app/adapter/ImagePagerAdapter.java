package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.ImageFragment;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.WelcomeScreenFragment;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by fsuda on 26.09.2014.
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    String field[]={"http://placehold.it/120x120&text=image1", //TODO: MAKE THIS DYNAMIC
            "http://placehold.it/120x120&text=image2",
            "http://placehold.it/120x120&text=image3",
            "http://placehold.it/120x120&text=image4"};

    private ArrayList<ImageFragment> imageFragments;
    private boolean editable = true;

    public ImagePagerAdapter(FragmentManager fm) {
        super(fm);
        imageFragments = new ArrayList<ImageFragment>();
    }

    public ImagePagerAdapter(FragmentManager fm, boolean editable) {
        super(fm);
        imageFragments = new ArrayList<ImageFragment>();
        this.editable = editable;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new ImageFragment();
        Bundle args = null;

        if(i < field.length) {
            args = new Bundle();
            args.putString(ImageFragment.ARG_IMAGE_URL, field[i]);
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return editable ? imageFragments.size()+1 : imageFragments.size();
    }

    //Implement this like http://stackoverflow.com/questions/13664155/dynamically-add-and-remove-view-to-viewpager
    //On Click should also be set   --> on click on existing image invokes delete dialog
    //                              --> on click on placeholder should invoke image picker (camera / gallery)
}
