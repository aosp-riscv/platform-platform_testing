/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.server.wm.flicker.monitor;

import android.os.RemoteException;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;

import java.nio.file.Path;

/** Captures Layers trace from SurfaceFlinger. */
public class LayersTraceMonitor extends TraceMonitor {
    private static final String TRACE_FILE = "layers_trace.pb";
    private IWindowManager mWm = WindowManagerGlobal.getWindowManagerService();

    public LayersTraceMonitor() {
        this(OUTPUT_DIR);
    }

    public LayersTraceMonitor(Path outputDir) {
        super(outputDir, TRACE_FILE);
    }

    @Override
    public void start() {
        setEnabled(true);
    }

    @Override
    public void stop() {
        setEnabled(false);
    }

    @Override
    public boolean isEnabled() {
        try {
            return mWm.isLayerTracing();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setEnabled(boolean isEnabled) {
        try {
            mWm.setLayerTracing(isEnabled);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
