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

package com.schedjoules.eventdiscovery.eventlist.items;

import android.view.View;

import com.schedjoules.eventdiscovery.R;
import com.schedjoules.eventdiscovery.eventlist.itemsprovider.ScrollDirection;
import com.schedjoules.eventdiscovery.framework.adapter.ListItem;
import com.schedjoules.eventdiscovery.framework.adapter.flexibleadapter.AbstractFlexible;


/**
 * Represents a loading error message item at the bottom or top of the event list.
 *
 * @author Gabor Keszthelyi
 */
// TODO create one layout for both ErrorItem and NoMoreEventsItem and set text in bindData
public final class ErrorItem extends AbstractFlexible<View> implements ListItem<View>
{
    public static final ErrorItem TOP = new ErrorItem();
    public static final ErrorItem BOTTOM = new ErrorItem();


    public static ErrorItem get(ScrollDirection direction)
    {
        return direction == ScrollDirection.BOTTOM ? BOTTOM : TOP;
    }


    private ErrorItem()
    {
    }


    @Override
    public int layoutResId()
    {
        return R.layout.schedjoules_list_item_error;
    }


    @Override
    public void bindDataTo(View view)
    {

    }
}
