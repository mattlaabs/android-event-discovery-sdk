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

package com.schedjoules.eventdiscovery.location;

import android.view.View;

import com.schedjoules.eventdiscovery.R;
import com.schedjoules.eventdiscovery.framework.adapter.ListItem;
import com.schedjoules.eventdiscovery.location.model.NamedPlace;


/**
 * TODO rename to Place
 *
 * List item for the location suggestion.
 *
 * @author Gabor Keszthelyi
 */
public class LocationSuggestionItem implements ListItem<LocationSuggestionItemView>
{
    private final NamedPlace mNamedPlace;

    private OnClickListener mOnClickListener;


    public LocationSuggestionItem(NamedPlace namedPlace)
    {
        mNamedPlace = namedPlace;
    }


    @Override
    public int layoutResId()
    {
        return R.layout.schedjoules_list_item_location_suggestion;
    }


    @Override
    public void bindDataTo(LocationSuggestionItemView view)
    {
        // TODO use match style
        view.setTitle(mNamedPlace.name());
        view.setSubTitle(mNamedPlace.extraContext());
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mOnClickListener.onPlaceSuggestionSelected(mNamedPlace);



            }
        });
    }

    // TODO consider other design, loader microfragment will solve this probably
    public void setListener(OnClickListener listener)
    {
        mOnClickListener = listener;
    }

    interface OnClickListener
    {
        void onPlaceSuggestionSelected(NamedPlace namedPlace);
    }
}
