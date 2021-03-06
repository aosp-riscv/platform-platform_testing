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
package android.platform.test.scenario.dial;

import android.platform.helpers.HelperAccessor;
import android.platform.helpers.IAutoGenericAppHelper;
import android.platform.test.scenario.annotation.Scenario;
import android.platform.helpers.IAutoDialHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Scroll down and up in contacts screen of dialer app */
@Scenario
@RunWith(JUnit4.class)
public class ScrollContactList {
    private static final String DIALER_PACKAGE = "com.android.car.dialer";

    static HelperAccessor<IAutoGenericAppHelper> sAutoGenericHelper =
            new HelperAccessor<>(IAutoGenericAppHelper.class);

    static {
        sAutoGenericHelper.get().setPackage(DIALER_PACKAGE);
        sAutoGenericHelper.get().setScrollableMargin(0, 200, 0, 200);
    }

    static HelperAccessor<IAutoDialHelper> sHelper = new HelperAccessor<>(IAutoDialHelper.class);

    @Test
    public void testScrollDownAndUp() {
        sHelper.get().openContacts();
        // test scroll down by one page in 500ms.
        sAutoGenericHelper.get().scrollDownOnePage(500);
        // test scroll up by one page in 500ms.
        sAutoGenericHelper.get().scrollUpOnePage(500);
    }
}
