/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm.traces.parser

import com.android.server.wm.traces.common.windowmanager.WindowManagerTrace
import com.android.server.wm.traces.parser.layers.LayersTrace
import com.android.server.wm.traces.parser.layers.LayerTraceEntry
import com.android.server.wm.traces.parser.windowmanager.WindowManagerTraceParser

/**
 * Represents a state dump containing the [WindowManagerTrace] and the [LayersTrace] both parsed
 * and in raw (byte) data.
 */
class DeviceStateDump(
    /**
     * [WindowManagerTrace] content
     */
    val wmTraceData: ByteArray,
    /**
     * [LayersTrace] content
     */
    val layersTraceData: ByteArray,
    /**
     * Predicate to parse [wmTraceData] into a [WindowManagerTrace]
     */
    val wmTraceParser: (ByteArray) -> WindowManagerTrace?,
    /**
     * Predicate to parse [layersTraceData] into a [LayersTrace]
     */
    val layersTraceParser: (ByteArray) -> LayersTrace?
) {
    /**
     * Parsed [WindowManagerTrace]
     */
    val wmTrace: WindowManagerTrace? by lazy { wmTraceParser(wmTraceData) }
    /**
     * Parsed [LayersTrace]
     */
    val layersTrace: LayersTrace? by lazy { layersTraceParser(layersTraceData) }

    companion object {
        /**
         * Creates a device state dump containing the [WindowManagerTrace] and [LayersTrace]
         * obtained from a `dumpsys` command. The parsed traces will contain a single
         * [WindowManagerTraceEntry] or [LayerTraceEntry].
         *
         * @param wmTraceData [WindowManagerTrace] content
         * @param layersTraceData [LayersTrace] content
         */
        @JvmStatic
        fun fromDump(wmTraceData: ByteArray, layersTraceData: ByteArray): DeviceStateDump {
            return DeviceStateDump(
                wmTraceData,
                layersTraceData,
                {
                    if (wmTraceData.isNotEmpty()) {
                        WindowManagerTraceParser.parseFromDump(wmTraceData)
                    } else {
                        null
                    }
                },
                {
                    if (layersTraceData.isNotEmpty()) {
                        LayersTrace.parseFromDump(layersTraceData)
                    } else {
                        null
                    }
                }
            )
        }

        /**
         * Creates a device state dump containing the WindowManager and Layers trace
         * obtained from a regular trace. The parsed traces may contain a multiple
         * [WindowManagerTraceEntry] or [LayerTraceEntry].
         *
         * @param wmTraceData [WindowManagerTrace] content
         * @param layersTraceData [LayersTrace] content
         */
        @JvmStatic
        fun fromTrace(wmTraceData: ByteArray, layersTraceData: ByteArray): DeviceStateDump {
            return DeviceStateDump(
                wmTraceData,
                layersTraceData,
                {
                    if (wmTraceData.isNotEmpty()) {
                        WindowManagerTraceParser.parseFromTrace(wmTraceData)
                    } else {
                        null
                    }
                },
                {
                    if (layersTraceData.isNotEmpty()) {
                        LayersTrace.parseFrom(layersTraceData)
                    } else {
                        null
                    }
                }
            )
        }
    }
}