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
import de.greenrobot.event.EventBus;
import org.apache.jena.riot.Lang;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import won.owner.protocol.message.base.MessageExtractingWonMessageHandlerAdapter;
import won.protocol.message.WonMessage;
import won.protocol.message.WonMessageDecoder;

import java.util.Collections;

/**
 * Created by fsuda on 19.03.2015.
 */
public class WonWebSocketHandler implements WebSocketHandler {
    private static final String LOG_TAG = WonWebSocketHandler.class.getSimpleName();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Log.d(LOG_TAG,"afterConnectionEstablished");
        Log.d(LOG_TAG,session.toString());
        Log.d(LOG_TAG,"AcceptedProtocol: "+session.getAcceptedProtocol());
        Log.d(LOG_TAG,"uri: "+session.getUri());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        EventBus.getDefault().post(message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Log.d(LOG_TAG,"handleTransportError");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Log.d(LOG_TAG,"afterConnectionClosed");
    }

    @Override
    public boolean supportsPartialMessages() {
        Log.d(LOG_TAG,"supportsPartialMessages");
        return false;
    }
}
