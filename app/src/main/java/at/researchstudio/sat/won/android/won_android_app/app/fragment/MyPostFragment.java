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
import at.researchstudio.sat.won.android.won_android_app.app.event.ReceivedMyPostEvent;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import com.viewpagerindicator.TabPageIndicator;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;
import de.greenrobot.event.util.AsyncExecutor;
import de.greenrobot.event.util.ThrowableFailureEvent;

/**
 * Created by fsuda on 10.10.2014.
 */
public class MyPostFragment extends Fragment {
    private static final String LOG_TAG = MyPostFragment.class.getSimpleName();

    private MainActivity activity;

    private MyPostPagerAdapter mMyPostPagerAdapter;
    private ViewPager mMyPostViewPager;
    private TabPageIndicator mIndicator;

    private View rootView;
    private ViewGroup container;
    private LayoutInflater inflater;

    private String postId;

    //*************** FRAGMENT LIFECYCLE***********************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args!=null){
            postId = args.getString(Post.ID_REF);
        }
        this.inflater = inflater;
        this.container = container;
        rootView = inflater.inflate(R.layout.fragment_mypost, container, false);

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        AsyncExecutor.create().execute(new DataRetrieval());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    //*********************************************************************************************************

    @Override
    public void onLowMemory() {
        mMyPostViewPager.setOffscreenPageLimit(1);
        super.onLowMemory();
    }

    private class DataRetrieval implements AsyncExecutor.RunnableEx {
        @Override
        public void run() {
            activity.getPostService().getMyPostById(postId);
        }
    }

    public void onEventMainThread(ReceivedMyPostEvent event) {
        Log.d(LOG_TAG, "MyPostEvent received");
        mIndicator = (TabPageIndicator) rootView.findViewById(R.id.mypost_viewpager_indicator);
        mMyPostViewPager = (ViewPager) rootView.findViewById(R.id.mypost_viewpager);

        //Initialize ViewPager
        mMyPostPagerAdapter = new MyPostPagerAdapter(activity, event.getPost());

        Parcelable state = mMyPostPagerAdapter.saveState();

        mMyPostViewPager.setAdapter(mMyPostPagerAdapter);
        mMyPostViewPager.setOffscreenPageLimit(1);
        mMyPostViewPager.setSaveFromParentEnabled(false); //This is necessary because it prevents the ViewPager from being messed up on pagechanges and popbackstack's
        //TODO: SET CURRENT TAB TO THE PAGE IT WAS SET

        mIndicator.setViewPager(mMyPostViewPager);

        mIndicator.setVisibility(View.VISIBLE);
        mMyPostViewPager.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(ThrowableFailureEvent event) {
        Log.e(LOG_TAG,event.getThrowable()+ " - "+event.getExecutionScope());
    }
}
