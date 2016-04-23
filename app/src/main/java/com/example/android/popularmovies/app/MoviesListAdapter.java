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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MoviesListAdapter extends BaseAdapter {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

   

    public static final String HASH_MAP_KEY_ID = "id";
    public static final String HASH_MAP_KEY_TITLE = "title";
    public static final String HASH_MAP_KEY_DESCRIPTION = "description";
    public static final String HASH_MAP_KEY_RATE = "rate";
    public static final String HASH_MAP_KEY_DATE = "date";
    public static final String HASH_MAP_KEY_IMAGE = "image";
    public static final String HASH_MAP_KEY_ADULT = "adult";
    public static final String HASH_MAP_KEY_LANG = "lang";

    private int currentPage = 1;
    private int nextPage = 1;


    private ArrayList<HashMap<String, String>> moviesData;

    private Activity activity;
    private Context mContext;
    private static LayoutInflater inflater=null;

    public MoviesListAdapter(ArrayList<HashMap<String, String>> moviesListData, Activity a, Context context) {
        activity = a;
        mContext = context;
        moviesData = moviesListData;
        currentPage = nextPage = 1;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return moviesData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HashMap<String, String> singleRow = new HashMap<String, String>();

        singleRow = moviesData.get(position);

        View v = inflater.inflate(R.layout.movie_list_row, null);
        if(convertView==null) {
            convertView = v;
        }



        TextView movieTitle = (TextView) convertView.findViewById(R.id.movie_row_title); // movie title
        TextView movieDesciption = (TextView) convertView.findViewById(R.id.movie_row_description); // movie description

        TextView movieDate = (TextView) convertView.findViewById(R.id.movie_row_date); // movie date

        ImageView movieImage= (ImageView) convertView.findViewById(R.id.movie_row_image); // movie image
        TextView movieAdult = (TextView) convertView.findViewById(R.id.movie_row_adult); // movie adult status

        RatingBar movieRatingBar = (RatingBar) convertView.findViewById(R.id.movie_rating_bar); // movie rating bar
        TextView movieRate = (TextView) convertView.findViewById(R.id.movie_row_rate); // movie rate

        movieTitle.setText(singleRow.get(HASH_MAP_KEY_TITLE));
        movieDesciption.setText(singleRow.get(HASH_MAP_KEY_DESCRIPTION));

        movieDate.setText(singleRow.get(HASH_MAP_KEY_DATE));

        String ratingValue = singleRow.get(HASH_MAP_KEY_RATE);
        movieRatingBar.setRating(Float.valueOf(ratingValue));
        movieRate.setText(ratingValue);

        movieAdult.setText(singleRow.get(HASH_MAP_KEY_ADULT));

        String ImgURL = BuildConfig.THE_MOVIE_DB_API_IMAGES_BASE_URL + BuildConfig.THE_MOVIE_DB_API_LIST_VIEW_IMG_SIZE + singleRow.get(HASH_MAP_KEY_IMAGE);
        Log.i(LOG_TAG, ImgURL);
        Picasso.with(mContext).load(ImgURL).into(movieImage);
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

    public void appendMovie(HashMap<String, String> newMovieData){
        moviesData.add(newMovieData);
    }

}
