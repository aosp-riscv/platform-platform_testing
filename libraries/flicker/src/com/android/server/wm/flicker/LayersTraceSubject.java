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

package com.android.server.wm.flicker;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertWithMessage;

import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.server.wm.flicker.Assertions.Result;
import com.android.server.wm.flicker.LayersTrace.Entry;
import com.android.server.wm.flicker.TransitionRunner.TransitionResult;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Truth subject for {@link LayersTrace} objects. */
public class LayersTraceSubject extends Subject<LayersTraceSubject, LayersTrace> {
    public static final String TAG = "FLICKER";

    // Boiler-plate Subject.Factory for LayersTraceSubject
    private static final Subject.Factory<LayersTraceSubject, LayersTrace> FACTORY =
            LayersTraceSubject::new;

    private AssertionsChecker<Entry> mChecker = new AssertionsChecker<>();
    private boolean mNewAssertion = true;

    private void addAssertion(Assertions.TraceAssertion<Entry> assertion, String name) {
        if (mNewAssertion) {
            mChecker.add(assertion, name);
        } else {
            mChecker.append(assertion, name);
        }
    }

    private LayersTraceSubject(FailureMetadata fm, @Nullable LayersTrace subject) {
        super(fm, subject);
    }

    // User-defined entry point
    public static LayersTraceSubject assertThat(@Nullable LayersTrace entry) {
        return assertAbout(FACTORY).that(entry);
    }

    // User-defined entry point. Ignores orphaned layers because of b/141326137
    public static LayersTraceSubject assertThat(@Nullable TransitionResult result) {
        Consumer<LayersTrace.Layer> orphanLayerCallback =
                layer ->
                        Log.w(
                                TAG,
                                String.format(
                                        Locale.getDefault(), "Ignoring orphaned layer %s", layer));

        return assertThat(result, orphanLayerCallback);
    }

    // User-defined entry point
    public static LayersTraceSubject assertThat(@Nullable TransitionResult result,
            Consumer<LayersTrace.Layer> orphanLayerCallback) {
        LayersTrace entries =
                LayersTrace.parseFrom(
                        result.getLayersTrace(),
                        result.getLayersTracePath(),
                        result.getLayersTraceChecksum(),
                        orphanLayerCallback);
        return assertWithMessage(result.toString()).about(FACTORY).that(entries);
    }

    // Static method for getting the subject factory (for use with assertAbout())
    public static Subject.Factory<LayersTraceSubject, LayersTrace> entries() {
        return FACTORY;
    }

    public void forAllEntries() {
        test();
    }

    public void forRange(long startTime, long endTime) {
        mChecker.filterByRange(startTime, endTime);
        test();
    }

    public LayersTraceSubject then() {
        mNewAssertion = true;
        mChecker.checkChangingAssertions();
        return this;
    }

    public LayersTraceSubject and() {
        mNewAssertion = false;
        mChecker.checkChangingAssertions();
        return this;
    }

    /**
     * Ignores the first entries in the trace, until the first assertion passes. If it reaches the
     * end of the trace without passing any assertion, return a failure with the name/reason from
     * the first assertion
     *
     * @return
     */
    public LayersTraceSubject skipUntilFirstAssertion() {
        mChecker.skipUntilFirstAssertion();
        return this;
    }

    public void inTheBeginning() {
        if (actual().getEntries().isEmpty()) {
            fail("No entries found.");
        }
        mChecker.checkFirstEntry();
        test();
    }

    public void atTheEnd() {
        if (actual().getEntries().isEmpty()) {
            fail("No entries found.");
        }
        mChecker.checkLastEntry();
        test();
    }

    private void test() {
        List<Result> failures = mChecker.test(actual().getEntries());
        if (!failures.isEmpty()) {
            String failureLogs =
                    failures.stream().map(Result::toString).collect(Collectors.joining("\n"));
            String tracePath = "";
            if (actual().getSource().isPresent()) {
                tracePath =
                        "\nLayers Trace can be found in: "
                                + actual().getSource().get().toAbsolutePath()
                                + "\nChecksum: "
                                + actual().getSourceChecksum()
                                + "\n";
            }
            fail(tracePath + failureLogs);
        }
    }

    public LayersTraceSubject coversRegion(Rect rect) {
        addAssertion(entry -> entry.coversRegion(rect), "coversRegion(" + rect + ")");
        return this;
    }

    public LayersTraceSubject hasVisibleRegion(String layerName, Rect size) {
        addAssertion(
                entry -> entry.hasVisibleRegion(layerName, size),
                "hasVisibleRegion(" + layerName + size + ")");
        return this;
    }

    public LayersTraceSubject hasNotLayer(String layerName) {
        addAssertion(entry -> entry.exists(layerName).negate(), "hasNotLayer(" + layerName + ")");
        return this;
    }

    public LayersTraceSubject hasLayer(String layerName) {
        addAssertion(entry -> entry.exists(layerName), "hasLayer(" + layerName + ")");
        return this;
    }

    public LayersTraceSubject showsLayer(String layerName) {
        addAssertion(entry -> entry.isVisible(layerName), "showsLayer(" + layerName + ")");
        return this;
    }

    public LayersTraceSubject replaceVisibleLayer(
            String previousLayerName, String currentLayerName) {
        return hidesLayer(previousLayerName).and().showsLayer(currentLayerName);
    }

    public LayersTraceSubject hidesLayer(String layerName) {
        addAssertion(entry -> entry.isVisible(layerName).negate(), "hidesLayer(" + layerName + ")");
        return this;
    }

}
