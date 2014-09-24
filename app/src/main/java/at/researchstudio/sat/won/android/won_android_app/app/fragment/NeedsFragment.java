package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.net.URI;

/**
 * Created by fsuda on 21.08.2014.
 */
public class NeedsFragment extends Fragment{
    private Uri uri;
    private Button notificationButton;

    public NeedsFragment() {
        super();
    }

    public NeedsFragment(Uri uri) {
        this();
        this.uri = uri;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_needs, container, false);
        notificationButton = (Button) rootView.findViewById(R.id.sharewith);
        WebView myWebView = (WebView) rootView.findViewById(R.id.webview);

        myWebView.getSettings().setJavaScriptEnabled(true);
        if(uri!=null) {
            Log.d("URI", uri.toString());
            myWebView.loadUrl(uri.toString());
        }else{
            myWebView.loadUrl("http://www.orf.at");
        }
        getActivity().setTitle(R.string.mi_myneeds);

        notificationButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTON", "NOTIFICATION BUTTON CLICKED");
            }
        });

        return rootView;
    }
}
