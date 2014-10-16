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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.MessageListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.enums.MessageType;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by fsuda on 14.10.2014.
 */
public class ConversationFragment extends Fragment {
    private static final String LOG_TAG = ConversationFragment.class.getSimpleName();

    private CreateListTask createListTask;
    private ListView mMessageListView;
    private ImageButton mSendMessage;
    private EditText mMessageText;
    private MessageListItemAdapter mMessageListItemAdapter;

    private String conversationId;

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
        Log.d(LOG_TAG, "onDestroy trying to cancel createListTask");
        super.onDestroy();
        if(createListTask != null && createListTask.getStatus() == AsyncTask.Status.RUNNING) {
            createListTask.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        /*************************
        TEST NEW ACTIONBAR IMPL

        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setSubtitle("THIS IS THE SUBTITLE");
        actionBar.setIcon(R.drawable.offer);
        */

        if(args!=null){
            conversationId=args.getString(Connection.ID_REF);
            Log.d(LOG_TAG, "Fragment started with conversationId: " + conversationId);
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

    private class CreateListTask extends AsyncTask<String, Integer, ArrayList<MessageItemModel>> {
        @Override
        protected ArrayList<MessageItemModel> doInBackground(String... params) {
            return Mock.getMessagesByConversationId(UUID.fromString(conversationId));
        }

        @Override
        protected void onCancelled(ArrayList<MessageItemModel> linkArray) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //TODO: INSERT CACHED RESULTS, WITHOUT CALL OF NEW THINGY
            if(linkArray != null) {
                mMessageListItemAdapter = new MessageListItemAdapter(getActivity());
                for (MessageItemModel message : linkArray) {
                    mMessageListItemAdapter.addItem(message); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
                }
                mMessageListView.setAdapter(mMessageListItemAdapter);
                mMessageListView.setSelection(mMessageListItemAdapter.getCount() - 1);
            }
        }

        protected void onPostExecute(ArrayList<MessageItemModel> linkArray) {
            mMessageListItemAdapter = new MessageListItemAdapter(getActivity());
            for (MessageItemModel message : linkArray) {
                mMessageListItemAdapter.addItem(message); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
            }
            mMessageListView.setAdapter(mMessageListItemAdapter);
            mMessageListView.setSelection(mMessageListItemAdapter.getCount() - 1);
        }
    }

    private void sendMessage(){
        String messageText = mMessageText.getText().toString();

        if(messageText!= null && !"".equals(messageText.trim())) {
            MessageItemModel message = new MessageItemModel(MessageType.SEND, mMessageText.getText().toString());

            //TODO: SEND MESSAGE IN BACKGROUND
            mMessageListItemAdapter.addItem(message);
            mMessageListView.setAdapter(mMessageListItemAdapter);
            mMessageListView.setSelection(mMessageListItemAdapter.getCount() - 1);
        }

        mMessageText.setText("");
    }
}
