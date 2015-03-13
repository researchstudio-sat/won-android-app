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
import android.os.AsyncTask;
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
import at.researchstudio.sat.won.android.won_android_app.app.adapter.PostListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * Created by fsuda on 10.10.2014.
 */
public class MyPostFragment extends Fragment {
    private static final String LOG_TAG = MyPostFragment.class.getSimpleName();

    private MainActivity activity;
    private CreatePostTask createPostTask;

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
        createPostTask = new CreatePostTask();
        createPostTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(createPostTask != null && createPostTask.getStatus() == AsyncTask.Status.RUNNING) {
            createPostTask.cancel(true);
        }
    }
    //*********************************************************************************************************

    @Override
    public void onLowMemory() {
        mMyPostViewPager.setOffscreenPageLimit(1);
        super.onLowMemory();
    }

    private class CreatePostTask extends AsyncTask<String, Integer, Post> {
        @Override
        protected Post doInBackground(String... params) {
            return activity.getPostService().getMyPostById(postId);
        }

        @Override
        protected void onCancelled(Post post) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //TODO: DO TOAST OR SOMETHING
        }

        protected void onPostExecute(Post post) {
            putListInView(post);
        }

        private void putListInView(Post post) {
            mIndicator = (TabPageIndicator) rootView.findViewById(R.id.mypost_viewpager_indicator);
            mMyPostViewPager = (ViewPager) rootView.findViewById(R.id.mypost_viewpager);

            //Initialize ViewPager
            mMyPostPagerAdapter = new MyPostPagerAdapter(activity, post);

            Parcelable state = mMyPostPagerAdapter.saveState();

            mMyPostViewPager.setAdapter(mMyPostPagerAdapter);
            mMyPostViewPager.setOffscreenPageLimit(1);
            mMyPostViewPager.setSaveFromParentEnabled(false); //This is necessary because it prevents the ViewPager from being messed up on pagechanges and popbackstack's
            //TODO: SET CURRENT TAB TO THE PAGE IT WAS SET

            mIndicator.setViewPager(mMyPostViewPager);

            mIndicator.setVisibility(View.VISIBLE);
            mMyPostViewPager.setVisibility(View.VISIBLE);
        }
    }
}
