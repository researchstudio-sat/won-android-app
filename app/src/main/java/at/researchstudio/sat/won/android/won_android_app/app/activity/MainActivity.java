package at.researchstudio.sat.won.android.won_android_app.app.activity;

import android.app.*;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import at.researchstudio.sat.won.android.won_android_app.app.*;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.WelcomeScreenPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.fragment.*;
import at.researchstudio.sat.won.android.won_android_app.app.service.LocationService;
import at.researchstudio.sat.won.android.won_android_app.app.service.SettingsService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;


public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                                                              GooglePlayServicesClient.ConnectionCallbacks,
                                                              GooglePlayServicesClient.OnConnectionFailedListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private WelcomeScreenPagerAdapter mWelcomeScreenPagerAdapater;
    private ViewPager mWelcomeScreenViewPager;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize LocationService
        LocationService.init(new LocationClient(this, this, this));
        //Initialize PreferencesService
        SettingsService.init(getSharedPreferences(SettingsService.PREFS_NAME, Context.MODE_PRIVATE));


        if(getIntent().getAction().equals(Intent.ACTION_VIEW)) {
            Log.d("INTENT","Opened via url");
            Log.d("INTENT",""+getIntent().getData());

            //OPEN NEEDS FRAGMENT RIGHT AWAY
            showMainMenu();
            getFragmentManager().beginTransaction().replace(R.id.container, new NeedsFragment(getIntent().getData())).commit();
        }else{
            Log.d("INTENT","Opened from launcher");
            Log.d("INTENT",""+getIntent().getData());

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
                mTitle = getString(R.string.mi_profile);
                fragment = new ProfileFragment();
                break;
            case 1:
                mTitle = getString(R.string.mi_createpost);
                fragment = new CreateFragment();
                break;
            case 2:
                mTitle = getString(R.string.mi_mailbox);
                fragment = new MailboxFragment();
                break;
            case 3:
                mTitle = getString(R.string.mi_myneeds);
                fragment = new NeedsFragment();
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
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                    Log.d("SEARCH","SEARCHQUERY: "+query);
                    //TODO: INVOKE SEARCH
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Log.d("SEARCH","SEARCHTEXT: "+newText);
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

        return super.onOptionsItemSelected(item);
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

        mWelcomeScreenPagerAdapater = new WelcomeScreenPagerAdapter(getFragmentManager());

        mWelcomeScreenViewPager = (ViewPager) findViewById(R.id.welcome_screen_pager);
        mWelcomeScreenViewPager.setAdapter(mWelcomeScreenPagerAdapater);
    }

    private void showSettings() {
        if((mNavigationDrawerFragment != null) && (mNavigationDrawerFragment.isDrawerOpen())){
            mNavigationDrawerFragment.closeDrawer();
        }
        mTitle = getString(R.string.pref_header);

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new SettingsFragment();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }
}
