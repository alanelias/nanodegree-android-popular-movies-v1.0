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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.CustomViewHolder> {

    protected final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    private Context mContext;

    private static LayoutInflater inflater=null;

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView movieTitle, movieDesciption, movieDate, movieAdult, movieRate;
        public final ImageView movieImage;
        public final RatingBar movieRatingBar;

        public CustomViewHolder(View view) {
            super(view);
            mView = view;
            movieTitle = (TextView) view.findViewById(R.id.movie_row_title); // movie title
            movieDesciption = (TextView) view.findViewById(R.id.movie_row_description); // movie description
            movieDate = (TextView) view.findViewById(R.id.movie_row_date); // movie date
            movieAdult = (TextView) view.findViewById(R.id.movie_row_adult); // movie adult
            movieRate = (TextView) view.findViewById(R.id.movie_row_rate); // movie rate
            movieImage = (ImageView) view.findViewById(R.id.movie_row_image); // movie image
            movieRatingBar = (RatingBar) view.findViewById(R.id.movie_rating_bar); // movie rating bar
        }

        @Override
        public String toString() {
            return super.toString() + " '" + movieTitle.getText();
        }
    }

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


    private ArrayList<HashMap<String, String>> moviesData;



    public MoviesAdapter(ArrayList<HashMap<String, String>> moviesListData, Context context) {
        mContext = context;
        moviesData = moviesListData;
        currentPage = nextPage = 1;
        //inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MOVIES_VIEW = mContext.getString(R.string.pref_movies_view_list);
    }


    public int getCount() {
        return moviesData.size();
    }


    public HashMap<String, String> getItem(int position) {
        return moviesData.get(position);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getItemsLayout(), parent, false);
        //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
        //view.setBackgroundResource(mBackground);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        //holder.mBoundString = moviesData.get(position);
        HashMap<String, String> currentItem = getItem(position);



//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = v.getContext();
//                Intent intent = new Intent(context, CheeseDetailActivity.class);
//                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
//
//                context.startActivity(intent);
//            }
//        });
        if(holder.movieTitle != null)
        holder.movieTitle.setText(currentItem.get(HASH_MAP_KEY_TITLE)); // movie title
        if(holder.movieDesciption != null)
        holder.movieDesciption.setText(currentItem.get(HASH_MAP_KEY_DESCRIPTION)); // movie description
        if(holder.movieAdult != null)
        holder.movieAdult.setText(currentItem.get(HASH_MAP_KEY_ADULT));; // movie adult status
        if(holder.movieDate != null)
        holder.movieDate.setText(currentItem.get(HASH_MAP_KEY_DATE)); // movie date
        String ratingValue = currentItem.get(HASH_MAP_KEY_RATE); // rate value
        if(holder.movieRate != null)
        holder.movieRate.setText(ratingValue); // movie rate text
        holder.movieRatingBar.setRating(Float.valueOf(ratingValue)); // movie rate bar

        // movie image
        String IMAGE_SIZE = BuildConfig.THE_MOVIE_DB_API_GRID_VIEW_IMG_SIZE;
        String ImgURL = BuildConfig.THE_MOVIE_DB_API_IMAGES_BASE_URL + IMAGE_SIZE + currentItem.get(HASH_MAP_KEY_IMAGE);
        Picasso.with(mContext).load(ImgURL).into(holder.movieImage);
        //System.gc();

    }

    private int getItemsLayout(){
        int moviesViewLayout = R.layout.movie_list_row;
        if(MOVIES_VIEW.equalsIgnoreCase(mContext.getString(R.string.pref_movies_view_grid))) {
            moviesViewLayout = R.layout.movie_grid_row;
        }
        return moviesViewLayout;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return moviesData.size();
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
        Log.i(LOG_TAG, "-----------------" + moviesData.size());
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
