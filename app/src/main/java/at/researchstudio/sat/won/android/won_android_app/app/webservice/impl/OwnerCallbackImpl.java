/*
 * Copyright 2015 Research Studios Austria Forschungsges.m.b.H.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.webservice.impl;

import android.util.Log;
import won.owner.protocol.message.OwnerCallback;
import won.protocol.message.WonMessage;
import won.protocol.model.Connection;
import won.protocol.model.Match;

import java.net.URI;

/**
 * Created by fsuda on 03.04.2015.
 */
public class OwnerCallbackImpl implements OwnerCallback {
    private static final String LOG_TAG = OwnerCallbackImpl.class.getSimpleName();

    @Override
    public void onConnectFromOtherNeed(Connection connection, WonMessage wonMessage) {
        Log.d(LOG_TAG, "onConnectFromOtherNeed");
        Log.d(LOG_TAG, "msg:" + wonMessage);
        Log.d(LOG_TAG, "con:" + connection);
        //TODO: IMPL
    }

    @Override
    public void onOpenFromOtherNeed(Connection connection, WonMessage wonMessage) {
        Log.d(LOG_TAG, "onOpenFromOtherNeed");
        Log.d(LOG_TAG, "msg:" + wonMessage);
        Log.d(LOG_TAG, "con:" + connection);
        //TODO: IMPL
    }

    @Override
    public void onCloseFromOtherNeed(Connection connection, WonMessage wonMessage) {
        Log.d(LOG_TAG, "onCloseFromOtherNeed");
        Log.d(LOG_TAG, "msg:" + wonMessage);
        Log.d(LOG_TAG, "con:" + connection);
        //TODO: IMPL
    }

    @Override
    public void onHintFromMatcher(Match match, WonMessage wonMessage) {
        Log.d(LOG_TAG, "onHintFromMatcher");
        Log.d(LOG_TAG, "msg:" + wonMessage);
        Log.d(LOG_TAG, "mat:" + match);
        //TODO: IMPL
    }

    @Override
    public void onMessageFromOtherNeed(Connection connection, WonMessage wonMessage) {
        Log.d(LOG_TAG, "onMessageFromOtherNeed");
        Log.d(LOG_TAG, "msg:" + wonMessage);
        Log.d(LOG_TAG, "con:" + connection);
        //TODO: IMPL
    }

    @Override
    public void onFailureResponse(URI uri, WonMessage wonMessage) {
        Log.d(LOG_TAG, "onFailureResponse");
        Log.d(LOG_TAG, "msg:" + wonMessage);
        Log.d(LOG_TAG, "uri:" + uri);
        //TODO: IMPL
    }

    @Override
    public void onSuccessResponse(URI uri, WonMessage wonMessage) {
        Log.d(LOG_TAG, "onSuccessResponse");
        Log.d(LOG_TAG, "msg:" + wonMessage);
        Log.d(LOG_TAG, "uri:" + uri);
        //TODO: IMPL
    }
}
