/*
 * Copyright 2012-2013, Arno Puder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.puder.trs80;

import org.puder.trs80.browser.FileBrowserActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class EditConfigurationActivity extends SherlockPreferenceActivity implements
        OnPreferenceChangeListener {

    public static final String CONF_NAME               = "conf_name";
    public static final String CONF_MODEL              = "conf_model";
    public static final String CONF_DISK1              = "conf_disk1";
    public static final String CONF_DISK2              = "conf_disk2";
    public static final String CONF_DISK3              = "conf_disk3";
    public static final String CONF_DISK4              = "conf_disk4";
    public static final String CONF_KEYBOARD_PORTRAIT  = "conf_keyboard_portrait";
    public static final String CONF_KEYBOARD_LANDSCAPE = "conf_keyboard_landscape";

    private SharedPreferences  sharedPrefs;
    private Handler            handler;

    private Preference         model;
    private Preference         name;
    private Preference         disk1;
    private Preference         disk2;
    private Preference         disk3;
    private Preference         disk4;
    private Preference         keyboardPortrait;
    private Preference         keyboardLandscape;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        Intent i = getIntent();
        int configId = i.getExtras().getInt("CONFIG_ID");
        getPreferenceManager().setSharedPreferencesName("CONFIG_" + configId);
        addPreferencesFromResource(R.xml.configuration);
        sharedPrefs = this.getPreferenceManager().getSharedPreferences();// this.getPreferences(MODE_PRIVATE);
        name = (Preference) findPreference(CONF_NAME);
        name.setOnPreferenceChangeListener(this);
        Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference pref) {
                String key = pref.getKey();
                int disk = Integer.parseInt(key.substring(key.length() - 1));
                Intent intent = new Intent(EditConfigurationActivity.this,
                        FileBrowserActivity.class);
                startActivityForResult(intent, disk);
                return true;
            }
        };
        disk1 = (Preference) findPreference(CONF_DISK1);
        disk1.setOnPreferenceChangeListener(this);
        disk1.setOnPreferenceClickListener(listener);

        disk2 = (Preference) findPreference(CONF_DISK2);
        disk2.setOnPreferenceChangeListener(this);
        disk2.setOnPreferenceClickListener(listener);

        disk3 = (Preference) findPreference(CONF_DISK3);
        disk3.setOnPreferenceChangeListener(this);
        disk3.setOnPreferenceClickListener(listener);

        disk4 = (Preference) findPreference(CONF_DISK4);
        disk4.setOnPreferenceChangeListener(this);
        disk4.setOnPreferenceClickListener(listener);

        model = (Preference) findPreference(CONF_MODEL);
        model.setOnPreferenceChangeListener(this);

        keyboardPortrait = (Preference) findPreference(CONF_KEYBOARD_PORTRAIT);
        keyboardPortrait.setOnPreferenceChangeListener(this);

        keyboardLandscape = (Preference) findPreference(CONF_KEYBOARD_LANDSCAPE);
        keyboardLandscape.setOnPreferenceChangeListener(this);

        updateSummaries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Cancel").setIcon(R.drawable.cancel_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Done").setIcon(R.drawable.ok_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Help").setIcon(R.drawable.help_icon)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ("Done".equals(item.getTitle())) {
            doneEditing(false);
            return true;
        }
        if ("Cancel".equals(item.getTitle())) {
            doneEditing(true);
            return true;
        }
        if ("Help".equals(item.getTitle())) {
            doHelp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        doneEditing(false);
    }

    private void doneEditing(boolean cancel) {
        setResult(cancel ? RESULT_CANCELED : RESULT_OK, getIntent());
        finish();
    }

    private void updateSummaries() {
        String val = sharedPrefs.getString(CONF_NAME, null);
        if (val != null) {
            name.setSummary(val);
        }
        val = sharedPrefs.getString(CONF_MODEL, null);
        if (val != null) {
            if (val.equals("5")) {
                val = "4P";
            }
            model.setSummary("Model " + val);
        }

        // Disk 1
        val = sharedPrefs.getString(CONF_DISK1, null);
        if (val != null) {
            disk1.setSummary(val);
        }

        // Disk 2
        val = sharedPrefs.getString(CONF_DISK2, null);
        if (val != null) {
            disk2.setSummary(val);
        }

        // Disk 3
        val = sharedPrefs.getString(CONF_DISK3, null);
        if (val != null) {
            disk3.setSummary(val);
        }

        // Disk 4
        val = sharedPrefs.getString(CONF_DISK4, null);
        if (val != null) {
            disk4.setSummary(val);
        }

        // Keyboard portrait
        val = sharedPrefs.getString(CONF_KEYBOARD_PORTRAIT, null);
        setKeyboardSummary(keyboardPortrait, val);

        // Keyboard landscape
        val = sharedPrefs.getString(CONF_KEYBOARD_LANDSCAPE, null);
        setKeyboardSummary(keyboardLandscape, val);
    }

    private void setKeyboardSummary(Preference pref, String val) {
        if (val == null) {
            return;
        }
        if ("0".equals(val)) {
            pref.setSummary("Original layout");
        }
        if ("1".equals(val)) {
            pref.setSummary("Compact layout");
        }
        if ("2".equals(val)) {
            // pref.setSummary("Gaming layout 1");
            pref.setSummary("Gaming layout");
        }
        if ("3".equals(val)) {
            pref.setSummary("Gaming layout 2");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String newValue = data.getStringExtra("PATH");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("conf_disk" + requestCode, newValue);
            editor.commit();
            updateSummaries();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        /*
         * When we get to this point, the preferences have not yet been updated
         * yet. For this reason updateSummaries() is called via a handler to
         * ensure the preferences have been updated.
         */
        handler.post(new Runnable() {

            @Override
            public void run() {
                updateSummaries();
            }
        });
        return true;
    }

    private void doHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help_title_edit_configuration);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.help_edit_configuration, null, false);
        TextView t = (TextView) view.findViewById(R.id.help_text);
        t.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}