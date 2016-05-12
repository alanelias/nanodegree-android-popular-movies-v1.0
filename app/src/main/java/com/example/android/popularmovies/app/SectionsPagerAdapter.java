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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alaaelias on 4/23/16.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    // declare order types pages
    public static final int PAGE_MOST_POPULAR = 0;
    public static final int PAGE_TOP_RELATED = 1;

    private Context mContext;

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    public SectionsPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }


    public void addFragment(PopularMoviesFragment popularMoviesFragment, String title) {
        mFragments.add(popularMoviesFragment);
        mFragmentTitles.add(title);
    }
}