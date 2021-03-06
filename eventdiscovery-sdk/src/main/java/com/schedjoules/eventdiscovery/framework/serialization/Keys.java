/*
 * Copyright 2017 SchedJoules
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.schedjoules.eventdiscovery.framework.serialization;

import android.os.Parcelable;

import com.schedjoules.client.ApiQuery;
import com.schedjoules.client.eventsdiscovery.Category;
import com.schedjoules.client.eventsdiscovery.Envelope;
import com.schedjoules.client.eventsdiscovery.Event;
import com.schedjoules.client.eventsdiscovery.GeoLocation;
import com.schedjoules.client.eventsdiscovery.ResultPage;
import com.schedjoules.eventdiscovery.framework.filter.categoryoption.CategoryOption;
import com.schedjoules.eventdiscovery.framework.model.category.Categories;
import com.schedjoules.eventdiscovery.framework.serialization.core.Box;
import com.schedjoules.eventdiscovery.framework.serialization.core.Key;
import com.schedjoules.eventdiscovery.framework.utils.loadresult.LoadResult;

import org.dmfs.android.microfragments.MicroFragment;
import org.dmfs.android.microfragments.MicroFragmentHost;
import org.dmfs.pigeonpost.Cage;
import org.dmfs.rfc5545.DateTime;


/**
 * Collection of {@link Key}s used in the project.
 *
 * @author Gabor Keszthelyi
 */
public final class Keys
{
    public static final Key<DateTime> DATE_TIME_START_AFTER = new SchedJoulesKey<>("START_AFTER");

    public static final Key<GeoLocation> GEO_LOCATION = new SchedJoulesKey<>("GEO_LOCATION");

    public static final Key<Integer> LOCATION_RADIUS = new SchedJoulesKey<>("LOCATION_RADIUS");

    public static final Key<Integer> THEME = new SchedJoulesKey<>("THEME");

    public static final Key<MicroFragment> MICRO_FRAGMENT = new SchedJoulesKey<>("MICRO_FRAGMENT");

    public static final Key<MicroFragmentHost> MICRO_FRAGMENT_HOST = new SchedJoulesKey<>("MICRO_FRAGMENT_HOST");

    public static final Key<ResultPage<Envelope<Event>>> EVENTS_RESULT_PAGE = new SchedJoulesKey<>("EVENTS_RESULT_PAGE");

    public static final Key<Cage<LoadResult<ResultPage<Envelope<Event>>>>> EVENTS_LOAD_RESULT_CAGE = new SchedJoulesKey<>("EVENTS_LOAD_RESULT_CAGE");

    public static final Key<Cage<Boolean>> RELOAD_EVENT_LIST_CAGE = new SchedJoulesKey<>("RELOAD_EVENT_LIST_CAGE");

    public static final Key<Categories> CATEGORIES = new SchedJoulesKey<>("CATEGORIES");

    public static final Key<Iterable<CategoryOption>> CATEGORY_OPTIONS = new SchedJoulesKey<>("CATEGORY_OPTIONS");

    public static final Key<Parcelable> SUPER_STATE = new SchedJoulesKey<>("SUPER_STATE");

    public static final Key<ApiQuery<ResultPage<Envelope<Event>>>> DISCOVERY_QUERY = new SchedJoulesKey<>("DISCOVERY_QUERY");

    public static final Key<Iterable<Category>> FILTER_CATEGORIES = new SchedJoulesKey<>("FILTER_CATEGORIES");

    public static final Key<Cage<Box<Iterable<Category>>>> CATEGORIES_CAGE = new SchedJoulesKey<>("CATEGORIES_CAGE");


    private Keys()
    {
    }

}
