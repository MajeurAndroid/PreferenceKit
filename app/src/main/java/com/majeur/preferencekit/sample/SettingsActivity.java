/*
 *  Copyright 2016 MajeurAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.majeur.preferencekit.sample;

import android.graphics.Color;
import android.os.Bundle;

import com.majeur.preferencekit.PreferenceHeadersActivity;

public class SettingsActivity extends PreferenceHeadersActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addHeadersFromResources(R.xml.headers);

        addHeaderAt(Header.createHeaderRow("Prime version",
                "Please buy prime to let me make some money",
                R.drawable.ic_pref_general,
                false,
                null,
                0,
                Color.RED), 0);

        addHeaderAt(Header.createHeaderRow("Not set as deault",
                "Click to set solid as default launcher",
                R.drawable.abc_ic_search_api_mtrl_alpha,
                false,
                null,
                0,
                Color.CYAN), 1);
    }

    @Override
    protected String onGetDefaultTitle() {
        return getString(R.string.app_name);
    }
}
