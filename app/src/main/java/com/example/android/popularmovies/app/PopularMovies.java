/*
 *     Popular Movies | The app allow users to discover the most popular movies playing
 *     Copyright (C) <2016>  <Alaa Elias>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PopularMovies extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    // reference to Context
    private Context mContext;

    // define log tag
    private final String LOG_TAG = PopularMovies.class.getSimpleName();

    private DrawerLayout mDrawerLayout;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popular_movies);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_view_grid);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }

        //mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mContext = this.getApplicationContext();

        // define default tap and set popular movies as default
        int defaultTap  = SectionsPagerAdapter.PAGE_MOST_POPULAR;

        // get reference to sharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // get the value of Order type (Page type) (Popular Movies = 0 / Highest Rated = 1)
        String pageType = sharedPreferences.getString(mContext.getString(R.string.pref_page_type), mContext.getString(R.string.pref_page_type_popular_movies));
        if(pageType.equalsIgnoreCase(getString(R.string.pref_page_type_highest_rated))) {
            defaultTap = SectionsPagerAdapter.PAGE_TOP_RELATED;
        }

        // set the e current tap active
        mViewPager.setCurrentItem(defaultTap);

        // Attach the page change listener inside the activity
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                // On page selected we need to update the page type key with the current tap value
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                switch (position) {
                    case SectionsPagerAdapter.PAGE_MOST_POPULAR:

                        editor.putString(getString(R.string.pref_page_type), getString(R.string.pref_page_type_popular_movies));

                        Log.i(LOG_TAG, getString(R.string.pref_page_type_popular_movies));

                        break;
                    case SectionsPagerAdapter.PAGE_TOP_RELATED:

                        editor.putString(getString(R.string.pref_page_type), getString(R.string.pref_page_type_highest_rated));

                        Log.i(LOG_TAG, getString(R.string.pref_page_type_highest_rated));

                        break;
                }
                // Save new values to sharedPreferences
                editor.commit();

                // send broadcast message to fragment class to update the movies list
                sendTaskToFragment(PopularMoviesFragment.BROADCAST_TASK_CLEAN_LIST_AND_UPDATE);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_grid) {
            // On view grid call changeMoviesView to change the movies view
            changeMoviesView(getString(R.string.pref_movies_view_grid));

            return true;
        }else if(id == R.id.action_view_list) {
            // On view list call changeMoviesView to change the movies view
            changeMoviesView(getString(R.string.pref_movies_view_list));

            return true;
        }else if(id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    // this method will send broadcast message to fragment
    // with task number
    private void sendTaskToFragment(int taskName){
        Intent intentData = new Intent("fragment.tasks");
        intentData.putExtra("pupular.movies.fragment", taskName);
        LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(intentData);
    }

    // will change the movies view to list or grid
    private void changeMoviesView(String viewMode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // save the new value of movies view mode
        editor.putString(getString(R.string.pref_movies_view), viewMode);
        editor.commit();

        // send broadcast message to fragment to update the UI
        sendTaskToFragment(PopularMoviesFragment.BROADCAST_TASK_UPDATE_FRAGMENT_LAYOUT);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        adapter.addFragment(new PopularMoviesFragment(), getString(R.string.pref_page_type_popular_movies));
        adapter.addFragment(new PopularMoviesFragment(), getString(R.string.pref_page_type_highest_rated));
        //adapter.addFragment(new CheeseListFragment(), "Category 3");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


}
