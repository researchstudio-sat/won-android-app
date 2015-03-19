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

import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ConnectionListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;

import java.util.ArrayList;

/**
 * This Fragment shows a List of Connections
 * Created by fsuda on 10.10.2014.
 */
public class ConnectionListFragment extends Fragment {
    private static final String LOG_TAG = ConnectionListFragment.class.getSimpleName();

    private MainActivity activity;
    private CreateListTask createListTask;
    private SwipeRefreshLayout swipeLayout;
    private ListView mConnectionListView;
    private ConnectionListItemAdapter mConnectionListItemAdapter;

    private String postId;
    private boolean receivedRequestsOnly;

    //*********FRAGMENT LIFECYCLE********************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
            receivedRequestsOnly=args.getBoolean(Connection.TYPE_RECEIVED_ONLY_REF,false);
        }else{
            postId = null;
            receivedRequestsOnly = false;
        }

        Log.d(LOG_TAG, "Fragment started with postId: " + postId+ " recReqOnly: "+receivedRequestsOnly);

        View rootView = inflater.inflate(R.layout.fragment_connections, container, false);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mConnectionListView = (ListView) rootView.findViewById(R.id.connection_list);

        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        activity = (MainActivity) getActivity();
        if(isMailbox()){swipeLayout.setRefreshing(true);}
        styleActionBar();

        mConnectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Connection connection = (Connection) mConnectionListItemAdapter.getItem(position);
                Fragment fragment;
                Bundle args = new Bundle();

                if(receivedRequestsOnly){
                    Log.d(LOG_TAG, "REQUEST SHOW POST WITH ID: "+ connection.getMatchedPost().getURI());
                    args.putString(Post.ID_REF, connection.getMatchedPost().getURI().toString());
                    args.putString(Post.TITLE_REF, connection.getMyPost().getTitle());

                    fragment = new PostFragment();
                }else {
                    Log.d(LOG_TAG, "CONVERSATIONID: " + connection.getURI());
                    args.putString(Connection.ID_REF, connection.getURIString());

                    fragment = new ConversationFragment();
                }
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                createListTask = new CreateListTask();
                createListTask.execute();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        createListTask = new CreateListTask();
        createListTask.execute();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG,"onDestroy trying to cancel createListTask");
        super.onDestroy();
        if(createListTask != null && createListTask.getStatus() == AsyncTask.Status.RUNNING) {
            createListTask.cancel(true);
        }
    }
    //***********************************************************

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
                    if(mConnectionListItemAdapter!=null) {
                        mConnectionListItemAdapter.getFilter().filter(query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(mConnectionListItemAdapter!=null) {
                        mConnectionListItemAdapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });
        }
    }

    public boolean isMailbox(){
        return postId==null;
    }

    private class CreateListTask extends AsyncTask<String, Integer, ArrayList<Connection>> {
        @Override
        protected ArrayList<Connection> doInBackground(String... params) {

            if(isMailbox()) {
                return activity.getPostService().getConversations();
            }else if(receivedRequestsOnly) {
                return activity.getPostService().getRequestsByPostId(postId);
            }else{
                return activity.getPostService().getConversationsByPostId(postId);
            }
        }

        @Override
        protected void onCancelled(ArrayList<Connection> linkArray) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //putListInView(linkArray);
        }

        protected void onPostExecute(ArrayList<Connection> linkArray) {
            putListInView(linkArray);
        }

        private void putListInView(ArrayList<Connection> linkArray){
            mConnectionListItemAdapter = new ConnectionListItemAdapter(getActivity(), isMailbox(), receivedRequestsOnly);
            for(Connection connection : linkArray) {
                mConnectionListItemAdapter.addItem(connection);
            }
            mConnectionListView.setAdapter(mConnectionListItemAdapter);
            swipeLayout.setRefreshing(false);
        }
    }

    private void styleActionBar() {
        if(isMailbox()){
            activity.setDrawerToggle(true);
            ActionBar ab = activity.getActionBar();

            ab.setTitle(getString(R.string.mi_mailbox));
            ab.setSubtitle(null);
            ab.setIcon(R.drawable.ic_launcher);
        }
    }
}
