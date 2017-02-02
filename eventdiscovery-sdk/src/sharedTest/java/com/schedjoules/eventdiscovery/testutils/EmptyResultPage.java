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

package com.schedjoules.eventdiscovery.testutils;

import com.schedjoules.client.ApiQuery;
import com.schedjoules.client.eventsdiscovery.Envelope;
import com.schedjoules.client.eventsdiscovery.Event;
import com.schedjoules.client.eventsdiscovery.ResultPage;

import java.util.Collections;


/**
 * @author Gabor Keszthelyi
 */
public final class EmptyResultPage implements ResultPage<Envelope<Event>>
{
    @Override
    public Iterable<Envelope<Event>> items()
    {
        return Collections.emptyList();
    }


    @Override
    public boolean isFirstPage()
    {
        return true;
    }


    @Override
    public boolean isLastPage()
    {
        return true;
    }


    @Override
    public ApiQuery<ResultPage<Envelope<Event>>> previousPageQuery() throws IllegalStateException
    {
        throw new IllegalStateException("No previous query");
    }


    @Override
    public ApiQuery<ResultPage<Envelope<Event>>> nextPageQuery() throws IllegalStateException
    {
        throw new IllegalStateException("No next query");
    }
}
