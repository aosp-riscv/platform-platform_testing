/*
 * Copyright (C) 2021 The Android Open Source Project
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
package android.platform.test.scenario.mediacenter.testmedia;

import android.platform.helpers.HelperAccessor;
import android.platform.helpers.IAutoGenericAppHelper;
import android.platform.helpers.IAutoMediaCenterHelper;
import android.platform.test.scenario.annotation.Scenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Opens the Test Media App application by launching media center. */
@Scenario
@RunWith(JUnit4.class)
public class OpenApp {

    private static final String MEDIA_CENTER_PACKAGE = "com.android.car.media";
    private static final String MEDIA_TEMPLATE = "android.car.intent.action.MEDIA_TEMPLATE";
    private static final Map<String, String> TEST_MEDIA_APP_EXTRA_ARGS =
            Stream.of(
                            new Object[][] {
                                {
                                    "android.car.intent.extra.MEDIA_COMPONENT",
                                    "com.android.car.media.testmediaapp/com.android.car.media.testmediaapp.TmaBrowser"
                                },
                            })
                    .collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));

    static HelperAccessor<IAutoGenericAppHelper> sAutoGenericrHelper =
            new HelperAccessor<>(IAutoGenericAppHelper.class);

    static HelperAccessor<IAutoMediaCenterHelper> sMediaCenterHelper =
            new HelperAccessor<>(IAutoMediaCenterHelper.class);

    static {
        sAutoGenericrHelper.get().setPackage(MEDIA_CENTER_PACKAGE);
        sAutoGenericrHelper.get().setLaunchAction(MEDIA_TEMPLATE, TEST_MEDIA_APP_EXTRA_ARGS);
    }

    @Test
    public void Open() {
        sAutoGenericrHelper.get().open();
    }
}
