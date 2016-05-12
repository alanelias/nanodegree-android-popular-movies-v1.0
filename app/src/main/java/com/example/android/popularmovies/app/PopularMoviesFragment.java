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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

public class PopularMoviesFragment extends Fragment {

    // referance to grid view adapter
    public MoviesAdapter moviesAdapter;

    // declare broadcast tasks
    public static final int BROADCAST_TASK_CLEAN_LIST_AND_UPDATE = 900;
    public static final int BROADCAST_TASK_UPDATE_FRAGMENT_LAYOUT = 901;

    // reference to gridview
    private RecyclerView gridView;

    // define log tag
    private final String LOG_TAG = PopularMovies.class.getSimpleName();

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PopularMoviesFragment() {
        //Log.i(LOG_TAG, "PopularMoviesFragment");
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    //public static PopularMoviesFragment newInstance(int sectionNumber) {
    //    PopularMoviesFragment fragment = new PopularMoviesFragment();
    //    Bundle args = new Bundle();
    //    args.putInt(ARG_SECTION_NUMBER, sectionNumber);
    //    fragment.setArguments(args);
    //    return fragment;
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);
        gridView = (RecyclerView) inflater.inflate(
                R.layout.fragment_popular_movies, container, false);



        createGridViewAdapter();

        updateMoviesViewLayout();

        updateAppTitle();

        return gridView;
    }

    // to fetch movies from api by page
    private void updateMoviesViewLayout() {

        if (moviesAdapter.MOVIES_VIEW.equalsIgnoreCase(getString(R.string.pref_movies_view_grid))) {
            gridView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
        }else if (moviesAdapter.MOVIES_VIEW.equalsIgnoreCase(getString(R.string.pref_movies_view_list))) {
            gridView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        }
    }

    // to fetch movies from api by page
    private void updateMovies(int page) {

        updateMoviesViewLayout();

        // run background task and fetch from api data
        FetchMoviesTask moviesTask = new FetchMoviesTask(getContext(), moviesAdapter, gridView);

        moviesTask.execute(String.valueOf(page));
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies(1);
    }

    @Override
    public void onResume() {
        super.onResume();

        // register the broadcast receiver
        IntentFilter fragmentTaskFilter = new IntentFilter("fragment.tasks");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(popularMoviesFragmentBCReceiver, fragmentTaskFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // register the broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(popularMoviesFragmentBCReceiver);
    }

    private BroadcastReceiver popularMoviesFragmentBCReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final int task = intent.getIntExtra("pupular.movies.fragment", -1);
            if (task == BROADCAST_TASK_CLEAN_LIST_AND_UPDATE) {

                // recreate and attach adapter to gridview
                createGridViewAdapter();

                // reload the gridview with data from api
                updateMovies(1);
                
                updateAppTitle();

            } else if (task == BROADCAST_TASK_UPDATE_FRAGMENT_LAYOUT) {

                // recreate and attach adapter to gridview
                createGridViewAdapter();

                // reload the gridview with data from api
                updateMovies(1);

                updateAppTitle();
            }

        }
    };

    // get the saved value from sharedPreferences
    private void updateMoviesViewMode() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String moviesViewMode = sharedPreferences.getString(getString(R.string.pref_movies_view), getString(R.string.pref_movies_view_list));

        // update the value inside the adapter
        moviesAdapter.MOVIES_VIEW = moviesViewMode;

    }


    private void createGridViewAdapter() {

        // MoviesAdapter will take the data from a the list and create the the ListView items
        moviesAdapter = new MoviesAdapter(new ArrayList<HashMap<String, String>>(), getContext());

        updateMoviesViewMode();

        // attach adapter to gridview
        gridView.setAdapter(moviesAdapter);
    }

    private void updateAppTitle(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // get the value of Order type (Page type) (Popular Movies = 0 / Highest Rated = 1)
        String pageType = sharedPreferences.getString(getContext().getString(R.string.pref_page_type), getContext().getString(R.string.pref_page_type_popular_movies));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(pageType);
    }

}
