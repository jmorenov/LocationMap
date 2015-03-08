package com.androidgranada.botemap.GUI;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidgranada.botemap.R;

import java.util.ArrayList;

public class LoginActivity extends ActionBarActivity implements ActionBar.OnNavigationListener
{

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        ArrayList<String> list = new ArrayList<String>();
        list.add(getString(R.string.title_section_login1));
        list.add(getString(R.string.title_section_login2));
        list.add(getString(R.string.title_section_login3));
        list.add(getString(R.string.title_section_login4));

        AdapterClass adClass = new AdapterClass(actionBar.getThemedContext(), list);
        actionBar.setListNavigationCallbacks(adClass, this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM))
        {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        if (id == android.R.id.home)
        {
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id)
    {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            assert rootView != null;
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class AdapterClass extends ArrayAdapter<String>
    {
        Context context;
        private ArrayList<String> TextValue = new ArrayList<String>();

        public AdapterClass(Context context, ArrayList<String> TextValue)
        {
            super(context, R.layout.login_drawer_layout, R.id.login_title_section, TextValue);
            this.context = context;
            this.TextValue = TextValue;

        }


        @Override
        public View getView(int position, View coverView, ViewGroup parent)
        {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.login_drawer_layout, parent, false);

            TextView text1 = (TextView) rowView.findViewById(R.id.login_title_section);
            text1.setTextColor(getResources().getColor(R.color.gray));
            text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            text1.setText(TextValue.get(position));

            ImageView iv = (ImageView) rowView.findViewById(R.id.login_ic_action);

            switch (position)
            {
                case 0:
                    iv.setImageResource(R.drawable.ic_action_email);
                    break;
                case 1:
                    iv.setImageResource(R.drawable.ic_facebook);
                    break;
                case 2:
                    iv.setImageResource(R.drawable.ic_google);
                    break;
                case 3:
                    iv.setImageResource(R.drawable.ic_twitter);
                    break;
            }

            return rowView;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View rowView = getView(position, convertView, parent);
            TextView text1 = (TextView) rowView.findViewById(R.id.login_title_section);
            text1.setTextColor(getResources().getColor(R.color.black));
            return rowView;
        }

    }

}
