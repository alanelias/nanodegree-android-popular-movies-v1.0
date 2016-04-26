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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;

public class PopularMoviesFragment extends Fragment {

    // referance to grid view adapter
    public MoviesAdapter moviesAdapter;

    // declare broadcast tasks
    public static final int BROADCAST_TASK_CLEAN_LIST_AND_UPDATE = 900;
    public static final int BROADCAST_TASK_UPDATE_FRAGMENT_LAYOUT = 901;

    // reference to gridview
    private GridView gridView;

    // define log tag
    private final String LOG_TAG = PopularMovies.class.getSimpleName();

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PopularMoviesFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PopularMoviesFragment newInstance(int sectionNumber) {
        PopularMoviesFragment fragment = new PopularMoviesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        // get a reference to ListView
        gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);

        // add on scroll listener to the grid view
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    // before 2 last items witch are in the view port of the screen
                    // we add page to the adapter counter to request data
                    if (view.getCount() - view.getLastVisiblePosition() < 3) {
                        // if the current page = the next page that means
                        // its loading so we avoid send request again
                        if (moviesAdapter.getNextPage() == moviesAdapter.getCurrentPage()) {
                            moviesAdapter.setNextPage();
                            // get movies from api by page
                            updateMovies(moviesAdapter.getNextPage());
                        }
                    }
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // when movie row clicked start the single movie details activity
                // and we pass the selected hashmap item to the details activity
                // via Intent
                Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetails.class).putExtra(MovieDetails.INTENT_HASHMAP, moviesAdapter.getItem(position));
                startActivity(intent);
            }
        });

        // create and attach adapter to gridview
        createGridViewAdapter();

        return rootView;
    }

    // to fetch movies from api by page
    private void updateMovies(int page) {

        // check the movie view mode and change the grid columns numbers in one row
        if (moviesAdapter.MOVIES_VIEW.equalsIgnoreCase(getString(R.string.pref_movies_view_grid))) {
            gridView.setNumColumns(GridView.AUTO_FIT);
        } else if (moviesAdapter.MOVIES_VIEW.equalsIgnoreCase(getString(R.string.pref_movies_view_list))) {
            gridView.setNumColumns(1);
        }

        // run background task and fetch from api data
        FetchMoviesTask moviesTask = new FetchMoviesTask(getContext(), moviesAdapter);

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

            } else if (task == BROADCAST_TASK_UPDATE_FRAGMENT_LAYOUT) {

                // recreate and attach adapter to gridview
                createGridViewAdapter();

                // reload the gridview with data from api
                updateMovies(1);
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
        moviesAdapter = new MoviesAdapter(new ArrayList<HashMap<String, String>>(), getActivity(), getContext(), gridView);

        updateMoviesViewMode();

        // attach adapter to gridview
        gridView.setAdapter(moviesAdapter);
    }

}
