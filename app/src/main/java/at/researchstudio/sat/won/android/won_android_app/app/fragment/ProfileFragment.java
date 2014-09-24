package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;

/**
 * Created by fsuda on 21.08.2014.
 */
public class ProfileFragment extends Fragment {
    private Button dialogButton;
    private Button toastButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        dialogButton = (Button) rootView.findViewById(R.id.opendialog);
        toastButton = (Button) rootView.findViewById(R.id.showtoast);

        dialogButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("I AM A DIALOG");
                builder.setTitle("DIALOG TITLE");

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Dialog","DIALOG YES");
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Dialog", "DIALOG NO");
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
        getActivity().setTitle(R.string.mi_profile);
        return rootView;
    }
}
