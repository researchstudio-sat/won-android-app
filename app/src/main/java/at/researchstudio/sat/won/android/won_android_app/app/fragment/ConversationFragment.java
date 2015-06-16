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

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.activity.MainActivity;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.MessageListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.components.LetterTileProvider;
import at.researchstudio.sat.won.android.won_android_app.app.enums.MessageType;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import de.greenrobot.event.EventBus;

import java.util.ArrayList;

/**
 * Created by fsuda on 14.10.2014.
 */
public class ConversationFragment extends Fragment {
    private static final String LOG_TAG = ConversationFragment.class.getSimpleName();
    private MainActivity activity;

    private CreateListTask createListTask;
    private ListView mMessageListView;
    private ImageButton mSendMessage;
    private EditText mMessageText;
    private MessageListItemAdapter mMessageListItemAdapter;

    private String conversationId;

    private Connection connection;
    private LetterTileProvider tileProvider;


    //******FRAGMENT LIFECYCLE*************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args!=null){
            conversationId=args.getString(Connection.ID_REF);
            Log.d(LOG_TAG, "Fragment started with conversationId: " + conversationId);
        }else{
            conversationId=null;
        }

        View rootView = inflater.inflate(R.layout.fragment_conversation, container, false);

        mMessageListView = (ListView) rootView.findViewById(R.id.conversation_messages);

        mSendMessage = (ImageButton) rootView.findViewById(R.id.conversation_send);
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mSendMessage.setEnabled(false); //DO NOT ALLOW EMPTY MESSAGES


        mMessageText = (EditText) rootView.findViewById(R.id.conversation_message);
        mMessageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });
        mMessageText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String message = mMessageText.getText().toString().trim();
                if (!"".equals(message)) {
                    mSendMessage.setEnabled(true);
                } else {
                    mSendMessage.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        activity = (MainActivity) getActivity();
        activity.showLoading();
        tileProvider = new LetterTileProvider(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        createListTask = new CreateListTask();
        createListTask.execute();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy trying to cancel createListTask");
        super.onDestroy();
        if(createListTask != null && createListTask.getStatus() == AsyncTask.Status.RUNNING) {
            createListTask.cancel(true);
        }
    }
    //*************************************************************
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(activity.isDrawerOpen()){
            super.onCreateOptionsMenu(menu, inflater);
        }else {
            menu.clear(); //THIS IS ALL A LITTLE WEIRD STILL NOT SURE IF THIS IS AT ALL BEST PRACTICE
            inflater.inflate(R.menu.list, menu);
            MenuItem searchViewItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchViewItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(mMessageListItemAdapter!=null) {
                        mMessageListItemAdapter.getFilter().filter(query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(mMessageListItemAdapter!=null) {
                        mMessageListItemAdapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });
        }
    }

    private class CreateListTask extends AsyncTask<String, Integer, ArrayList<MessageItemModel>> {
        @Override
        protected ArrayList<MessageItemModel> doInBackground(String... params) {
            connection = activity.getPostService().getConversationById(conversationId);
            return activity.getPostService().getMessagesByConversationId(conversationId);
        }

        @Override
        protected void onCancelled(ArrayList<MessageItemModel> linkArray) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //putConversationInView(linkArray);
        }

        protected void onPostExecute(ArrayList<MessageItemModel> linkArray) {
            putConversationInView(linkArray);
        }

        private void putConversationInView(ArrayList<MessageItemModel> linkArray) {
            mMessageListItemAdapter = new MessageListItemAdapter(getActivity());
            for (MessageItemModel message : linkArray) {
                mMessageListItemAdapter.addItem(message);
            }
            mMessageListView.setAdapter(mMessageListItemAdapter);
            mMessageListView.setSelection(mMessageListItemAdapter.getCount() - 1);
            styleActionBar();
            activity.hideLoading();
        }
    }



    private void sendMessage(){
        Log.d(LOG_TAG,"Sending Message");
        String messageText = mMessageText.getText().toString();

        if(messageText!= null && !"".equals(messageText.trim())) {
            MessageItemModel message = new MessageItemModel(MessageType.SEND, mMessageText.getText().toString());

            //TODO: SEND MESSAGE IN BACKGROUND
            /*mMessageListItemAdapter.addItem(message);
            mMessageListView.setAdapter(mMessageListItemAdapter);
            mMessageListView.setSelection(mMessageListItemAdapter.getCount() - 1);*/

            EventBus.getDefault().post(message);
        }

        mMessageText.setText("");
    }

    private void styleActionBar(){
        ActionBar ab = activity.getActionBar();
        Post post = connection.getMatchedPost();
        Post post2 = connection.getMyPost();

        String titleImageUrl = post.getTitleImageUrl();

        activity.setDrawerToggle(false); //DISABLE THE NAVDRAWER -> POSTFRAGMENT IS A LOWLEVEL VIEW
        ab.setTitle(post.getTitle());
        ab.setSubtitle(post2.getTitle() != null ? getString(R.string.to)+" "+post2.getTitle(): null);

        if(titleImageUrl!=null) {
            ab.setIcon(new BitmapDrawable(getResources(), activity.getImageLoaderService().getCroppedBitmap(titleImageUrl)));
        }else{
            final int tileSize = getResources().getDimensionPixelSize(R.dimen.letter_tile_size);
            final Bitmap letterTile = tileProvider.getLetterTile(post.getTitle(), post.getTitle(), tileSize, tileSize);

            ab.setIcon(new BitmapDrawable(getResources(), letterTile));
        }
    }

    public void onEvent(MessageItemModel message){
        Log.d(LOG_TAG, "MESSAGE CREATED");
        mMessageListItemAdapter.addItem(message);
        mMessageListView.setAdapter(mMessageListItemAdapter);
        mMessageListView.setSelection(mMessageListItemAdapter.getCount() - 1);
    }
}
