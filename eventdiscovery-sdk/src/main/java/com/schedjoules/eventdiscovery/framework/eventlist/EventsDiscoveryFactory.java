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

package com.schedjoules.eventdiscovery.framework.eventlist;

import com.schedjoules.client.ApiQuery;
import com.schedjoules.client.eventsdiscovery.Category;
import com.schedjoules.client.eventsdiscovery.EventsDiscovery;
import com.schedjoules.client.eventsdiscovery.GeoLocation;
import com.schedjoules.client.eventsdiscovery.queries.SimpleEventsDiscovery;
import com.schedjoules.eventdiscovery.framework.model.category.CategoryUris;
import com.schedjoules.eventdiscovery.framework.model.location.geolocation.UndefinedGeoLocation;
import com.schedjoules.eventdiscovery.framework.utils.factory.Factory;

import org.dmfs.iterables.EmptyIterable;
import org.dmfs.optional.Absent;
import org.dmfs.optional.Optional;
import org.dmfs.optional.Present;
import org.dmfs.rfc3986.Uri;
import org.dmfs.rfc5545.DateTime;

import java.util.TimeZone;


/**
 * Factory for creating the {@link EventsDiscovery} for loading the first page.
 * <p>
 * Note: This is used instead of implementing {@link EventsDiscovery} to avoid defining {@link ApiQuery#serializable()}.
 *
 * @author Gabor Keszthelyi
 */
public final class EventsDiscoveryFactory implements Factory<EventsDiscovery>
{

    private final Optional<DateTime> mStartAfter;
    private final Optional<GeoLocation> mLocation;
    private final Iterable<Uri> mCategoryUris;


    private EventsDiscoveryFactory(Optional<DateTime> startAfter, Optional<GeoLocation> location, Iterable<Uri> categoryUris)
    {
        mStartAfter = startAfter;
        mLocation = location;
        mCategoryUris = categoryUris;
    }


    public EventsDiscoveryFactory(Optional<DateTime> startAfter, GeoLocation location)
    {
        this(
                startAfter,
                // TODO Get rid of instanceof and possibly UndefinedGeoLocation
                location instanceof UndefinedGeoLocation ? Absent.<GeoLocation>absent() : new Present<>(location),
                EmptyIterable.<Uri>instance());
    }


    public EventsDiscoveryFactory(Optional<DateTime> startAfter, GeoLocation location, Iterable<Category> categories)
    {
        this(
                startAfter,
                // TODO Get rid of instanceof and possibly UndefinedGeoLocation
                location instanceof UndefinedGeoLocation ? Absent.<GeoLocation>absent() : new Present<>(location),
                new CategoryUris(categories));
    }


    @Override
    public EventsDiscovery create()
    {
        DateTime startAfter = mStartAfter.value(DateTime.nowAndHere());
        // convert any floating time to absolute time using the current time zone
        startAfter = startAfter.isFloating() ? startAfter.swapTimeZone(TimeZone.getDefault()) : startAfter;

        EventsDiscovery query = new SimpleEventsDiscovery()
                .withStartAtOrAfter(startAfter);

        if (mCategoryUris.iterator().hasNext())
        {
            query = query.withCategories(mCategoryUris);
        }

        if (mLocation.isPresent())
        {
            query = query.withGeoLocation(mLocation.value(), 10000 /*meters*/);
        }

        query = query.withResultsLimit(40);

        return query;
    }
}
