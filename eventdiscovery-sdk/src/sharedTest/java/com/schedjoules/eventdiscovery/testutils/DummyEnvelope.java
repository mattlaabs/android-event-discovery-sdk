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

import com.schedjoules.client.eventsdiscovery.Envelope;
import com.schedjoules.client.eventsdiscovery.Event;


/**
 * @author Gabor Keszthelyi
 */
public final class DummyEnvelope implements Envelope<Event>
{
    private final Event mEvent;


    public DummyEnvelope(Event event)
    {
        mEvent = event;
    }


    @Override
    public String etag()
    {
        return "dummy etag";
    }


    @Override
    public String uid()
    {
        return "dummy env uid";
    }


    @Override
    public boolean hasPayload()
    {
        return true;
    }


    @Override
    public Event payload()
    {
        return mEvent;
    }
}
