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
 */

package org.mqttbee.api.mqtt;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mqttbee.annotations.DoNotImplement;
import org.mqttbee.mqtt.MqttClientSslConfigImplBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author Christoph Schäbel
 */
@DoNotImplement
public interface MqttClientSslConfig {

    long DEFAULT_HANDSHAKE_TIMEOUT_MS = 10_000;

    static @NotNull MqttClientSslConfigBuilder builder() {
        return new MqttClientSslConfigImplBuilder.Default();
    }

    @Nullable KeyManagerFactory getKeyManagerFactory();

    @Nullable TrustManagerFactory getTrustManagerFactory();

    @Nullable ImmutableList<String> getCipherSuites();

    @Nullable ImmutableList<String> getProtocols();

    long getHandshakeTimeoutMs();
}
