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

package com.majeur.preferencekit;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class PreferenceFragment extends android.preference.PreferenceFragment {

    private int mActivityDecorColor = -1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof PreferenceHeadersActivity) {
            mActivityDecorColor = ((PreferenceHeadersActivity) activity).getDecorColor();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = (ListView) view.findViewById(android.R.id.list);
        if (listView != null)
            listView.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof PreferenceHeadersActivity) {
            int color = ((PreferenceHeadersActivity) getActivity()).getDecorColor();

            if (color != mActivityDecorColor) {
                mActivityDecorColor = color;
                ((BaseAdapter) getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
            }
        }
    }
}
