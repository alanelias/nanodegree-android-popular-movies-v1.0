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


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MoviesAdapter extends BaseAdapter {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private Activity activity;

    private Context mContext;

    private static LayoutInflater inflater=null;

    public static final String HASH_MAP_KEY_ID = "id";
    public static final String HASH_MAP_KEY_TITLE = "title";
    public static final String HASH_MAP_KEY_DESCRIPTION = "description";
    public static final String HASH_MAP_KEY_RATE = "rate";
    public static final String HASH_MAP_KEY_DATE = "date";
    public static final String HASH_MAP_KEY_IMAGE = "image";
    public static final String HASH_MAP_KEY_ADULT = "adult";
    public static final String HASH_MAP_KEY_LANG = "lang";

    public String MOVIES_VIEW = "";

    private int currentPage = 1;

    private int nextPage = 1;

    private GridView gridView = null;


    private ArrayList<HashMap<String, String>> moviesData;



    public MoviesAdapter(ArrayList<HashMap<String, String>> moviesListData, Activity a, Context context, GridView gridView) {
        activity = a;
        mContext = context;
        moviesData = moviesListData;
        currentPage = nextPage = 1;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MOVIES_VIEW = mContext.getString(R.string.pref_movies_view_list);
        this.gridView = gridView;
    }

    @Override
    public int getCount() {
        return moviesData.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return moviesData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HashMap<String, String> singleRow = new HashMap<String, String>();

        singleRow = moviesData.get(position);

        int moviesViewLayout = R.layout.movie_list_row;

        if(MOVIES_VIEW.equalsIgnoreCase(mContext.getString(R.string.pref_movies_view_grid))) {
            moviesViewLayout = R.layout.movie_grid_row;
        }

        View v = inflater.inflate(moviesViewLayout, null);
        if(convertView==null) {
            convertView = v;
        }

        TextView movieTitle, movieDesciption, movieDate, movieAdult, movieRate;
        ImageView movieImage;

        String IMAGE_SIZE = BuildConfig.THE_MOVIE_DB_API_GRID_VIEW_IMG_SIZE;

        movieImage= (ImageView) convertView.findViewById(R.id.movie_row_image); // movie image

        RatingBar movieRatingBar = (RatingBar) convertView.findViewById(R.id.movie_rating_bar); // movie rating bar

        movieDate = (TextView) convertView.findViewById(R.id.movie_row_date); // movie date

        String ratingValue = singleRow.get(HASH_MAP_KEY_RATE);

        movieRatingBar.setRating(Float.valueOf(ratingValue));

        movieDate.setText(singleRow.get(HASH_MAP_KEY_DATE));

        // check the view mode if list we have to show more information
        if(moviesViewLayout == R.layout.movie_list_row) {
            movieTitle = (TextView) convertView.findViewById(R.id.movie_row_title); // movie title
            movieDesciption = (TextView) convertView.findViewById(R.id.movie_row_description); // movie description
            movieAdult = (TextView) convertView.findViewById(R.id.movie_row_adult); // movie adult status
            movieRate = (TextView) convertView.findViewById(R.id.movie_row_rate); // movie rate

            movieTitle.setText(singleRow.get(HASH_MAP_KEY_TITLE));
            movieDesciption.setText(singleRow.get(HASH_MAP_KEY_DESCRIPTION));

            movieAdult.setText(singleRow.get(HASH_MAP_KEY_ADULT));
            movieRate.setText(ratingValue);

            IMAGE_SIZE = BuildConfig.THE_MOVIE_DB_API_LIST_VIEW_IMG_SIZE;
        }

        String ImgURL = BuildConfig.THE_MOVIE_DB_API_IMAGES_BASE_URL + IMAGE_SIZE + singleRow.get(HASH_MAP_KEY_IMAGE);
        Picasso.with(mContext).load(ImgURL).into(movieImage);
        System.gc();
        return convertView;
    }



    public void clearAdapter(){
        moviesData.clear();
        currentPage = nextPage = 1;
    }

    public int getCurrentPage(){
        return currentPage;
    }

    public void setCurrentPage(int cPage){
        currentPage = cPage;
    }

    public int getNextPage(){
        return nextPage;
    }

    public void setNextPage(int nPage){
        nextPage = nPage;
    }
    public void setNextPage(){
        nextPage = currentPage + 1;
    }

    // add single movie date
    public void appendMovie(HashMap<String, String> newMovieData){
        moviesData.add(newMovieData);
    }

    // add movies data
    public void appendMovies(ArrayList<HashMap<String, String>> newMoviesData){

        if(newMoviesData != null) {
            moviesData.addAll(newMoviesData);
            /*for (int i = 0; i < newMoviesData.size(); i++) {
                HashMap<String, String> movieData = newMoviesData.get(i);
                moviesData.add(movieData);
            }*/
        }

        notifyDataSetChanged();


    }



}
