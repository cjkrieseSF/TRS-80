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

import org.puder.trs80.Hardware.Model;

import android.content.SharedPreferences.Editor;

public class ConfigurationBackup extends Configuration {

    private String   backupName;
    private Model    backupModel;
    private String[] backupDisk = new String[4];
    private int      backupKeyboardLayoutPortrait;
    private int      backupKeyboardLayoutLandscape;

    private Editor   editor;

    protected ConfigurationBackup(Configuration other) {
        super(other.id);
        this.backupName = other.getName();
        this.backupModel = other.getModel();
        this.backupDisk[0] = other.getDiskPath(0);
        this.backupDisk[1] = other.getDiskPath(1);
        this.backupDisk[2] = other.getDiskPath(2);
        this.backupDisk[3] = other.getDiskPath(3);
        this.backupKeyboardLayoutPortrait = other.getKeyboardLayoutPortrait();
        this.backupKeyboardLayoutLandscape = other.getKeyboardLayoutLandscape();
    }

    public void save() {
        editor = sharedPrefs.edit();
        saveName();
        saveModel();
        saveDiskPath(0);
        saveDiskPath(1);
        saveDiskPath(2);
        saveDiskPath(3);
        saveKeyboardLayout();
        editor.commit();
    }

    private void saveName() {
        editor.putString(EditConfigurationActivity.CONF_NAME, backupName);
    }

    private void saveModel() {
        String model = null;
        switch (backupModel) {
        case MODEL1:
            model = "1";
            break;
        case MODEL3:
            model = "3";
            break;
        case MODEL4:
            model = "4";
            break;
        case MODEL4P:
            model = "5";
            break;
        default:
            break;
        }
        if (model == null) {
            editor.remove(EditConfigurationActivity.CONF_MODEL);
        } else {
            editor.putString(EditConfigurationActivity.CONF_MODEL, model);
        }
    }

    private void saveDiskPath(int disk) {
        String key;
        switch (disk) {
        case 0:
            key = EditConfigurationActivity.CONF_DISK1;
            break;
        case 1:
            key = EditConfigurationActivity.CONF_DISK2;
            break;
        case 2:
            key = EditConfigurationActivity.CONF_DISK3;
            break;
        case 3:
            key = EditConfigurationActivity.CONF_DISK4;
            break;
        default:
            return;
        }
        if (backupDisk == null) {
            editor.remove(key);
        } else {
            editor.putString(key, backupDisk[disk]);
        }
    }

    private void saveKeyboardLayout() {
        editor.putString(EditConfigurationActivity.CONF_KEYBOARD_PORTRAIT,
                Integer.toString(backupKeyboardLayoutPortrait));
        editor.putString(EditConfigurationActivity.CONF_KEYBOARD_LANDSCAPE,
                Integer.toString(backupKeyboardLayoutLandscape));
    }
}