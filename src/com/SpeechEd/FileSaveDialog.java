/*
Copyright © 2012 SSAD Team 37

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/* Author: Kriti Kansal */
/**
     * Return a human-readable name for a kind (music, alarm, ringtone, ...).
     * These won't be displayed on-screen (just in logs) so they shouldn't
     * be translated.
*/

package com.SpeechEd;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;


public class FileSaveDialog extends Dialog {

    // File kinds - these should correspond to the order in which
    // they're presented in the spinner control
    public static final int FILE_KIND_MUSIC = 0;
    public static final int FILE_KIND_ALARM = 1;
    public static final int FILE_KIND_NOTIFICATION = 2;
    public static final int FILE_KIND_RINGTONE = 3;

    private Spinner mTypeSpinner;
    private EditText mFilename;
    private Message mResponse;
    private String mOriginalName;
    private ArrayList<String> mTypeArray;
    private int mPreviousSelection;

   
    public static String KindToName(int kind) {
        switch(kind) {
        default:
            return "Unknown";
        case FILE_KIND_MUSIC:
            return "Music";
        case FILE_KIND_ALARM:
            return "Alarm";
        case FILE_KIND_NOTIFICATION:
            return "Notification";
        case FILE_KIND_RINGTONE:
            return "Ringtone";
        }
    }

    public FileSaveDialog(Context context,
                          Resources resources,
                          String originalName,
                          Message response) {
        super(context);

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.file_save);

        setTitle(resources.getString(R.string.file_save_title));

        mTypeArray = new ArrayList<String>();
        mTypeArray.add(resources.getString(R.string.type_music));
        mTypeArray.add(resources.getString(R.string.type_alarm));
        mTypeArray.add(resources.getString(R.string.type_notification));
        mTypeArray.add(resources.getString(R.string.type_ringtone));

        mFilename = (EditText)findViewById(R.id.filename);
        mOriginalName = originalName;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            context, android.R.layout.simple_spinner_item, mTypeArray);
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner = (Spinner) findViewById(R.id.ringtone_type);
        mTypeSpinner.setAdapter(adapter);
        mTypeSpinner.setSelection(FILE_KIND_RINGTONE);
        mPreviousSelection = FILE_KIND_RINGTONE;

        setFilenameEditBoxFromName(false);

        mTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView parent,
                                           View v,
                                           int position,
                                           long id) {
                    setFilenameEditBoxFromName(true);
                }
                public void onNothingSelected(AdapterView parent) {
                }
            });

        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(saveListener);
        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(cancelListener);
        mResponse = response;
    }

    private void setFilenameEditBoxFromName(boolean onlyIfNotEdited) {
        if (onlyIfNotEdited) {
            CharSequence currentText = mFilename.getText();
            String expectedText = mOriginalName + " " +
                mTypeArray.get(mPreviousSelection);

            if (!expectedText.contentEquals(currentText)) {
                return;
            }
        }

        int newSelection = mTypeSpinner.getSelectedItemPosition();
        String newSuffix = mTypeArray.get(newSelection);
        mFilename.setText(mOriginalName + " " + newSuffix);
        mPreviousSelection = mTypeSpinner.getSelectedItemPosition();
    }

    private View.OnClickListener saveListener = new View.OnClickListener() {
            public void onClick(View view) {
                mResponse.obj = mFilename.getText();
                mResponse.arg1 = mTypeSpinner.getSelectedItemPosition();
                mResponse.sendToTarget();
                dismiss();
            }
        };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        };
}
