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
import android.util.Log;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.*;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;

/**
 * Created by fsuda on 10.10.2014.
 */
public class MyPostPagerAdapter extends FragmentStatePagerAdapter {
    private static final String LOG_TAG = MyPostPagerAdapter.class.getSimpleName();
    private String postId;
    private FragmentManager fm;

    public MyPostPagerAdapter(FragmentManager fm, String postId) {
        super(fm);
        this.postId = postId;
        this.fm = fm;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(LOG_TAG, "postId: " + postId);
        Fragment fragment;
        Bundle args = new Bundle();
        args.putString(Post.ID_REF, postId);

        //This will be used to determine where the post came from
        switch (position) {
            case 0:
            default:
                //POST VIEW PAGE
                fragment = new PostFragment();
                break;
            case 1:
                //MATCHES VIEW PAGE
                fragment = new PostBoxFragment();
                break;
            case 2:
                //REQUESTS PAGE
                fragment = new ConnectionListFragment();
                args.putBoolean(Connection.TYPE_RECEIVED_ONLY_REF, true);
                break;
            case 3:
                //CONVERSATION PAGE
                fragment = new ConnectionListFragment();
                args.putBoolean(Connection.TYPE_RECEIVED_ONLY_REF, false);
                break;
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //TODO: GET THE STRINGS FROM RESOURCES
        switch(position){
            case 0:
            default:
                //POST VIEW PAGE
                return "Post";
            case 1:
                //MATCHES VIEW PAGE
                return "Matches";
            case 2:
                //REQUESTS PAGE
                return "Requests";
            case 3:
                //CONVERSATION PAGE
                return "Conversations";
        }
    }

}
