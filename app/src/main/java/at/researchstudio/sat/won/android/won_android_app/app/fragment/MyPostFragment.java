package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.MyPostPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by fsuda on 10.10.2014.
 */
public class MyPostFragment extends Fragment {
    private static final String LOG_TAG = MyPostFragment.class.getSimpleName();

    private MyPostPagerAdapter mMyPostPagerAdapter;
    private ViewPager mMyPostViewPager;

    private String postId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "*****************************************");
        Log.d(LOG_TAG, "ON CREATE VIEW");
        Bundle args = getArguments();
        if(args!=null){
            postId = args.getString(Post.ID_REF);
        }
        Log.d(LOG_TAG,"postId: "+postId);

        View rootView = inflater.inflate(R.layout.fragment_mypost, container, false);

        //Initialize ViewPager
        mMyPostPagerAdapter = new MyPostPagerAdapter(getActivity().getFragmentManager(),postId);

        mMyPostViewPager = (ViewPager) rootView.findViewById(R.id.mypost_viewpager);
        mMyPostViewPager.setAdapter(mMyPostPagerAdapter);
        mMyPostViewPager.setOffscreenPageLimit(2);
        TabPageIndicator indicator = (TabPageIndicator)rootView.findViewById(R.id.mypost_viewpager_indicator);
        indicator.setViewPager(mMyPostViewPager);
        Log.d(LOG_TAG, "*****************************************");
        return rootView;
    }

    @Override
    public void onLowMemory() {
        mMyPostViewPager.setOffscreenPageLimit(1);
        super.onLowMemory();
    }
}
