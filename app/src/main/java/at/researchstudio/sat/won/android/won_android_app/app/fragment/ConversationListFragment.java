package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.SearchView;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ConversationListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.model.Conversation;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.model.ConversationListItemModel;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by fsuda on 10.10.2014.
 */
public class ConversationListFragment extends ListFragment {
    private static final String LOG_TAG = ConversationListFragment.class.getSimpleName();

    private CreateListTask createListTask;
    private ListView mConversationListView;
    private ConversationListItemAdapter mConversationListItemAdapter;

    private String postId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
            Log.d(LOG_TAG, "Fragment started with postId: " + postId);
        }
        Log.d(LOG_TAG,"postId: "+postId);

        mConversationListView = (ListView) inflater.inflate(R.layout.fragment_requests, container, false);

        return mConversationListView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); //THIS IS ALL A LITTLE WEIRD STILL NOT SURE IF THIS IS AT ALL BEST PRACTICE
        getActivity().getMenuInflater().inflate(R.menu.needlist, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(LOG_TAG,"SEARCHQUERY: "+query);
                //TODO: INVOKE SEARCH
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG,"SEARCHTEXT: "+newText);
                //TODO: CHANGE SEARCH RESULTS MAYBE
                return true;
            }
        });
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(LOG_TAG,"LIST ITEM CLICKED!!");
        //TODO: Implement "Real" list item clicking
        Conversation conversation = (Conversation) mConversationListItemAdapter.getItem(position);
        Fragment fragment;

        Bundle args = new Bundle();
        Log.d(LOG_TAG, "CONVERSATIONID: " + conversation.getUuid());
        args.putString(Conversation.ID_REF, conversation.getUuidString());

        fragment = new ConversationFragment();

        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    public boolean isMailbox(){
        return postId==null;
    }

    private class CreateListTask extends AsyncTask<String, Integer, ArrayList<Conversation>> {
        @Override
        protected ArrayList<Conversation> doInBackground(String... params) {

            if(isMailbox()) {
                return new ArrayList<Conversation>(Mock.myMockConversations.values());
            }else{
                return Mock.getConversationsByPostId(UUID.fromString(postId));
            }
        }

        @Override
        protected void onCancelled(ArrayList<Conversation> linkArray) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //TODO: INSERT CACHED RESULTS, WITHOUT CALL OF NEW THINGY
            if(linkArray != null) {
                mConversationListItemAdapter = new ConversationListItemAdapter(getActivity(), isMailbox());
                for (Conversation conversation : linkArray) {
                    mConversationListItemAdapter.addItem(conversation); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
                }
                setListAdapter(mConversationListItemAdapter);
            }
        }

        protected void onPostExecute(ArrayList<Conversation> linkArray) {
            mConversationListItemAdapter = new ConversationListItemAdapter(getActivity(), isMailbox());
            for(Conversation conversation : linkArray) {
                mConversationListItemAdapter.addItem(conversation); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
            }
            setListAdapter(mConversationListItemAdapter);
        }
    }
}
