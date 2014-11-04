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

import android.app.*;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.SearchView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.PostListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.components.LoadingDialog;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;

import java.util.ArrayList;

/**
 * Created by fsuda on 21.08.2014.
 */
public class PostBoxFragment extends ListFragment {
    private static final String LOG_TAG = PostBoxFragment.class.getSimpleName();

    private CreateListTask createListTask;
    private ListView mNeedListView;
    private PostListItemAdapter mPostListItemAdapter;
    private MainActivity activity;

    private String postId;

    //***************FRAGMENT LIFECYLCLE******************************************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"ON CREATE VIEW");
        activity = (MainActivity) getActivity();
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
        }else{
            postId=null;
        }

        styleActionBar();
        Log.d(LOG_TAG,"Fragment started with postId: "+postId);

        mNeedListView = (ListView) inflater.inflate(R.layout.fragment_postbox, container, false);

        return mNeedListView;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG,"FRAGMENT ONSTART IS CALLED");
        super.onStart();
        createListTask = new CreateListTask();
        createListTask.execute();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy trying to cancel createListTask");
        super.onDestroy();
        if(createListTask != null && createListTask.getStatus() == AsyncTask.Status.RUNNING) {
            createListTask.cancel(true);
        }
    }
    //****************************************************************************************************

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(activity.isDrawerOpen()){
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(LOG_TAG,"LIST ITEM CLICKED!!");
        //TODO: Implement "Real" list item clicking
        Post post = (Post) mPostListItemAdapter.getItem(position);
        Fragment fragment;

        Bundle args = new Bundle();

        if(isPostBox()) { //IF IT IS ONE OF YOUR OWN POSTS
            fragment = new MyPostFragment();
        }else{ //IF ITS A POST FROM SOMEBODY ELSE
            //post.setMatches(0);
            //mPostListItemAdapter.notifyDataSetChanged();
            args.putString(Post.TITLE_REF, getActivity().getActionBar().getTitle().toString());
            fragment = new PostFragment();
        }

        postId = post.getUuidString();
        args.putString(Post.ID_REF, postId);


        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Determines whether the Fragment is used for PostBox or for the Matches View
     * @return
     */
    public boolean isPostBox(){
        return postId == null;
    }

    private class CreateListTask extends AsyncTask<String, Integer, ArrayList<Post>> {
        private LoadingDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(isPostBox()) { //SHOW LOADING DIALOG ONLY IN POSTBOX VIEW
                progress = new LoadingDialog(getActivity(), this);
                progress.show();

                progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.d(LOG_TAG, "called onCancel");
                        if(createListTask != null && createListTask.getStatus() == AsyncTask.Status.RUNNING) {
                            createListTask.cancel(true);
                        }
                    }
                });
            }
        }

        @Override
        protected ArrayList<Post> doInBackground(String... params) {
            if(isPostBox()) {
                return activity.getPostService().getMyPosts();
            }else{
                return activity.getPostService().getMatchesByPostId(postId);
            }
        }

        @Override
        protected void onCancelled(ArrayList<Post> linkArray) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //TODO: INSERT CACHED RESULTS, WITHOUT CALL OF NEW THINGY
            if(linkArray != null) {
                mPostListItemAdapter = new PostListItemAdapter(getActivity());
                for (Post post : linkArray) {
                    mPostListItemAdapter.addItem(post); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
                }
                setListAdapter(mPostListItemAdapter);
            }
        }

        protected void onPostExecute(ArrayList<Post> linkArray) {
            mPostListItemAdapter = new PostListItemAdapter(getActivity());
            for(Post post : linkArray) {
                mPostListItemAdapter.addItem(post); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
            }
            setListAdapter(mPostListItemAdapter);
            if(isPostBox()) { //DISMISS PROGRESS DIALOG ONLY IN POSTBOX VIEW
                progress.dismiss();
            }
        }
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
}
