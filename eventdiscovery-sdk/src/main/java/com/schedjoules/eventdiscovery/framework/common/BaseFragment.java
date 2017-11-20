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

package com.schedjoules.eventdiscovery.framework.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.schedjoules.eventdiscovery.R;
import com.schedjoules.eventdiscovery.framework.utils.colors.AttributeColor;
import com.schedjoules.eventdiscovery.framework.utils.colors.Transparent;

import gk.android.investigator.Investigator;


/**
 * Base class for all Fragments in the app.
 *
 * @author Gabor Keszthelyi
 */
public abstract class BaseFragment extends Fragment
{

    /**
     * Sets whether the the View of this Fragment can cover the status bar.
     */
    protected void setStatusBarCoverEnabled(boolean enabled)
    {
        new StatusBar(getActivity()).update(enabled ?
                Transparent.INSTANCE : new AttributeColor(getContext(), R.attr.colorPrimaryDark));
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Investigator.log(this, "savedState", savedInstanceState);
        Investigator.log(this, "args", getArguments());
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Investigator.log(this, "outState", outState);
        Investigator.log(this, "args", getArguments());
    }


}
