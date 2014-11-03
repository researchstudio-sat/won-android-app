/*
 * Copyright 2014 Research Studios Austria Forschungsges.m.b.H.
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

import android.app.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import at.researchstudio.sat.won.android.won_android_app.app.*;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.WelcomeScreenPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.constants.Mock;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.*;
import at.researchstudio.sat.won.android.won_android_app.app.service.ImageLoaderService;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;
import at.researchstudio.sat.won.android.won_android_app.app.service.PostService;
import at.researchstudio.sat.won.android.won_android_app.app.service.SettingsService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;


public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                                                              GooglePlayServicesClient.ConnectionCallbacks,
                                                              GooglePlayServicesClient.OnConnectionFailedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ImageLoaderService mImgLoader;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private WelcomeScreenPagerAdapter mWelcomeScreenPagerAdapter;
    private ViewPager mWelcomeScreenViewPager;
    private PostService postService;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize Connection to the backend
        postService = new PostService();

        //Initialize LocationService
        LocationService.init(new LocationClient(this, this, this));
        //Initialize PreferencesService
        SettingsService.init(getSharedPreferences(SettingsService.PREFS_NAME, Context.MODE_PRIVATE));

        mImgLoader = new ImageLoaderService(this);

        //TODO: THIS OPEN VIA URI DOES NOT NECESSARILY WORK
        if(getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Log.d(LOG_TAG,"Opened via url");
            Log.d(LOG_TAG,""+getIntent().getData());

            //OPEN NEEDS FRAGMENT RIGHT AWAY
            showMainMenu();
            //getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment(getIntent().getData())).commit();
            getFragmentManager().beginTransaction().replace(R.id.container, new PostBoxFragment()).commit();
        }else{
            Log.d(LOG_TAG,"Opened from launcher");
            Log.d(LOG_TAG,""+getIntent().getData());

            if((SettingsService.appStarts > 0) && !SettingsService.showStartupScreen) {
                showMainMenu();
            }else {
                showWelcomeScreen();
            }
        }
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
    protected void onStop() {
        LocationService.disconnect();
        super.onStop();
    }

    @Override
    protected void onStart() {
        LocationService.connect();
        super.onStart();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch(position){
            case 0:
                mTitle = getString(R.string.mi_postbox);
                fragment = new PostBoxFragment();
                break;
            case 1:
                mTitle = getString(R.string.mi_mailbox);
                fragment = new ConnectionListFragment();
                break;
            case 2:
                mTitle = getString(R.string.mi_createpost);
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

        //actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu in Activity");
        if ((mNavigationDrawerFragment != null) && (!mNavigationDrawerFragment.isDrawerOpen())) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem searchViewItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchViewItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(LOG_TAG,"SEARCHQUERY: "+query);
                    //TODO: INVOKE SEARCH
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d(LOG_TAG,"SEARCHTEXT: "+newText);
                    //TODO: CHANGE SEARCH RESULTS MAYBE
                    return true;
                }
            });

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
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            showSettings();
            return true;
        }

        if (id == android.R.id.home){ //TODO MAKE THIS A SWITCH STATEMENT NOT IFS
            Log.d(LOG_TAG,"PRESSED HOME BUTTON");
            if(popBackStackIfPossible()){
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //TODO: IMPL A METHOD TO HAVE A TOAST AND ANOTHER BACK TO EXIT THE APP
        if(!popBackStackIfPossible()) {
            super.onBackPressed();
        }
    }

    public void skipWelcome(View v) {
        showMainMenu();
    }

    private void showMainMenu() {
        getActionBar().show();
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void showWelcomeScreen() {
        getActionBar().hide();
        setContentView(R.layout.welcome_screen);

        mWelcomeScreenPagerAdapter = new WelcomeScreenPagerAdapter(getFragmentManager());

        mWelcomeScreenViewPager = (ViewPager) findViewById(R.id.welcome_screen_pager);
        mWelcomeScreenViewPager.setAdapter(mWelcomeScreenPagerAdapter);
    }

    private void showSettings() {
        if((mNavigationDrawerFragment != null) && (mNavigationDrawerFragment.isDrawerOpen())){
            mNavigationDrawerFragment.closeDrawer();
        }
        mTitle = getString(R.string.pref_header);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new SettingsFragment();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    public void setTitle(String title){
        this.mTitle = title;
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
        mNavigationDrawerFragment.setDrawerToggle(enabled);
    }

    public void displayImage(String url, int loader, ImageView imageView) {
        mImgLoader.displayImage(url, loader, imageView);
    }

    public ImageLoaderService getImageLoaderService(){
        return mImgLoader;
    }

    public PostService getPostService(){
        return postService;
    }
}
