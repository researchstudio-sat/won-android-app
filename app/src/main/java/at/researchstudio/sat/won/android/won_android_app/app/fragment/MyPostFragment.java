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

package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.MyPostPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by fsuda on 10.10.2014.
 */
public class MyPostFragment extends Fragment {
    private static final String LOG_TAG = MyPostFragment.class.getSimpleName();

    private MainActivity activity;

    private MyPostPagerAdapter mMyPostPagerAdapter;
    private ViewPager mMyPostViewPager;
    private TabPageIndicator mIndicator;

    private String postId;

    //*************** FRAGMENT LIFECYCLE***********************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        Bundle args = getArguments();

        if(args!=null){
            postId = args.getString(Post.ID_REF);
        }
        Log.d(LOG_TAG,"postId: "+postId);

        View rootView = inflater.inflate(R.layout.fragment_mypost, container, false);

        //Initialize ViewPager
        mMyPostPagerAdapter = new MyPostPagerAdapter(activity, postId);

        Parcelable state = mMyPostPagerAdapter.saveState();
        mMyPostViewPager = (ViewPager) rootView.findViewById(R.id.mypost_viewpager);

        mMyPostViewPager.setAdapter(mMyPostPagerAdapter);
        mMyPostViewPager.setOffscreenPageLimit(1);
        mMyPostViewPager.setSaveFromParentEnabled(false); //This is necessary because it prevents the ViewPager from being messed up on pagechanges and popbackstack's
        //TODO: SET CURRENT TAB TO THE PAGE IT WAS SET
        mIndicator = (TabPageIndicator) rootView.findViewById(R.id.mypost_viewpager_indicator);

        mIndicator.setViewPager(mMyPostViewPager);
        return rootView;
    }
    //*********************************************************************************************************

    @Override
    public void onLowMemory() {
        mMyPostViewPager.setOffscreenPageLimit(1);
        super.onLowMemory();
    }


}
