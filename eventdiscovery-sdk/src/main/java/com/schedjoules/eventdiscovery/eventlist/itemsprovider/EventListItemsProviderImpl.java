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

package com.schedjoules.eventdiscovery.eventlist.itemsprovider;

import android.util.Log;

import com.schedjoules.client.ApiQuery;
import com.schedjoules.client.eventsdiscovery.Envelope;
import com.schedjoules.client.eventsdiscovery.Event;
import com.schedjoules.client.eventsdiscovery.GeoLocation;
import com.schedjoules.client.eventsdiscovery.ResultPage;
import com.schedjoules.eventdiscovery.eventlist.items.ErrorItem;
import com.schedjoules.eventdiscovery.eventlist.items.LoadingIndicatorItem;
import com.schedjoules.eventdiscovery.eventlist.items.NoMoreEventsItem;
import com.schedjoules.eventdiscovery.eventlist.itemsprovider.EventListDownloadTask.TaskParam;
import com.schedjoules.eventdiscovery.eventlist.itemsprovider.EventListDownloadTask.TaskResult;
import com.schedjoules.eventdiscovery.eventlist.view.EventListBackgroundMessage;
import com.schedjoules.eventdiscovery.eventlist.view.EventListLoadingIndicatorOverlay;
import com.schedjoules.eventdiscovery.framework.adapter.ListItemsProvider;
import com.schedjoules.eventdiscovery.framework.async.SafeAsyncTaskResult;
import com.schedjoules.eventdiscovery.model.UndefinedGeoLocation;
import com.schedjoules.eventdiscovery.service.ApiService;
import com.schedjoules.eventdiscovery.utils.Objects;
import com.smoothsync.smoothsetup.services.FutureServiceConnection;

import org.dmfs.rfc5545.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.schedjoules.eventdiscovery.eventlist.itemsprovider.ScrollDirection.BOTTOM;
import static com.schedjoules.eventdiscovery.eventlist.itemsprovider.ScrollDirection.TOP;


/**
 * {@link ListItemsProvider} for the Event list.
 *
 * @author Gabor Keszthelyi
 */
public class EventListItemsProviderImpl implements EventListItemsProvider, EventListBackgroundMessage.OnClickListener
{
    public static final int CLOSE_TO_TOP_OR_BOTTOM_THRESHOLD = 30;

    private static final String TAG = EventListItemsProviderImpl.class.getSimpleName();

    private final FutureServiceConnection<ApiService> mApiService;

    private final ExecutorService mExecutorService;

    private final EventListBackgroundMessage mBackgroundMessage;

    private final EventListItems mItems;
    private final EventListLoadingIndicatorOverlay mLoadingIndicatorOverlay;

    private final EventPageQueries mPageQueries;

    private GeoLocation mLocation;
    private Map<ScrollDirection, Boolean> mIsLoading;

    private DownloadTaskClient mDownloadTaskClient;

    private Map<ScrollDirection, Boolean> mIsInErrorMode;
    private Map<ScrollDirection, TaskParam> mErrorTaskParam;


    public EventListItemsProviderImpl(FutureServiceConnection<ApiService> apiService, EventListItems items, EventListBackgroundMessage backgroundMessage, EventListLoadingIndicatorOverlay loadingIndicatorOverlay)
    {
        mApiService = apiService;
        mBackgroundMessage = backgroundMessage;
        mItems = items;
        mLoadingIndicatorOverlay = loadingIndicatorOverlay;
        mBackgroundMessage.setOnClickListener(this);
        mExecutorService = Executors.newSingleThreadExecutor();
        mDownloadTaskClient = new DownloadTaskClient();
        mErrorTaskParam = new HashMap<>(2);

        mIsInErrorMode = new HashMap<>(2);
        mIsInErrorMode.put(TOP, false);
        mIsInErrorMode.put(BOTTOM, false);

        mIsLoading = new HashMap<>(2);
        mIsLoading.put(TOP, false);
        mIsLoading.put(BOTTOM, false);

        mPageQueries = new EventPageQueries();
    }


    @Override
    public void loadEvents(GeoLocation geoLocation, DateTime dateTime)
    {
        clearEverything();

        // TODO maybe use a transparent GeoLocation proxy object, or use NoLocationSelected, or ...
        mLocation = geoLocation == null ? UndefinedGeoLocation.INSTANCE : geoLocation;

        queueDownloadTask(new InitialEventsDiscovery(dateTime, mLocation), BOTTOM);
    }


    @Override
    public void setAdapterNotifier(AdapterNotifier adapterNotifier)
    {
        mItems.setAdapterNotifier(adapterNotifier);
    }


    private void queueDownloadTask(ApiQuery<ResultPage<Envelope<Event>>> query, ScrollDirection scrollDirection)
    {
        //noinspection unchecked
        new EventListDownloadTask(new TaskParam(mLocation, query, scrollDirection), mDownloadTaskClient)
                .executeOnExecutor(mExecutorService, mApiService);
    }


