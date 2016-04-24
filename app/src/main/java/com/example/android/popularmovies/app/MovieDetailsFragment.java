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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    private HashMap<String, String> mMovieData;

    private final String LOG_TAG = PopularMovies.class.getSimpleName();

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MovieDetails.INTENT_HASHMAP)) {
            mMovieData = (HashMap<String, String>) intent.getSerializableExtra(MovieDetails.INTENT_HASHMAP);
            if(mMovieData != null) {
                ImageView movieImage= (ImageView) rootView.findViewById(R.id.movie_details_image); // movie image
                TextView movieTitle = (TextView) rootView.findViewById(R.id.movie_details_title); // movie title
                TextView movieDescription = (TextView) rootView.findViewById(R.id.movie_details_description); // movie description
                TextView movieDate = (TextView) rootView.findViewById(R.id.movie_details_date); // movie date
                TextView movieRate = (TextView) rootView.findViewById(R.id.movie_details_rate); // movie rate
                RatingBar movieRatingBar = (RatingBar) rootView.findViewById(R.id.movie_details_rating_bar); // movie rating bar

                String ImgURL = BuildConfig.THE_MOVIE_DB_API_IMAGES_BASE_URL + BuildConfig.THE_MOVIE_DB_API_SINGLE_VIEW_IMG_SIZE + mMovieData.get(MoviesListAdapter.HASH_MAP_KEY_IMAGE);

                Picasso.with(getActivity().getApplicationContext()).load(ImgURL).into(movieImage);

                movieTitle.setText(mMovieData.get(MoviesListAdapter.HASH_MAP_KEY_TITLE));
                movieDescription.setText(mMovieData.get(MoviesListAdapter.HASH_MAP_KEY_DESCRIPTION));

                movieDate.setText(mMovieData.get(MoviesListAdapter.HASH_MAP_KEY_DATE));

                String ratingValue = mMovieData.get(MoviesListAdapter.HASH_MAP_KEY_RATE);
                movieRate.setText(ratingValue);
                movieRatingBar.setRating(Float.valueOf(ratingValue));
                //rootView.notifyAll();
            }
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
