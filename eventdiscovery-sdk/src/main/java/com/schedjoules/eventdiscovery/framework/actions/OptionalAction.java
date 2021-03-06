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

package com.schedjoules.eventdiscovery.framework.actions;

import com.schedjoules.client.eventsdiscovery.Event;
import com.schedjoules.eventdiscovery.framework.utils.factory.Factory;
import com.schedjoules.eventdiscovery.framework.utils.optionals.AbstractCachingOptional;

import org.dmfs.httpessentials.types.Link;
import org.dmfs.iterators.Filter;
import org.dmfs.iterators.Function;
import org.dmfs.iterators.decorators.Filtered;
import org.dmfs.iterators.decorators.Mapped;
import org.dmfs.optional.Next;
import org.dmfs.optional.Optional;


/**
 * {@link Optional<Action>} that looks for the {@link Action} in a provided {@link Iterable<Action>} by link rel type.
 *
 * @author Gabor Keszthelyi
 */
public final class OptionalAction extends AbstractCachingOptional<Action>
{

    public OptionalAction(final String actionLinkRelType, final Iterable<Link> actionLinks, final ActionFactory actionFactory, final Event event)
    {
        super(new Factory<Optional<Action>>()
        {
            @Override
            public Optional<Action> create()
            {
                return new Next<>(
                        new Mapped<>(
                                new Filtered<>(
                                        actionLinks.iterator(),
                                        new Filter<Link>()
                                        {
                                            @Override
                                            public boolean iterate(Link link)
                                            {
                                                return link.relationTypes().contains(actionLinkRelType);
                                            }
                                        }),
                                new Function<Link, Action>()
                                {
                                    @Override
                                    public Action apply(Link element)
                                    {
                                        return actionFactory.action(element, event);
                                    }
                                }));

            }
        });
    }

}
