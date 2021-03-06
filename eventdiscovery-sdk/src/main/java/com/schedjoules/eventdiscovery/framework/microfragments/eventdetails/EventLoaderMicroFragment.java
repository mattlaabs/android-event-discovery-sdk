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

package com.schedjoules.eventdiscovery.framework.microfragments.eventdetails;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schedjoules.client.eventsdiscovery.Category;
import com.schedjoules.client.eventsdiscovery.Event;
import com.schedjoules.client.eventsdiscovery.queries.CategoriesQuery;
import com.schedjoules.client.eventsdiscovery.queries.EventByUid;
import com.schedjoules.eventdiscovery.R;
import com.schedjoules.eventdiscovery.framework.common.BaseFragment;
import com.schedjoules.eventdiscovery.framework.common.FluentContextState;
import com.schedjoules.eventdiscovery.framework.model.category.EagerCategories;
import com.schedjoules.eventdiscovery.framework.serialization.Keys;
import com.schedjoules.eventdiscovery.framework.serialization.boxes.CategoriesBox;
import com.schedjoules.eventdiscovery.framework.services.ActionService;
import com.schedjoules.eventdiscovery.framework.utils.ServiceJob;
import com.schedjoules.eventdiscovery.framework.utils.ServiceJobQueue;
import com.schedjoules.eventdiscovery.framework.utils.SimpleServiceJobQueue;
import com.schedjoules.eventdiscovery.framework.utils.anims.Revealed;
import com.schedjoules.eventdiscovery.service.ApiService;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import org.dmfs.android.microfragments.FragmentEnvironment;
import org.dmfs.android.microfragments.MicroFragment;
import org.dmfs.android.microfragments.MicroFragmentHost;
import org.dmfs.android.microfragments.Timestamp;
import org.dmfs.android.microfragments.timestamps.UiTimestamp;
import org.dmfs.android.microfragments.transitions.ForwardTransition;
import org.dmfs.android.microfragments.transitions.FragmentTransition;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.types.Link;
import org.dmfs.httpessentials.types.StringToken;
import org.dmfs.optional.Optional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


/**
 * A {@link MicroFragment} that loads an event given by UID.
 */
public final class EventLoaderMicroFragment implements MicroFragment<String>
{
    private static final String TAG = "EventLoaderMF";

    public final static Creator<EventLoaderMicroFragment> CREATOR = new Creator<EventLoaderMicroFragment>()
    {
        @Override
        public EventLoaderMicroFragment createFromParcel(Parcel source)
        {
            return new EventLoaderMicroFragment(source.readString());
        }


        @Override
        public EventLoaderMicroFragment[] newArray(int size)
        {
            return new EventLoaderMicroFragment[size];
        }
    };
    private final String mEventUid;


    public EventLoaderMicroFragment(String eventUid)
    {
        mEventUid = eventUid;
    }


    @NonNull
    @Override
    public String title(@NonNull Context context)
    {
        return "Loading …";
    }


    @NonNull
    @Override
    public Fragment fragment(@NonNull Context context, MicroFragmentHost host)
    {
        return new LoaderFragment();
    }


    @NonNull
    @Override
    public String parameter()
    {
        return mEventUid;
    }


    @Override
    public boolean skipOnBack()
    {
        return true;
    }


