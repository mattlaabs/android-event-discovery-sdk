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

package com.schedjoules.eventdiscovery.framework.microfragments.eventdetails.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.schedjoules.client.eventsdiscovery.Event;
import com.schedjoules.client.insights.steps.Screen;
import com.schedjoules.eventdiscovery.R;
import com.schedjoules.eventdiscovery.databinding.SchedjoulesFragmentEventDetailsBinding;
import com.schedjoules.eventdiscovery.framework.actions.ActionViewIterable;
import com.schedjoules.eventdiscovery.framework.actions.Actions;
import com.schedjoules.eventdiscovery.framework.actions.BaseActionFactory;
import com.schedjoules.eventdiscovery.framework.activities.MicroFragmentHostActivity;
import com.schedjoules.eventdiscovery.framework.common.BaseActivity;
import com.schedjoules.eventdiscovery.framework.common.BaseFragment;
import com.schedjoules.eventdiscovery.framework.common.ExternalUrlFeedbackForm;
import com.schedjoules.eventdiscovery.framework.datetime.LongDate;
import com.schedjoules.eventdiscovery.framework.datetime.StartAndEndTime;
import com.schedjoules.eventdiscovery.framework.eventlist.EventListActivity;
import com.schedjoules.eventdiscovery.framework.microfragments.eventdetails.ShowEventMicroFragment;
import com.schedjoules.eventdiscovery.framework.microfragments.eventdetails.fragments.menu.EventDetailsMenu;
import com.schedjoules.eventdiscovery.framework.microfragments.eventdetails.fragments.views.EventDetailsItemView;
import com.schedjoules.eventdiscovery.framework.microfragments.eventdetails.fragments.views.EventDetailsTwoLineItemView;
import com.schedjoules.eventdiscovery.framework.microfragments.eventdetails.fragments.views.HorizontalActionsView;
import com.schedjoules.eventdiscovery.framework.microfragments.eventdetails.fragments.views.SmallEventActionView;
import com.schedjoules.eventdiscovery.framework.model.SchedJoulesLinks;
import com.schedjoules.eventdiscovery.framework.utils.InsightsTask;
import com.schedjoules.eventdiscovery.framework.utils.Limiting;
import com.schedjoules.eventdiscovery.framework.utils.Skipping;

import org.dmfs.android.microfragments.FragmentEnvironment;
import org.dmfs.android.microfragments.MicroFragmentEnvironment;
import org.dmfs.httpessentials.types.Link;
import org.dmfs.httpessentials.types.StringToken;

import java.util.List;

import static com.schedjoules.eventdiscovery.framework.utils.LocationFormatter.longLocationFormat;


/**
 * A fragment representing a single Event detail screen. This fragment is either contained in a {@link EventListActivity} in two-pane mode (on tablets) or a
 * {@link MicroFragmentHostActivity} on handsets.
 *
 * @author Gabor Keszthelyi
 */
public final class EventDetailFragment extends BaseFragment implements EventDetailsMenu.Listener
{
    private Event mEvent;
    private List<Link> mActions;

    private SchedjoulesFragmentEventDetailsBinding mViews;
    private LinearLayout mVerticalItems;
    private HorizontalActionsView mHorizontalActions;
    private EventDetailsMenu mMenu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        MicroFragmentEnvironment<ShowEventMicroFragment.EventParams> env = new FragmentEnvironment<>(this);
        mEvent = env.microFragment().parameters().event();
        mActions = env.microFragment().parameters().actions();

        if (savedInstanceState == null)
        {
            new InsightsTask(getActivity()).execute(new Screen(new StringToken("details"), mEvent));
        }

        mViews = DataBindingUtil.inflate(inflater, R.layout.schedjoules_fragment_event_details, container, false);

        ((BaseActivity) getActivity()).setSupportActionBar(
                mViews.schedjoulesDetailsHeader.schedjoulesEventDetailToolbar);
        setHasOptionsMenu(true);
        mMenu = new EventDetailsMenu(this);
        mVerticalItems = mViews.schedjoulesEventDetailVerticalItems;
        mHorizontalActions = mViews.schedjoulesEventHorizontalActions;
        mViews.schedjoulesDetailsHeader.schedjoulesEventDetailToolbarLayout.setTitle(mEvent.title());
        addFixVerticalItems();
        showActions();
        Glide.with(getActivity())
                .load(new SchedJoulesLinks(mEvent.links()).bannerUri())
                .into(mViews.schedjoulesDetailsHeader.schedjoulesEventDetailBanner);
        return mViews.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        mMenu.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return mMenu.onOptionsItemSelected(item);
    }


    // TODO Could be factored out declaratively to sg like EventDetailItems implements Iterator<View>.
    // (And create LinearLayout with addViews(Iterator<View> views) for mVerticalItems (would be useful in onResult, too))
    private void addFixVerticalItems()
    {
        EventDetailsTwoLineItemView dateTimeItem = EventDetailsTwoLineItemView.inflate(mVerticalItems);
        dateTimeItem.setIcon(R.drawable.schedjoules_ic_time);
        dateTimeItem.setTitle(new LongDate(mEvent.start()).value(getContext()));
        dateTimeItem.setSubTitle(new StartAndEndTime(mEvent).value(getContext()));
        mVerticalItems.addView(dateTimeItem);

        if (mEvent.locations().iterator().hasNext())
        {
            EventDetailsItemView locationItem = EventDetailsItemView.inflate(mVerticalItems);
            locationItem.setIcon(R.drawable.schedjoules_ic_location);
            locationItem.setTextAsTitle(longLocationFormat(mEvent.locations()));
            mVerticalItems.addView(locationItem);
        }

        if (!TextUtils.isEmpty(mEvent.description()))
        {
            EventDetailsItemView descriptionItem = EventDetailsItemView.inflate(mVerticalItems);
            descriptionItem.setIcon(R.drawable.schedjoules_ic_description);
            descriptionItem.setTextAsTitle(mEvent.description());
            mVerticalItems.addView(descriptionItem);
        }

    }


    private void showActions()
    {
        if (mActions.size() > 0)
        {
            int maxNumberOfItemsInTopBar = getResources().getInteger(R.integer.schedjoules_maxNumberOfHorizontalActions);

            mHorizontalActions.showActionViews(
                    new ActionViewIterable(new Limiting<>(new Actions(mActions, mEvent, new BaseActionFactory()), maxNumberOfItemsInTopBar),
                            new SmallEventActionView.Factory(mHorizontalActions)));

            mViews.schedjoulesEventDetailsDivider.setVisibility(View.VISIBLE);
            mHorizontalActions.setVisibility(View.VISIBLE);

            for (View view : new ActionViewIterable(new Skipping<>(new Actions(mActions, mEvent, new BaseActionFactory()), maxNumberOfItemsInTopBar),
                    new EventDetailsItemView.Factory(mVerticalItems)))
            {
                mVerticalItems.addView(view);
            }
        }
        else
        {
            // hide actions bar
            mViews.schedjoulesEventHorizontalActions.setVisibility(View.GONE);
        }
    }


    @Override
    public void onFeedbackMenuClick()
    {
        getView().post(new Runnable()
        {
            @Override
            public void run()
            {
                new ExternalUrlFeedbackForm().show(getActivity(), new FragmentEnvironment<>(EventDetailFragment.this).host());
            }
        });
    }
}