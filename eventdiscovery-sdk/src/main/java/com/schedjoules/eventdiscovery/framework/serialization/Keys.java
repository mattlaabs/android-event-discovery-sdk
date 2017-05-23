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

import com.schedjoules.client.eventsdiscovery.GeoLocation;
import com.schedjoules.eventdiscovery.framework.serialization.core.Key;

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


    private Keys()
    {
    }

}