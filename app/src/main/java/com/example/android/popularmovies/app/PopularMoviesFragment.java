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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alaaelias on 4/23/16.
 */
public class PopularMoviesFragment extends Fragment {

    public MoviesListAdapter moviesListAdapter;

    public static final int BROADCAST_TASK_CLEAN_LIST_AND_UPDATE = 999;

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

        // MoviesListAdapter will take the data from a the list and create the the ListView items
        moviesListAdapter = new MoviesListAdapter(new ArrayList<HashMap<String, String>>(), getActivity(), getContext());

        // get a refrance to ListView
        final ListView listView = (ListView) rootView.findViewById(R.id.movies_list_view);

        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == 0) {
                    if(view.getCount() - view.getLastVisiblePosition() < 3 ){
                        if(moviesListAdapter.getNextPage()==moviesListAdapter.getCurrentPage()) {
                            moviesListAdapter.setNextPage();
                            updateMovies(moviesListAdapter.getNextPage());
                        }
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetails.class).putExtra(MovieDetails.INTENT_HASHMAP, moviesListAdapter.getItem(position));
                startActivity(intent);
            }
        });


        // attach adapter to listview
        listView.setAdapter(moviesListAdapter);

        return rootView;
    }

    private void updateMovies(int page) {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getContext(), moviesListAdapter);
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
            if(task == BROADCAST_TASK_CLEAN_LIST_AND_UPDATE) {
                Log.i(LOG_TAG, "Task Received " + task);

                moviesListAdapter.clearAdapter();
                moviesListAdapter.notifyDataSetChanged();
                updateMovies(1);
            }
        }
    };
}