    private void queueDownloadTask(TaskParam taskParam)
    {
        //noinspection unchecked
        new EventListDownloadTask(taskParam, mDownloadTaskClient).executeOnExecutor(mExecutorService, mApiService);
    }


    private void queueComingPage(ScrollDirection direction)
    {
        if (mPageQueries.hasComingPage(direction))
        {
            queueDownloadTask(mPageQueries.comingPageQuery(direction), direction);
        }
    }


    @Override
    public void onBackgroundMessageClick()
    {
        onScrolledCloseToEdge(BOTTOM);
    }


    @Override
    public void onScrolledCloseToEdge(ScrollDirection direction)
    {
        if (!mIsLoading.get(direction))
        {
            if (mIsInErrorMode.get(direction))
            {
                queueDownloadTask(mErrorTaskParam.get(direction));
            }
            else
            {
                queueComingPage(direction);
            }
        }
    }


    private void clearEverything()
    {
        mItems.clear();

        mIsInErrorMode.put(TOP, false);
        mIsInErrorMode.put(BOTTOM, false);
        mIsLoading.put(TOP, false);
        mIsLoading.put(BOTTOM, false);
        mErrorTaskParam.clear();
        mPageQueries.clear();
    }


    private void markLoadStarted(boolean isEmpty, ScrollDirection direction)
    {
        mIsLoading.put(direction, true);
        if (isEmpty)
        {
            mLoadingIndicatorOverlay.show();
        }
        else
        {
            mItems.addSpecialItemPost(LoadingIndicatorItem.get(direction), direction);
        }
    }


    private void markLoadFinishedSuccess(boolean isEmptyBefore, boolean isEmptyAfter, ScrollDirection direction)
    {
        mIsLoading.put(direction, false);

        // Hide loading:
        if (isEmptyBefore)
        {
            mLoadingIndicatorOverlay.hide();
            mBackgroundMessage.hide();
        }
        else
        {
            mItems.removeSpecialItem(LoadingIndicatorItem.get(direction), direction);
        }

        // Resuming from error mode:
        if (mIsInErrorMode.get(direction))
        {
            if (isEmptyBefore)
            {
                mBackgroundMessage.hide();
            }
            else
            {
                mItems.removeSpecialItem(ErrorItem.get(direction), direction);
            }
            mIsInErrorMode.put(direction, false);
        }

        // No more events message:
        if (isEmptyAfter)
        {
            mBackgroundMessage.showNoEventsFoundMsg();
        }
        else if (!mPageQueries.hasComingPage(direction)
                && (direction == BOTTOM || (direction == TOP && !mItems.isTodayShown())))
        {
            mItems.addSpecialItemNow(NoMoreEventsItem.get(direction), direction);
        }
    }


    private void markLoadFinishedError(boolean isEmpty, ScrollDirection direction)
    {
        mIsLoading.put(direction, false);

        // Hide loading:
        if (isEmpty)
        {
            mLoadingIndicatorOverlay.hide();
        }
        else
        {
            mItems.removeSpecialItem(LoadingIndicatorItem.get(direction), direction);
        }

        if (!mIsInErrorMode.get(direction))
        {
            if (isEmpty)
            {
                mBackgroundMessage.showErrorMsg();
            }
            else
            {
                mItems.addSpecialItemNow(ErrorItem.get(direction), direction);
            }
            mIsInErrorMode.put(direction, true);
        }
    }


    private class DownloadTaskClient implements EventListDownloadTask.Client
    {

        @Override
        public void onPreExecute(TaskParam taskParam)
        {
            markLoadStarted(mItems.isEmpty(), taskParam.mDirection);
        }


        // Called from both background and main thread
        @Override
        public boolean shouldDiscard(TaskParam taskParam)
        {
            return !Objects.equals(taskParam.mRequestLocation, mLocation)
                    ||
                    (mIsInErrorMode.get(taskParam.mDirection)
                            && !taskParam.equals(mErrorTaskParam.get(taskParam.mDirection)));
        }


        @Override
        public void onTaskFinish(SafeAsyncTaskResult<TaskResult> result, TaskParam taskParam)
        {
            try
            {
                onTaskSuccess(result.value(), taskParam);
            }
            catch (Exception e)
            {
                onTaskError(taskParam, e);
            }
        }


        private void onTaskSuccess(TaskResult result, TaskParam taskParam)
        {
            mPageQueries.saveLastResult(result.mResultPage, taskParam.mDirection);

            boolean isEmptyBefore = mItems.isEmpty();
            mItems.mergeNewItems(result.mListItems, taskParam.mDirection);

            markLoadFinishedSuccess(isEmptyBefore, mItems.isEmpty(), taskParam.mDirection);

            if (taskParam.mQuery instanceof InitialEventsDiscovery)
            {
                mPageQueries.saveLastResult(result.mResultPage, TOP);
                queueComingPage(TOP);
            }
        }


        private void onTaskError(TaskParam taskParam, Exception e)
        {
            mErrorTaskParam.put(taskParam.mDirection, taskParam);
            Log.e(TAG, "Error during download task", e);
            markLoadFinishedError(mItems.isEmpty(), taskParam.mDirection);
        }
    }
}
