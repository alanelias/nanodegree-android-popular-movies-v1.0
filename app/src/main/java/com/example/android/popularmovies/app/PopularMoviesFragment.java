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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;

public class PopularMoviesFragment extends Fragment {

    public MoviesAdapter moviesAdapter;

    public static final int BROADCAST_TASK_CLEAN_LIST_AND_UPDATE = 900;
    public static final int BROADCAST_TASK_UPDATE_FRAGMENT_LAYOUT = 901;

    private GridView gridView;

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

        // get a refrance to ListView
        gridView = (GridView) rootView.findViewById(R.id.movies_list_view);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    if (view.getCount() - view.getLastVisiblePosition() < 3) {
                        if (moviesAdapter.getNextPage() == moviesAdapter.getCurrentPage()) {
                            moviesAdapter.setNextPage();
                            updateMovies(moviesAdapter.getNextPage());
                        }
                    }
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetails.class).putExtra(MovieDetails.INTENT_HASHMAP, moviesAdapter.getItem(position));
                startActivity(intent);
            }
        });

        // create and attach adapter to gridview
        createGridViewAdapter();

        return rootView;
    }

    private void updateMovies(int page) {

        if(moviesAdapter.MOVIES_VIEW.equalsIgnoreCase(getString(R.string.pref_movies_view_grid))) {
            gridView.setNumColumns(GridView.AUTO_FIT);
        }else if(moviesAdapter.MOVIES_VIEW.equalsIgnoreCase(getString(R.string.pref_movies_view_list))) {
            gridView.setNumColumns(1);
        }
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
        IntentFilter fragmentTaskFilter = new IntentFilter("fragment.tasks");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(popularMoviesFragmentBCReceiver, fragmentTaskFilter);
        Log.i(LOG_TAG, "registerReceiver");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(popularMoviesFragmentBCReceiver);
        Log.i(LOG_TAG, "unregisterReceiver");
    }

    private BroadcastReceiver popularMoviesFragmentBCReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final int task = intent.getIntExtra("pupular.movies.fragment", -1);
            if (task == BROADCAST_TASK_CLEAN_LIST_AND_UPDATE) {
                // recreate and attach adapter to gridview
                createGridViewAdapter();
                updateMovies(1);
            } else if (task == BROADCAST_TASK_UPDATE_FRAGMENT_LAYOUT) {
                // recreate and attach adapter to gridview
                createGridViewAdapter();
                updateMovies(1);
            }

        }
    };

    private void updateMoviesViewMode(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String moviesViewMode = sharedPreferences.getString(getString(R.string.pref_movies_view), getString(R.string.pref_movies_view_list));
        moviesAdapter.MOVIES_VIEW = moviesViewMode;

    }

    private void createGridViewAdapter(){
        // MoviesAdapter will take the data from a the list and create the the ListView items
        moviesAdapter = new MoviesAdapter(new ArrayList<HashMap<String, String>>(), getActivity(), getContext(), gridView);
        updateMoviesViewMode();
        gridView.setAdapter(moviesAdapter);
    }

}
