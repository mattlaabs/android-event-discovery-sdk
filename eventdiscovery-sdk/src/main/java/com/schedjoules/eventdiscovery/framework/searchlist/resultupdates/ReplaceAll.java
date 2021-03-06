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

package com.schedjoules.eventdiscovery.framework.searchlist.resultupdates;

import com.schedjoules.eventdiscovery.framework.list.changes.nonnotifying.NonNotifyingChangeableList;

import java.util.List;


/**
 * {@link ResultUpdate} to replace all items with the given ones, if the current query string still matches.
 *
 * @author Gabor Keszthelyi
 */
public final class ReplaceAll<T> implements ResultUpdate<T>
{
    private final List<T> mItems;
    private final String mQuery;


    public ReplaceAll(List<T> items, String query)
    {
        mItems = items;
        mQuery = query;
    }


    @Override
    public void apply(NonNotifyingChangeableList<T> changeableList, String currentQuery)
    {
        new SearchResultUpdate<>(
                new com.schedjoules.eventdiscovery.framework.list.changes.nonnotifying.ReplaceAll<>(mItems), mQuery)
                .apply(changeableList, currentQuery);
    }
}
