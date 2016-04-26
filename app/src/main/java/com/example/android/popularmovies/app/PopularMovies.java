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
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
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

    private Context mContext;

    private final String LOG_TAG = PopularMovies.class.getSimpleName();

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
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);


        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mContext = this.getApplicationContext();

        int defaultTap  = SectionsPagerAdapter.PAGE_MOST_POPULAR;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pageType = sharedPreferences.getString(mContext.getString(R.string.pref_page_type), mContext.getString(R.string.pref_page_type_popular_movies));
        if(pageType.equalsIgnoreCase(getString(R.string.pref_page_type_highest_rated))) {
            defaultTap = SectionsPagerAdapter.PAGE_TOP_RELATED;
        }
        mViewPager.setCurrentItem(defaultTap);

        // Attach the page change listener inside the activity
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
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
                editor.commit();
                sendTaskToFragment(PopularMoviesFragment.BROADCAST_TASK_CLEAN_LIST_AND_UPDATE);
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
                Log.i(LOG_TAG, String.valueOf(position) + " - " + String.valueOf(positionOffset) + " - " + String.valueOf(positionOffsetPixels));
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
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
            changeMoviesView(getString(R.string.pref_movies_view_grid));
            return true;
        }else if(id == R.id.action_view_list) {
            changeMoviesView(getString(R.string.pref_movies_view_list));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendTaskToFragment(int taskName){
        Intent intentData = new Intent("fragment.tasks");
        intentData.putExtra("pupular.movies.fragment", taskName);
        LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(intentData);
    }

    private  void changeMoviesView(String viewMode){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(getString(R.string.pref_movies_view), viewMode);
        Log.i(LOG_TAG, viewMode);

        editor.commit();
        sendTaskToFragment(PopularMoviesFragment.BROADCAST_TASK_UPDATE_FRAGMENT_LAYOUT);
    }


}