    @Override
    public int describeContents()
    {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mEventUid);
    }


    public final static class LoaderFragment extends BaseFragment
    {
        private final Timestamp mTimestamp = new UiTimestamp();
        private ServiceJobQueue<ActionService> mActionServiceJobQueue;
        private ServiceJobQueue<ApiService> mApiServiceJobQueue;
        private String mEventUid;

        private AtomicReference<Event> mEvent = new AtomicReference<>();
        private AtomicReference<List<Link>> mActions = new AtomicReference<>();
        private AtomicBoolean mCategoriesLoaded = new AtomicBoolean();


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            mActionServiceJobQueue = new SimpleServiceJobQueue<>(new ActionService.FutureConnection(getActivity()));
            mApiServiceJobQueue = new SimpleServiceJobQueue<>(new ApiService.FutureConnection(getActivity()));
            mEventUid = new FragmentEnvironment<String>(this).microFragment().parameter();
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
            setStatusBarCoverEnabled(true);
            View view = inflater.inflate(R.layout.schedjoules_fragment_event_details_loader, container, false);
            view.findViewById(android.R.id.message).animate().setStartDelay(1500).alpha(1).start();
            ((CollapsingToolbarLayout) view.findViewById(R.id.schedjoules_event_detail_toolbar_layout)).setTitle("Loading event …");

            return view;
        }


        @Override
        public void onResume()
        {
            super.onResume();
            mApiServiceJobQueue.post(
                    new ServiceJob<ApiService>()
                    {
                        @Override
                        public void execute(ApiService service)
                        {
                            try
                            {
                                Optional<Event> optEvent = service.apiResponse(new EventByUid(new StringToken(mEventUid))).payload();
                                if (!optEvent.isPresent())
                                {
                                    throw new RuntimeException("Response doesn't have Event payload.");
                                }
                                mEvent.set(optEvent.value());
                                loaderReady();
                            }
                            catch (URISyntaxException | ProtocolError | ProtocolException | IOException | RuntimeException e)
                            {
                                Log.e(TAG, "Failed to load event", e);
                                onError();
                            }
                        }


                        @Override
                        public void onTimeOut()
                        {
                            onError();
                        }
                    }, 5000);

            mActionServiceJobQueue.post(
                    new ServiceJob<ActionService>()
                    {
                        @Override
                        public void execute(ActionService service)
                        {
                            try
                            {
                                mActions.set(service.actions(mEventUid));
                            }
                            catch (TimeoutException | InterruptedException | ProtocolError | IOException | ProtocolException | URISyntaxException | RuntimeException e)
                            {
                                // TODO: remove RuntimeException when https://github.com/dmfs/http-client-essentials-suite/issues/36 is fixed
                                // actions could not be loaded - fall back to an empty list
                                Log.e(TAG, "Failed to load actions", e);
                                mActions.set(Collections.<Link>emptyList());
                            }
                            loaderReady();
                        }


                        @Override
                        public void onTimeOut()
                        {
                            onError();
                        }
                    }, 5000
            );

            mApiServiceJobQueue.post(new ServiceJob<ApiService>()
            {
                @Override
                public void execute(ApiService service)
                {
                    try
                    {
                        Iterable<Category> categories = service.apiResponse(new CategoriesQuery());

                        new FluentContextState(getActivity()).put(Keys.CATEGORIES, new CategoriesBox(new EagerCategories(categories)));

                        mCategoriesLoaded.set(true);
                        loaderReady();
                    }
                    catch (ProtocolError | IOException | ProtocolException | URISyntaxException | RuntimeException e)
                    {
                        Log.e(TAG, "Failed to load category labels", e);
                        onError();
                    }
                }


                @Override
                public void onTimeOut()
                {
                    onError();
                }
            }, 5000);
        }


        private void onError()
        {
            startTransition(new Revealed(new ForwardTransition(new ErrorMicroFragment(), mTimestamp)));
        }


        @Override
        public void onDestroy()
        {
            mApiServiceJobQueue.disconnect();
            mActionServiceJobQueue.disconnect();
            super.onDestroy();
        }


        private void loaderReady()
        {
            if (mEvent.get() != null && mActions.get() != null && mCategoriesLoaded.get())
            {
                startTransition(new Revealed(new ForwardTransition(new ShowEventMicroFragment(mEvent.get(), mActions.get()), mTimestamp)));
            }
        }


        private void startTransition(FragmentTransition fragmentTransition)
        {
            if (isResumed())
            {
                new FragmentEnvironment<>(this).host().execute(getActivity(), fragmentTransition);
            }
        }

    }
}
