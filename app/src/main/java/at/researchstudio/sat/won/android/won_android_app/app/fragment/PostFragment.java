package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.ImagePagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.TypeSpinnerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostTypeSpinnerModel;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.viewpagerindicator.IconPageIndicator;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by fsuda on 21.08.2014.
 */
public class PostFragment extends Fragment {
    private static final String LOG_TAG = PostFragment.class.getSimpleName();

    private TextView postTitle;
    private String postId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG,"ON CREATE VIEW");
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
            Log.d(LOG_TAG,"Fragment started with postId: "+postId);
        }
        Log.d(LOG_TAG,"postId: "+postId);

        View rootView = inflater.inflate(R.layout.fragment_post, container, false);

        postTitle = (TextView) rootView.findViewById(R.id.post_title);
        postTitle.setText(postId);
        getActivity().setTitle(postId); //TODO: SET CORRECT TITLE

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG,"ON RESUME");
        super.onResume();
    }
    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "ON DESTROY");
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        Log.d(LOG_TAG, "ON LOW MEMORY");
        super.onLowMemory();
    }
}
