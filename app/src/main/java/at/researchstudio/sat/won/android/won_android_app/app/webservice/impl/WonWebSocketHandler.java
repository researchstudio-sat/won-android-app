package at.researchstudio.sat.won.android.won_android_app.app.webservice.impl;

import android.util.Log;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

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
        Log.d(LOG_TAG,"msg:"+message);
        Log.d(LOG_TAG,"msg:"+message.getPayload());
        Log.d(LOG_TAG,"handleMessage");
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
