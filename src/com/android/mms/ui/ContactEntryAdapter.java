/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.mms.ui;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.mms.R;

public class ContactEntryAdapter extends CursorAdapter {
    private Context mContext;
    private int mDataIndex;
    private int mTypeIndex;
    private int mLabelIndex;
    private int mMimeTypeIndex;

    protected LayoutInflater mInflater;

    private static final int sResource = R.layout.contact_entry_list_item;

    public ContactEntryAdapter(Context context, Cursor c) {
        super(context, c);
        mContext = context;
        mDataIndex = c.getColumnIndex(Data.DATA1);
        mTypeIndex = c.getColumnIndex(Data.DATA2);
        mLabelIndex = c.getColumnIndex(Data.DATA3);
        mMimeTypeIndex = c.getColumnIndex(Data.MIMETYPE);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Cursor c = getCursor();
        c.moveToPosition(position);

        String data = c.getString(mDataIndex);
        int type = c.getInt(mTypeIndex);
        String label = c.getString(mLabelIndex);
        String mimeType = c.getString(mMimeTypeIndex);

        if(convertView == null){
            convertView = mInflater.inflate(sResource, parent, false);
        }

        Resources res = mContext.getResources();
        if(mimeType.equals(Phone.MIMETYPE)){
            label = Phone.getTypeLabel(res, type, label).toString();
        }
        else if(mimeType.equals(Email.MIMETYPE)){
            label = Email.getTypeLabel(res, type, label).toString();
        }
        else if(mimeType.equals(StructuredPostal.MIMETYPE)){
            label = StructuredPostal.getTypeLabel(res, type, label).toString();
        }
        else if(mimeType.equals(Website.MIMETYPE)){
            label = "";
        }

        TextView text = (TextView) convertView.findViewById(R.id.text_entry_type);
        text.setText(Phone.getTypeLabel(mContext.getResources(), type, label) + ":");

        text = (TextView) convertView.findViewById(R.id.text_entry_data);
        text.setText(data);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

}
