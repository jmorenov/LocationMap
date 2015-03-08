package com.androidgranada.botemap.GUI;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidgranada.botemap.R;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public Fragment fragment;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle = null;

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //Globales.inicializar(getApplicationContext());

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        setActionBarTitleByPosition(0);
        restoreActionBar();
    }

    @Override
    public void onPause() {
        fragment.onPause();
        super.onPause();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        setFragment(position);
        setActionBarTitleByPosition(position);
        restoreActionBar();
    }

    public void setActionBarTitleByPosition(int position) {
        mTitle = getTagByPosition(position);
    }

    public String getTagByPosition(int position) {
        String title;
        switch (position) {
            case 1: //Localizaciones
                title = getString(R.string.title_section2);
                break;
            case 2: //Perfil
                title = getString(R.string.title_section3);
                break;
            default: //Map
                title = getString(R.string.title_section1);
                break;
        }
        return title;
    }

    public Fragment getFragmentByPosition(int position) {
        Fragment fragment;
        switch (position) {
            case 1:
                fragment = new LocationsFragment();
                break;
            case 2:
                fragment = new PerfilFragment();
                break;
            default:
                fragment = new MapFragment();
                break;
        }
        return fragment;
    }

    public void setFragment(int position) {
        String title = getTagByPosition(position);
        if (title != mTitle) {
            fragment = getFragmentByPosition(position);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_enter, R.anim.slide_exit, R.anim.pop_enter, R.anim.pop_exit);
            fragmentTransaction.replace(R.id.container, fragment);
            /*if(mTitle == getTagByPosition(0))
                fragmentTransaction.addToBackStack(null);*/
            fragmentTransaction.commit();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            if (mTitle == getString(R.string.title_section1))
                getMenuInflater().inflate(R.menu.map, menu);
            else if (mTitle == getString(R.string.title_section2))
                getMenuInflater().inflate(R.menu.locations, menu);
            else if (mTitle == getString(R.string.title_section3))
                getMenuInflater().inflate(R.menu.perfil, menu);
            else
                getMenuInflater().inflate(R.menu.global, menu);

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
            Intent myIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(myIntent, 0);
            return true;
        } else if (id == R.id.action_send_position_contacts) {
            if (!((MapFragment) fragment).map.isPositionEnabled()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_select_position_first),
                        Toast.LENGTH_SHORT).show();
            } else {
                Intent myIntent = new Intent(getApplicationContext(), ContactsActivity.class);
                startActivity(myIntent);
            }
            return true;
        } else if (id == R.id.action_select_map_position) {
            ((MapFragment) fragment).selectPositionMap();
            return true;
        } else if (id == R.id.action_share_map_position) {
            if (!((MapFragment) fragment).map.isPositionEnabled()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_select_position_first),
                        Toast.LENGTH_SHORT).show();
            } else
                ((MapFragment) fragment).sharePositionMap();
            return true;
        } else if (id == R.id.action_refresh_locations) {
            return true;
        } else if (id == R.id.action_edit_perfil) {
            PerfilFragment.cambiarModo(1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

