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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

    // define log tag name
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    // reference to gridview adapter
    private MoviesAdapter moviesAdapter;

    // reference to Context
    private final Context mContext;

    // reference to dialog (the waiting screen)
    private ProgressDialog dialog;


    private RecyclerView recyclerView;

    public FetchMoviesTask(Context context, MoviesAdapter mMoviesAdapter, RecyclerView recyclerView) {
        mContext = context;
        moviesAdapter = mMoviesAdapter;
        dialog = new ProgressDialog(context);
        this.recyclerView = recyclerView;
    }


    // process api server response as json and create
    // array of movies and return it back
    protected ArrayList<HashMap<String, String>> appendMoviesDataFromJsonToAdapter(String jsonString) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OPM_LIST = "results";
        final String OPM_ID = "id";
        final String OPM_TITLE = "original_title";
        final String OPM_DESCRIPTION = "overview";
        final String OPM_RATE = "vote_average";
        final String OPM_DATE = "release_date";
        final String OPM_LANG = "original_language";
        final String OPM_ADULT = "adult";
        final String OPM_IMG = "poster_path";
        final String OPM_PAGE = "page";

        // define list of all movies
        ArrayList<HashMap<String, String>> moviesData = new ArrayList<HashMap<String, String>>();

        JSONObject moviesJson = new JSONObject(jsonString);

        int page = Integer.valueOf(moviesJson.getString(OPM_PAGE));

        // verify response page number and update the adapter page number
        if(page > 0){
            moviesAdapter.setCurrentPage(page);
        } else return moviesData;

        JSONArray moviesArray = moviesJson.getJSONArray(OPM_LIST);

        for(int i = 0; i < moviesArray.length(); i++) {

            // Get the JSON object representing the one movie row
            JSONObject movieRow = moviesArray.getJSONObject(i);
            HashMap<String, String> mItem = new HashMap<String, String>();

            mItem.put(MoviesAdapter.HASH_MAP_KEY_ID, movieRow.getString(OPM_ID));
            mItem.put(MoviesAdapter.HASH_MAP_KEY_TITLE, movieRow.getString(OPM_TITLE));
            mItem.put(MoviesAdapter.HASH_MAP_KEY_DESCRIPTION, movieRow.getString(OPM_DESCRIPTION));
            mItem.put(MoviesAdapter.HASH_MAP_KEY_DATE, beautifyDate(movieRow.getString(OPM_DATE)));
            mItem.put(MoviesAdapter.HASH_MAP_KEY_RATE, convertRateFrom10To5Stars(Float.valueOf(movieRow.getString(OPM_RATE))));
            String movieAges = mContext.getString(R.string.movie_adult_all);
            if(movieRow.getString(OPM_ADULT).equalsIgnoreCase("true")) {
                movieAges = mContext.getString(R.string.movie_adult_plus_18);
            }
            mItem.put(MoviesAdapter.HASH_MAP_KEY_ADULT, movieAges);
            mItem.put(MoviesAdapter.HASH_MAP_KEY_LANG, movieRow.getString(OPM_LANG));
            mItem.put(MoviesAdapter.HASH_MAP_KEY_IMAGE, movieRow.getString(OPM_IMG));

            moviesData.add(mItem);
            //moviesAdapter.appendMovie(mItem);

        }

        return moviesData;
    }

    // convert date from yyyy-mm-dd to MMM dd, yyyy
    private String beautifyDate(String movieDate){
        SimpleDateFormat newDate = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat oldDate = new SimpleDateFormat("yyyy-mm-dd");
        Date mDate = null;
        try {
            mDate = oldDate.parse(movieDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(mDate == null) return "N/A";

        return newDate.format(mDate);
    }

    // convert rate from 10 to 5
    private String convertRateFrom10To5Stars(float rate){
        float rateValue = mapNumbers(rate, 0, 10, 0, 5);
        rateValue = (float)Math.round(rateValue * 10) / 10;
        return String.valueOf(rateValue);
    }

    // mapping numbers between two ranges
    private float mapNumbers(float currentValue, float fromRangeMin, float fromRangeMax, float ToRangeMin, float ToRangeMax) {
        return (currentValue - fromRangeMin) * (ToRangeMax - ToRangeMin) / (fromRangeMax - fromRangeMin) + ToRangeMin;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
        // verify page number
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        String PARAM_PAGE = "page";
        String PAGE_TYPE = BuildConfig.THE_MOVIE_DB_API_POPULAR_URL;

        // get page type from shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String pageType = sharedPreferences.getString(mContext.getString(R.string.pref_page_type), mContext.getString(R.string.pref_page_type_popular_movies));
        Log.i(LOG_TAG, pageType);
        if(pageType.equalsIgnoreCase(mContext.getString(R.string.pref_page_type_highest_rated))) {
            PAGE_TYPE = BuildConfig.THE_MOVIE_DB_API_TOP_RETED_URL;
        }

        try {
            // Construct the URL for the themovieDB query
            // Possible parameters are avaiable at themovieDB API page, at
            // https://www.themoviedb.org/documentation/api
            Uri builtUri = Uri.parse(BuildConfig.THE_MOVIE_DB_API_BASE_URL +
                    PAGE_TYPE ).buildUpon()
                    .appendQueryParameter(BuildConfig.THE_MOVIE_DB_API_APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .appendQueryParameter(PARAM_PAGE, params[0]).build();

            URL url = new URL(builtUri.toString());

            Log.i(LOG_TAG, builtUri.toString());

            // Create the request to themovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return appendMoviesDataFromJsonToAdapter(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> moviesData) {
        super.onPostExecute(moviesData);

        // add the new data to adapter
        moviesAdapter.appendMovies(moviesData);

        //recyclerView.setAdapter(new MoviesAdapter(moviesData, mContext));
        //moviesAdapter.notifyDataSetChanged();

        Log.i(LOG_TAG, "-----------------" + moviesData.size());

        // dismiss waiting message
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        // create waiting message
        this.dialog.setMessage("Loading please wait..");
        this.dialog.show();

    }
}
