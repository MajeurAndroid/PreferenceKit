package com.majeur.preferencekit.sample;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.majeur.preferencekit.CheckBoxPreference;
import com.majeur.preferencekit.DialogPreference;
import com.majeur.preferencekit.ExtraButtonsPreference;
import com.majeur.preferencekit.Preference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
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
                    Toast.makeText(getActivity(), "Button clicked: " + which, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.main, menu);
        }

        private boolean mPreferencesLocked = true;

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_lock:
                    for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                        android.preference.Preference p = getPreferenceScreen().getPreference(i);

                        if (p instanceof Preference)
                            ((Preference) p).setLocked(mPreferencesLocked);
                        else if (p instanceof DialogPreference)
                            ((DialogPreference) p).setLocked(mPreferencesLocked);
                        else if (p instanceof CheckBoxPreference)
                            ((CheckBoxPreference) p).setLocked(mPreferencesLocked);
                    }
                    mPreferencesLocked = !mPreferencesLocked;
                    return true;

                case R.id.action_about:
                    showAboutDialog();
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void showAboutDialog() {
            new AlertDialog.Builder(getActivity())
                    .setTitle("About")
                    .setView(getActivity().getLayoutInflater().inflate(R.layout.about_dialog_message, null))
                    .setNegativeButton(android.R.string.ok, null)
                    .show();
        }
    }
}
