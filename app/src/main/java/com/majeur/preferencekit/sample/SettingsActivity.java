package com.majeur.preferencekit.sample;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.majeur.preferencekit.CheckBoxPreference;
import com.majeur.preferencekit.DialogPreference;
import com.majeur.preferencekit.Preference;
import com.majeur.preferencekit.ExtraButtonsPreference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            addPreferencesFromResource(R.xml.preferences);

            ExtraButtonsPreference buttonsPreference = (ExtraButtonsPreference) findPreference("extrabuttons");
            buttonsPreference.setOnExtraButtonClickListener(new ExtraButtonsPreference.OnExtraButtonClickListener() {
                @Override
                public void onExtraButtonClick(int which) {
                    Toast.makeText(getActivity(), "Clicked " + which, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            menu.add(0, 0, 0, "Lock");
        }

        private boolean aBoolean = true;

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == 0) {
                for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                    android.preference.Preference p = getPreferenceScreen().getPreference(i);

                    if (p instanceof Preference)
                        ((Preference) p).setLocked(aBoolean);
                    else if (p instanceof DialogPreference)
                        ((DialogPreference) p).setLocked(aBoolean);
                    else if (p instanceof CheckBoxPreference)
                        ((CheckBoxPreference) p).setLocked(aBoolean);
                }
                aBoolean = !aBoolean;
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    }
}
