/*
 * Copyright (C) 2013 The CyanogenMod Project
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import com.android.mms.R;
import com.android.mms.data.Group;
import com.android.mms.data.PhoneNumber;
import com.android.mms.util.HanziToPinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AddRecipientsListAdapter extends ArrayAdapter<AddRecipientsListItem> implements SectionIndexer {
    private static final String TAG = "AddRecipientsListAdapter";
    private final LayoutInflater mFactory;
    private HashMap<String, Integer> alphaIndexer;
    private String[] sections;

    public AddRecipientsListAdapter(Context context, List<AddRecipientsListItem> items) {
        super(context, R.layout.add_recipients_list_item, items);
        mFactory = LayoutInflater.from(context);

        alphaIndexer = new HashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            AddRecipientsListItem item = items.get(i);
            if (!item.isGroup()) {
                String name = item.getPhoneNumber().getName();
                String s = name.substring(0, 1).toUpperCase();
                final HanziToPinyin pinyin = HanziToPinyin.getInstance();
                String hzPinYin = pinyin.getFirstPinYin(name).toUpperCase();

                if (!name.equals(hzPinYin) && !hzPinYin.isEmpty()) {
                    s = hzPinYin;
                }

                if (!alphaIndexer.containsKey(s)) {
                    alphaIndexer.put(s, i);
                }
            }
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];

        for (int i = 0; i < sectionList.size(); i++) {
            sections[i] = sectionList.get(i);
        }
    }

    public View getView(int position, View convertView, ViewGroup viewGroup) {
        AddRecipientsListItem view;

        if (convertView == null) {
            view = (AddRecipientsListItem) mFactory.inflate(
                    R.layout.add_recipients_list_item, viewGroup, false);
        } else {
            if (convertView instanceof AddRecipientsListItem) {
                view = (AddRecipientsListItem) convertView;
            } else {
                return convertView;
            }
        }

        bindView(position, view);
        return view;
    }

    private void bindView(int position, AddRecipientsListItem view) {
        final AddRecipientsListItem item = this.getItem(position);

        PhoneNumber phoneNumber = item.getPhoneNumber();
        Group group = item.getGroup();
        boolean showHeader;

        if (!item.isGroup()) {
            showHeader = alphaIndexer.containsValue(position);

            boolean showFooter = true;
            long cid = phoneNumber.getContactId();
            PhoneNumber nextPhoneNumber = null;
            long nextCid = -1;
            int lastIndex = this.getCount() - 1;

            if (position < lastIndex) {
                int nextPosition = position + 1;
                nextPhoneNumber = this.getItem(nextPosition).getPhoneNumber();
                if (nextPhoneNumber != null) {
                    nextCid = nextPhoneNumber.getContactId();
                }
            }

            if (cid == nextCid) {
                showFooter = false;
                nextPhoneNumber.setFirst(false);
            }
            view.bind(getContext(), phoneNumber, showHeader, showFooter);
        } else {
            showHeader = (position == 0);
            view.bind(getContext(), group, showHeader);
        }
    }

    @Override
    public int getPositionForSection(int section) {
        return alphaIndexer.get(sections[section]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }
}
