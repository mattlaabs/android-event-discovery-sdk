/*
 * Copyright 2016 SchedJoules
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

package com.schedjoules.eventdiscovery.location;

import android.content.Context;
import android.content.SharedPreferences;

import com.schedjoules.client.eventsdiscovery.locations.StructuredGeoLocation;


/**
 * {@link LastSelectedLocation} that uses SharedPreferences to store the data.
 *
 * @author Gabor Keszthelyi
 */
public final class SharedPrefLastSelectedLocation implements LastSelectedLocation
{
    private static final String PREFERENCES_NAME = "LastSelectedLocationStore";
    private static final String KEY_LOCATION_NAME = "key_lastLocation_name";
    private static final String KEY_LATITUDE = "key_lastLocation_latitude";
    private static final String KEY_LONGITUDE = "key_lastLocation_longitude";

    private static final int NOT_EXIST = 6666;

    private final SharedPreferences mPrefs;
    private final Context mContext;


    public SharedPrefLastSelectedLocation(Context context)
    {
        mContext = context.getApplicationContext();
        mPrefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }


    @Override
    public void update(NamedLocation location)
    {
        mPrefs.edit()
                .putString(KEY_LOCATION_NAME, location.name().toString())
                .putFloat(KEY_LATITUDE, location.geoLocation().latitude())
                .putFloat(KEY_LONGITUDE, location.geoLocation().longitude())
                .apply();
    }


    @Override
    public NamedLocation get()
    {
        String name = mPrefs.getString(KEY_LOCATION_NAME, null);
        float latitude = mPrefs.getFloat(KEY_LATITUDE, NOT_EXIST);
        float longitude = mPrefs.getFloat(KEY_LONGITUDE, NOT_EXIST);

        if (name == null || longitude == NOT_EXIST || latitude == NOT_EXIST)
        {
            return new Anywhere(mContext);
        }
        else
        {
            return new StructuredNamedLocation(
                    name, new StructuredGeoLocation(latitude, longitude));
        }
    }


    @Override
    public void clear()
    {
        mPrefs.edit().clear().apply();
    }
}
