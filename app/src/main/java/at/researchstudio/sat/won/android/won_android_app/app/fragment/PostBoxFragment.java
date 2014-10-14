package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.*;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.PostListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.WelcomeScreenPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.components.LoadingDialog;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostListItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsuda on 21.08.2014.
 */
public class PostBoxFragment extends ListFragment {
    private static final String LOG_TAG = PostBoxFragment.class.getSimpleName();

    private CreateListTask createListTask;
    private ListView mNeedListView;
    private PostListItemAdapter mNeedListItemAdapter;

    private String postId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
            Log.d(LOG_TAG,"Fragment started with postId: "+postId);
        }
        Log.d(LOG_TAG,"postId: "+postId);

        mNeedListView = (ListView) inflater.inflate(R.layout.fragment_postbox, container, false);

        return mNeedListView;
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

    @Override
    public void onStart() {
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(LOG_TAG,"LIST ITEM CLICKED!!");
        //TODO: Implement "Real" list item clicking
        PostListItemModel needListItemModel = (PostListItemModel) mNeedListItemAdapter.getItem(position);
        Fragment fragment;

        Bundle args = new Bundle();

        if(!isMatchesList()) { //IF IT IS ONE OF YOUR OWN POSTS
            Log.d(LOG_TAG,"Clicked ownitem");
            postId=needListItemModel.getTitle();
            Log.d(LOG_TAG,"NEW POSTID: "+postId);
            args.putString(Post.ID_REF, postId); //TODO: CHANGE THIS TO ID

            fragment = new MyPostFragment();
        }else{ //IF ITS A POST FROM SOMEBODY ELSE
            //needListItemModel.setMatches(0);
            //mNeedListItemAdapter.notifyDataSetChanged();
            Log.d(LOG_TAG,"Clicked foreignitem");
            postId=needListItemModel.getTitle();
            Log.d(LOG_TAG,"NEW POSTID: "+postId);
            args.putString(Post.ID_REF, postId); //TODO: CHANGE THIS TO ID

            fragment = new PostFragment();
        }

        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    /**
     * Determines whether the Fragment is used for PostBox or for the Matches View
     * @return
     */
    public boolean isMatchesList(){
        return postId != null;
    }

    private class CreateListTask extends AsyncTask<String, Integer, ArrayList<PostListItemModel>> {
        private LoadingDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!isMatchesList()) { //SHOW LOADING DIALOG ONLY IN POSTBOX VIEW
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
        protected ArrayList<PostListItemModel> doInBackground(String... params) {

            ArrayList<PostListItemModel> retrievedList = new ArrayList<PostListItemModel>();

            int amount = 50000;

            //TODO: DUMMY DATA RETRIEVAL MOVE THIS TO THE BACKEND
            for(int i = 0; i < amount; i++) {
                if(isCancelled()){
                    Log.d(LOG_TAG, "GOT CANCELLED DURING BG WORK");
                    break;
                }

                PostListItemModel post;

                if(isMatchesList()) {
                    post = Mock.getRandomMatch();
                }else{
                    post = Mock.getRandomPost();
                }

                if(((int)(Math.random()*1000)) == 0) {
                    retrievedList.add(post);
                }
            }

            return retrievedList;
        }

        @Override
        protected void onCancelled(ArrayList<PostListItemModel> linkArray) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //TODO: INSERT CACHED RESULTS, WITHOUT CALL OF NEW THINGY
            if(linkArray != null) {
                mNeedListItemAdapter = new PostListItemAdapter(getActivity());
                for (PostListItemModel need : linkArray) {
                    mNeedListItemAdapter.addItem(need); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
                }
                setListAdapter(mNeedListItemAdapter);
            }
        }

        protected void onPostExecute(ArrayList<PostListItemModel> linkArray) {
            mNeedListItemAdapter = new PostListItemAdapter(getActivity());
            for(PostListItemModel need : linkArray) {
                mNeedListItemAdapter.addItem(need); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
            }
            setListAdapter(mNeedListItemAdapter);
            if(!isMatchesList()) { //DISMISS PROGRESS DIALOG ONLY IN POSTBOX VIEW
                progress.dismiss();
            }
        }
    }
}
