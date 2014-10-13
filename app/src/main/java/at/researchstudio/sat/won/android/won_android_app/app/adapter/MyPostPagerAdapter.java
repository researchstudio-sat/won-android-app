package at.researchstudio.sat.won.android.won_android_app.app.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.*;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;

/**
 * Created by fsuda on 10.10.2014.
 */
public class MyPostPagerAdapter extends FragmentPagerAdapter{
    private String postId;

    public MyPostPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyPostPagerAdapter(FragmentManager fm, String postId) {
        super(fm);
        this.postId = postId;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;

        switch(position){
            case 0:
            default:
                //POST VIEW PAGE
                fragment = new PostFragment();
                break;
            case 1:
                //MATCHES VIEW PAGE
                fragment = new PostBoxFragment();
                break;
            case 2:
                //REQUESTS PAGE
                fragment = new RequestListFragment();
                break;
            case 3:
                //CONVERSATION PAGE
                fragment = new ConversationListFragment();
                break;
        }
        //This will be used to determine where the post came from
        Bundle args = new Bundle();
        args.putString(Post.ID_REF, postId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //TODO: GET THE STRINGS FROM RESOURCES
        switch(position){
            case 0:
            default:
                //POST VIEW PAGE
                return "Post";
            case 1:
                //MATCHES VIEW PAGE
                return "Matches";
            case 2:
                //REQUESTS PAGE
                return "Requests";
            case 3:
                //CONVERSATION PAGE
                return "Conversations";
        }
    }
}
