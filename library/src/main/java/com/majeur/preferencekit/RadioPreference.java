package com.majeur.preferencekit;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Arrays;

/**
 * Preference that shows multiple single choice items in form of radio buttons.
 */
public class RadioPreference extends Preference {

    /**
     * @see SpinnerPreference Same implementation
     */
    private static final String STRING_DEFAULT = "str_default_none";

    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private CharSequence mValue;
    private RadioGroup mContainer;
    private LayoutInflater mInflater;

    public RadioPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RadioPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RadioPreference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.preference_radio, 0, 0);
            mEntries = typedArray.getTextArray(R.styleable.preference_radio_entries);
            mEntryValues = typedArray.getTextArray(R.styleable.preference_radio_entryValues);
            typedArray.recycle();
        }

        mInflater = LayoutInflater.from(context);
    }

    /**
     * Set manually arrays resources. They both must contains elements.
     *
     * @param entriesResId     Resource Id of Entries
     * @param entryValuesResId Resource Id of EntryValues
     */
    public void setEntryResourceIds(int entriesResId, int entryValuesResId) {
        Resources resources = getContext().getResources();
        mEntries = resources.getTextArray(entriesResId);
        mEntryValues = resources.getTextArray(entryValuesResId);

        if (Utils.isArrayEmpty(mEntries) || Utils.isArrayEmpty(mEntryValues))
            throw new IllegalStateException("entries and entryValues must be set, and must have at least one item.");

        setRadioButtons();
        setNewValue(mEntryValues[0].toString());
    }

    /**
     * @see SpinnerPreference Same implementation of {@link #STRING_DEFAULT}
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        String value = a.getString(index);
        return (value == null) ? STRING_DEFAULT : value;
    }

    /**
     * @see SpinnerPreference Same implementation of {@link #STRING_DEFAULT}
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (Utils.isArrayEmpty(mEntries) || Utils.isArrayEmpty(mEntryValues))
            throw new IllegalStateException("entries and entryValues must be set.");

        if (STRING_DEFAULT.equals(defaultValue))
            defaultValue = mEntryValues[0].toString();

        String value = restorePersistedValue ? getPersistedString(mEntryValues[0].toString()) : (String) defaultValue;
        setNewValue(value);
    }

    /**
     * Called to commit value change
     *
     * @param newValue New value
     */
    private void setNewValue(String newValue) {

        if (isPersistent())
            persistString(newValue);

        if (getOnPreferenceChangeListener() != null)
            getOnPreferenceChangeListener().onPreferenceChange(this, newValue);

        final int newPosition = Arrays.asList(mEntryValues).indexOf(newValue);
        int radioId = intToRadioId(newPosition);

        if (mContainer != null && mContainer.getCheckedRadioButtonId() != radioId)
            mContainer.check(radioId);

        mValue = newValue;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        return mInflater.inflate(R.layout.preference_radio, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mContainer = (RadioGroup) view.findViewById(R.id.radio_group);
        setRadioButtons();

        mContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                String newValue = mEntryValues[radioIdToInt(id)].toString();
                setNewValue(newValue);
            }
        });
    }

    private void setRadioButtons() {
        if (mContainer != null) {
            mContainer.removeAllViews();

            for (int i = 0; i < mEntries.length; i++) {
                final int id = intToRadioId(i);

                RadioButton button = (RadioButton) mInflater.inflate(R.layout.simple_radio_button, mContainer, false);
                button.setText(mEntries[i]);
                button.setEnabled(isEnabled());
                button.setId(id); // Needed for the RadioGroup

                mContainer.addView(button);

                if (mEntryValues[i].equals(mValue))
                    mContainer.check(id);
            }
        }
    }

    // Used to avoid any 0 as id
    private int intToRadioId(int i) {
        return i + 20;
    }

    private int radioIdToInt(int i) {
        return i - 20;
    }
}