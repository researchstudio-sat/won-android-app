package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;

/**
 * Created by fsuda on 21.08.2014.
 */
public class ProfileFragment extends Fragment {
    private static final String LOG_TAG = ProfileFragment.class.getSimpleName();
    private Button dialogButton;
    private Button toastButton;
    private Uri uri;
    private Button notificationButton;

    public ProfileFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        dialogButton = (Button) rootView.findViewById(R.id.opendialog);
        toastButton = (Button) rootView.findViewById(R.id.showtoast);
        notificationButton = (Button) rootView.findViewById(R.id.sharewith);
        WebView myWebView = (WebView) rootView.findViewById(R.id.webview);

        dialogButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("I AM A DIALOG");
                builder.setTitle("DIALOG TITLE");

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(LOG_TAG,"DIALOG YES");
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(LOG_TAG, "DIALOG NO");
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        toastButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Location:"+LocationService.getCurrentLocation()+" Accuracy: "+LocationService.getCurrentLocation().getAccuracy(), Toast.LENGTH_SHORT).show();
            }
        });



        myWebView.getSettings().setJavaScriptEnabled(true);
        if(uri!=null) {
            Log.d(LOG_TAG, uri.toString());
            myWebView.loadUrl(uri.toString());
        }else{
            myWebView.loadUrl("http://www.orf.at");
        }

        notificationButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "NOTIFICATION BUTTON CLICKED");
            }
        });

        getActivity().setTitle(R.string.mi_profile);
        return rootView;
    }
}
