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
        return WelcomeScreenFragment.PAGE_COUNT;
    }
}
