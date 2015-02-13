/*
 * Copyright 2015 Research Studios Austria Forschungsges.m.b.H.
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

package at.researchstudio.sat.won.android.won_android_app.app.activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.WelcomeScreenPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.*;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.service.ImageLoaderService;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;
import at.researchstudio.sat.won.android.won_android_app.app.service.PostService;
import at.researchstudio.sat.won.android.won_android_app.app.service.SettingsService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.util.UUID;


public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                                                              GooglePlayServicesClient.ConnectionCallbacks,
                                                              GooglePlayServicesClient.OnConnectionFailedListener {
    private static final String APP_STARTED_REF = "app_started_ref";
    private static final String TEMPPOST_REF = "temppost_ref";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ImageLoaderService mImgLoader;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private WelcomeScreenPagerAdapter mWelcomeScreenPagerAdapter;
    private ViewPager mWelcomeScreenViewPager;
    private PostService postService;

    private boolean doubleBackToExitPressedOnce;

    private RelativeLayout mLoadingScreen;
    private FrameLayout mContainer;

    private Post tempPost;

    //*******ACTIVITY LIFECYCLE**************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean appAlreadyStarted;
        //LOAD TEMPPOST
        if(savedInstanceState != null){
            tempPost = savedInstanceState.getParcelable(TEMPPOST_REF);
            appAlreadyStarted = savedInstanceState.getBoolean(APP_STARTED_REF, false);
        }else{
            tempPost = new Post();
            appAlreadyStarted = false;
        }

        //Initialize LocationService
        LocationService.init(new LocationClient(this, this, this));
        //Initialize PreferencesService
        SettingsService.init(getSharedPreferences(SettingsService.PREFS_NAME, Context.MODE_PRIVATE));

        if(appAlreadyStarted){
            Log.d(LOG_TAG,"App was already running");
            showMainMenu();
        }else {
            //TODO: THIS OPEN VIA URI DOES NOT NECESSARILY WORK
            if (getIntent().getAction().equals(Intent.ACTION_VIEW)) {
                Log.d(LOG_TAG, "Opened via url");
                Log.d(LOG_TAG, "" + getIntent().getData());

                //OPEN NEEDS FRAGMENT RIGHT AWAY
                showMainMenu();
                getFragmentManager().beginTransaction().replace(R.id.container, new PostBoxFragment()).commit();
            } else {
                Log.d(LOG_TAG, "Opened from launcher");
                Log.d(LOG_TAG, "" + getIntent().getData());

                if ((SettingsService.appStarts > 0) && !SettingsService.showStartupScreen) {
                    showMainMenu();
                } else {
                    showWelcomeScreen();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        LocationService.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        LocationService.disconnect();
        super.onStop();
    }
    //**********************************

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(APP_STARTED_REF,true);
        outState.putParcelable(TEMPPOST_REF,tempPost);
    }

    @Override
    public void onConnected(Bundle bundle) {
        //TODO: DO NOTHING FOR NOW
    }

    @Override
    public void onDisconnected() {
        //TODO: DO NOTHING FOR NOW
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch(position){
            case 0:
                fragment = new PostBoxFragment();
                break;
            case 1:
                fragment = new ConnectionListFragment();
                break;
            case 2:
                fragment = new CreateFragment();
                break;
        }
        if(mNavigationDrawerFragment!=null) {
            mNavigationDrawerFragment.afterSelectMenuItem(position);
        }

        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }


    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            menu.clear();
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case (R.id.action_settings):
                showSettings();
                return true;
            case (android.R.id.home):
                if(popBackStackIfPossible()) {
                    return true;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!popBackStackIfPossible()) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }else {
                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.toast_back_to_exit), Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

    public void skipWelcome(View v) {
        //TODO: IMPLEMENT IF "AUTHENTICATED CHECK"-METHOD
        if(false){
            showMainMenu();
        }else{
            showLoginScreen();
        }
    }

    public void showMainMenu() {
        getActionBar().show();
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mContainer = (FrameLayout) findViewById(R.id.container);
        mLoadingScreen = (RelativeLayout) findViewById(R.id.loading_screen);
        showLoading();

        //Initialize Connection to the "backend"
        postService = new PostService();
        mImgLoader = new ImageLoaderService(this);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        hideLoading();
    }

    private void showWelcomeScreen() {
        getActionBar().hide();
        setContentView(R.layout.welcome_screen);

        mWelcomeScreenPagerAdapter = new WelcomeScreenPagerAdapter(getFragmentManager());

        mWelcomeScreenViewPager = (ViewPager) findViewById(R.id.welcome_screen_pager);
        mWelcomeScreenViewPager.setAdapter(mWelcomeScreenPagerAdapter);
    }

    private void showLoginScreen() {
        getActionBar().show();
        setContentView(R.layout.activity_login);

        // update the main content by replacing fragments
        Fragment fragment = new LoginFragment();

        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }


    private void showSettings() {
        if((mNavigationDrawerFragment != null) && (mNavigationDrawerFragment.isDrawerOpen())){
            mNavigationDrawerFragment.closeDrawer();
        }
        getActionBar().setTitle(R.string.pref_header);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new SettingsFragment();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    public boolean popBackStackIfPossible(){
        int backStackCount = getFragmentManager().getBackStackEntryCount();
        if(backStackCount>0){
            getFragmentManager().popBackStack();
            return true;
        }else{
            return false;
        }
    }

    /**
     * Enables or Disables the NavigationDrawer Item in the ActionBar based on the value of the boolean flag
     * enabled = true shows the navigation drawer icon
     * enabled = false shows the back caret icon
     * @param enabled
     */
    public void setDrawerToggle(boolean enabled) {
        if(mNavigationDrawerFragment!=null) {
            mNavigationDrawerFragment.setDrawerToggle(enabled);
        }
    }

    public ImageLoaderService getImageLoaderService(){
        return mImgLoader;
    }

    public PostService getPostService(){
        return postService;
    }

    public boolean isDrawerOpen(){
        return (mNavigationDrawerFragment != null) && (mNavigationDrawerFragment.isDrawerOpen());
    }

    public Post getTempPost() {
        return tempPost;
    }

    public void setTempPost(Post tempPost) {
        this.tempPost = tempPost;
    }

    public void showLoading() {
        if(mContainer!=null) {
            mContainer.setVisibility(View.GONE);
        }
        if(mLoadingScreen!=null) {
            mLoadingScreen.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if(mContainer!=null){
            mContainer.setVisibility(View.VISIBLE);
        }
        if(mLoadingScreen!=null) {
            mLoadingScreen.setVisibility(View.GONE);
        }
    }

    public void createDraft(String postId){
        createDraft(UUID.fromString(postId));
    }

    public void createDraft(UUID postId) {
        Toast.makeText(this, getString(R.string.toast_create_draft), Toast.LENGTH_SHORT).show();

        tempPost = postService.createDraft(postId);
        Log.d(LOG_TAG,"Creating Draft from: "+tempPost);

        Fragment fragment = new CreateFragment();

        //TODO: SUBVIEWS SHOULD PROBABLY CALL POPBACKSTACK BEFORE TO GET RID OF ANY VISIBLE VIEWS THAT ARE PRESENT (Matches View, MyPostView, PostView)

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
