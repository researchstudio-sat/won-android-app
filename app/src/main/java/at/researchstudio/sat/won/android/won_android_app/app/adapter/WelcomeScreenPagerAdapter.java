package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v13.app.FragmentPagerAdapter;

import at.researchstudio.sat.won.android.won_android_app.app.fragment.WelcomeScreenFragment;

/**
 * Created by fsuda on 22.09.2014.
 */
public class WelcomeScreenPagerAdapter extends FragmentPagerAdapter {
    public WelcomeScreenPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new WelcomeScreenFragment();
        Bundle args = new Bundle();

        args.putInt(WelcomeScreenFragment.ARG_WELCOME_PAGE_NUMBER, i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3; //TODO: FIGURE OUT A WAY TO MAKE THIS DYNAMIC THIS IS HARDCODED CHANGE THAT TO THE AMOUNT OF PAGES
    }
}
