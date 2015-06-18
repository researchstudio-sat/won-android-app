/*
 * Copyright 2015 Research Studios Austria Forschungsges.m.b.H.
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

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.PostListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.event.ReceivedMatchesEvent;
import at.researchstudio.sat.won.android.won_android_app.app.event.ReceivedMyPostsEvent;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;
import won.protocol.model.NeedState;

import java.net.URI;
import java.util.ArrayList;

public class PostBoxFragment extends Fragment{
    private static final String LOG_TAG = PostBoxFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeLayout;
    private ListView mNeedListView;
    private PostListItemAdapter mPostListItemAdapter;
    private MainActivity activity;

    private String postId;
    //*******FRAGMENT LIFECYCLE************************************************************************************
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
        }else{
            postId=null;
        }

        Log.d(LOG_TAG,"Fragment started with postId: "+postId);

        View rootView = inflater.inflate(R.layout.fragment_postbox, container, false);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mNeedListView = (ListView) rootView.findViewById(R.id.postbox_list);

        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        EventBus.getDefault().register(this); //TODO: EVENT IS ALREADY REGISTERED EXCEPTION WHEN RETURNING TO POSTBOX :-(
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        swipeLayout.setRefreshing(true);

        mNeedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                displayListActions(position);
                return true;
            }
        });

        mNeedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = (Post) mPostListItemAdapter.getItem(position);

                if(post.getNeedState() == NeedState.ACTIVE) {
                    Fragment fragment;

                    Bundle args = new Bundle();

                    if (isPostBox()) { //IF IT IS ONE OF YOUR OWN POSTS
                        fragment = new MyPostFragment();
                    } else { //IF ITS A POST FROM SOMEBODY ELSE
                        args.putString(Post.TITLE_REF, getActivity().getActionBar().getTitle().toString());
                        fragment = new PostFragment();
                    }

                    postId = post.getURIString();
                    args.putString(Post.ID_REF, postId);
                    
                    fragment.setArguments(args);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else{
                    //TODO: Figure out what to do when clicking closed elements
                }
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                AsyncExecutor.create().execute(new DataRetrieval());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG,"PostBoxFragment started");
        swipeLayout.setRefreshing(true);
        AsyncExecutor.create().execute(new DataRetrieval());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(LOG_TAG,"PostBoxFragment stopped");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    //*************************************************************************************************************

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(activity!=null && activity.isDrawerOpen()){
            super.onCreateOptionsMenu(menu, inflater);
        }else {
            menu.clear(); //THIS IS ALL A LITTLE WEIRD STILL NOT SURE IF THIS IS AT ALL BEST PRACTICE
            getActivity().getMenuInflater().inflate(R.menu.list, menu);
            MenuItem searchViewItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchViewItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(mPostListItemAdapter!=null) {
                        mPostListItemAdapter.getFilter().filter(query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(mPostListItemAdapter!=null) {
                        mPostListItemAdapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });
        }
    }

    public void displayListActions(final int position){
        Post post = (Post) mPostListItemAdapter.getItem(position);
        final URI postId = post.getURI();

        if(isPostBox()){
            //CLOSED MYPOST PICKER
            if(post.getNeedState() == NeedState.INACTIVE){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.dialog_select_action_title)
                        .setItems(R.array.postbox_closed_action_picker, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0: //DRAFT
                                        activity.createDraft(postId);
                                        break;
                                    case 1: //REOPEN POST
                                        updateItemAtPosition(activity.getPostService().reOpenPost(postId), position);
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                //OPEN MYPOST PICKER
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.dialog_select_action_title)
                        .setItems(R.array.postbox_open_action_picker, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0: //DRAFT
                                        activity.createDraft(postId);
                                        break;
                                    case 1: //CLOSE
                                        updateItemAtPosition(activity.getPostService().closePost(postId), position);
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }else{
            //MATCH PICKER
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dialog_select_action_title)
                    .setItems(R.array.matches_action_picker, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which){
                                case 0:  //SEND REQUEST
                                    //TODO: IMPL SEND REQUEST --> START CHAT OR SOMETHING
                                    break;
                                case 1: //DRAFT
                                    activity.createDraft(postId);
                                    break;
                                case 2: //CLOSE MATCH
                                    //TODO: IMPL CLOSE MATCH
                                    break;
                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    /**
     * Updates the view of the given position
     * @param position
     */
    private void updateItemAtPosition(Post post, int position) {
        int visiblePosition = mNeedListView.getFirstVisiblePosition();

        View view = mNeedListView.getChildAt(position - visiblePosition); //needed because mNeedListView encapsulates only items that are visible (not all items)
        //((Post)mPostListItemAdapter.getItem(position)).setNeedState(post.getNeedState());
        mPostListItemAdapter.swapItem(post, position);
        mNeedListView.getAdapter().getView(position, view, mNeedListView);
    }


    private void styleActionBar(){
        if(isPostBox()) {
            activity.setDrawerToggle(true);
            ActionBar ab = activity.getActionBar();

            ab.setTitle(getString(R.string.mi_postbox));
            ab.setSubtitle(null);
            ab.setIcon(R.drawable.ic_launcher);
        }
    }

    /**
     * Determines whether the Fragment is used for PostBox or for the Matches View
     * @return
     */
    public boolean isPostBox(){
        return postId == null;
    }

    private void putListInView(ArrayList<Post> linkArray) {
        Log.d(LOG_TAG,"POSTBOX ARRAYLIST EVENT RECEIVED");

        mPostListItemAdapter = new PostListItemAdapter(getActivity());
        for(Post post : linkArray) {
            mPostListItemAdapter.addItem(post);
        }

        mNeedListView.setAdapter(mPostListItemAdapter);
        swipeLayout.setRefreshing(false);
        activity.hideLoading();
        styleActionBar();
    }

    public void onEventMainThread(ReceivedMyPostsEvent event) {
        putListInView(event.getMyPostsEvent());
    }

    public void onEventMainThread(ReceivedMatchesEvent event) {
        putListInView(event.getMatches());
    }

    private class DataRetrieval implements AsyncExecutor.RunnableEx {
        @Override
        public void run() throws Exception {
            if(isPostBox()) {
                activity.getPostService().getMyPosts();
            }else{
                activity.getPostService().getMatchesByPostId(postId);
            }
        }
    }
}
