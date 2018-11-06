/*
 * Copyright 2018 The MQTT Bee project
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
 *
 */

package org.mqttbee.mqtt.message.subscribe;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.api.mqtt.datatypes.MqttQos;
import org.mqttbee.api.mqtt.datatypes.MqttTopicFilter;
import org.mqttbee.api.mqtt.datatypes.MqttTopicFilterBuilder;
import org.mqttbee.api.mqtt.mqtt5.datatypes.Mqtt5UserProperties;
import org.mqttbee.api.mqtt.mqtt5.datatypes.Mqtt5UserPropertiesBuilder;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.Mqtt5RetainHandling;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.Mqtt5SubscribeBuilder;
import org.mqttbee.api.mqtt.mqtt5.message.subscribe.Mqtt5Subscription;
import org.mqttbee.mqtt.datatypes.MqttUserPropertiesImpl;
import org.mqttbee.mqtt.util.MqttBuilderUtil;
import org.mqttbee.util.MustNotBeImplementedUtil;

import java.util.function.Function;

/**
 * @author Silvio Giebl
 */
public abstract class MqttSubscribeBuilder<B extends MqttSubscribeBuilder<B>> {

    private final @NotNull ImmutableList.Builder<MqttSubscription> subscriptionsBuilder;
    private @NotNull MqttUserPropertiesImpl userProperties = MqttUserPropertiesImpl.NO_USER_PROPERTIES;
    private @Nullable MqttSubscriptionBuilder.Default firstSubscriptionBuilder;

    protected MqttSubscribeBuilder() {
        subscriptionsBuilder = ImmutableList.builder();
    }

    MqttSubscribeBuilder(final @NotNull Mqtt5Subscribe subscribe) {
        final MqttSubscribe subscribeImpl =
                MustNotBeImplementedUtil.checkNotImplemented(subscribe, MqttSubscribe.class);
        final ImmutableList<MqttSubscription> subscriptions = subscribeImpl.getSubscriptions();
        subscriptionsBuilder = ImmutableList.builderWithExpectedSize(subscriptions.size() + 1);
        subscriptionsBuilder.addAll(subscriptions);
    }

    protected abstract @NotNull B self();

    public @NotNull B addSubscription(final @NotNull Mqtt5Subscription subscription) {
        buildFirstSubscription();
        subscriptionsBuilder.add(MustNotBeImplementedUtil.checkNotImplemented(subscription, MqttSubscription.class));
        return self();
    }

    public @NotNull MqttSubscriptionBuilder.Nested<B> addSubscription() {
        return new MqttSubscriptionBuilder.Nested<>(this::addSubscription);
    }

    public @NotNull B userProperties(final @NotNull Mqtt5UserProperties userProperties) {
        this.userProperties = MqttBuilderUtil.userProperties(userProperties);
        return self();
    }

    private @NotNull MqttSubscriptionBuilder.Default getFirstSubscriptionBuilder() {
        if (firstSubscriptionBuilder == null) {
            firstSubscriptionBuilder = new MqttSubscriptionBuilder.Default();
        }
        return firstSubscriptionBuilder;
    }

    private void buildFirstSubscription() {
        if (firstSubscriptionBuilder != null) {
            subscriptionsBuilder.add(firstSubscriptionBuilder.build());
            firstSubscriptionBuilder = null;
        }
    }

    public @NotNull Mqtt5UserPropertiesBuilder<B> userProperties() {
        return new Mqtt5UserPropertiesBuilder<>(this::userProperties);
    }

    public @NotNull B topicFilter(final @NotNull String topicFilter) {
        getFirstSubscriptionBuilder().topicFilter(topicFilter);
        return self();
    }

    public @NotNull B topicFilter(final @NotNull MqttTopicFilter topicFilter) {
        getFirstSubscriptionBuilder().topicFilter(topicFilter);
        return self();
    }

    public @NotNull MqttTopicFilterBuilder<B> topicFilter() {
        return new MqttTopicFilterBuilder<>(this::topicFilter);
    }

    public @NotNull B qos(final @NotNull MqttQos qos) {
        getFirstSubscriptionBuilder().qos(qos);
        return self();
    }

    public @NotNull B noLocal(final boolean noLocal) {
        getFirstSubscriptionBuilder().noLocal(noLocal);
        return self();
    }

    public @NotNull B retainHandling(final @NotNull Mqtt5RetainHandling retainHandling) {
        getFirstSubscriptionBuilder().retainHandling(retainHandling);
        return self();
    }

    public @NotNull B retainAsPublished(final boolean retainAsPublished) {
        getFirstSubscriptionBuilder().retainAsPublished(retainAsPublished);
        return self();
    }

    public @NotNull MqttSubscribe build() {
        buildFirstSubscription();
        final ImmutableList<MqttSubscription> subscriptions = subscriptionsBuilder.build();
        if (subscriptions.isEmpty()) {
            throw new IllegalStateException("At least one subscription must be added.");
        }
        return new MqttSubscribe(subscriptions, userProperties);
    }

    // @formatter:off
    public static class Default
            extends MqttSubscribeBuilder<Default>
            implements Mqtt5SubscribeBuilder.Complete,
                       Mqtt5SubscribeBuilder.Start.Complete {
    // @formatter:on

        public Default() {}

        public Default(final @NotNull Mqtt5Subscribe subscribe) {
            super(subscribe);
        }

        @Override
        protected @NotNull MqttSubscribeBuilder.Default self() {
            return this;
        }
    }

    // @formatter:off
    public static class Nested<P>
            extends MqttSubscribeBuilder<Nested<P>>
            implements Mqtt5SubscribeBuilder.Nested.Complete<P>,
                       Mqtt5SubscribeBuilder.Nested.Start.Complete<P> {
    // @formatter:on

        private final @NotNull Function<? super MqttSubscribe, P> parentConsumer;

        public Nested(final @NotNull Function<? super MqttSubscribe, P> parentConsumer) {
            this.parentConsumer = parentConsumer;
        }

        @Override
        protected @NotNull MqttSubscribeBuilder.Nested<P> self() {
            return this;
        }

        @Override
        public @NotNull P applySubscribe() {
            return parentConsumer.apply(build());
        }
    }

    // @formatter:off
    public static class Send<P>
            extends MqttSubscribeBuilder<Send<P>>
            implements Mqtt5SubscribeBuilder.Send.Complete<P>,
                       Mqtt5SubscribeBuilder.Send.Start.Complete<P> {
    // @formatter:on

        private final @NotNull Function<? super MqttSubscribe, P> parentConsumer;

        public Send(final @NotNull Function<? super MqttSubscribe, P> parentConsumer) {
            this.parentConsumer = parentConsumer;
        }

        @Override
        protected @NotNull MqttSubscribeBuilder.Send<P> self() {
            return this;
        }

        @Override
        public @NotNull P send() {
            return parentConsumer.apply(build());
        }
    }
}