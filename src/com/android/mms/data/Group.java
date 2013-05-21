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

package com.android.mms.data;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Groups;
import android.util.Log;

import com.android.mms.LogTag;

public class Group {
    private static final String TAG = "Mms/recipient";
    private static final boolean DEBUG = false;

    private static final int _ID           = 0;
    private static final int GROUP_TITLE   = 1;
    private static final int ACCOUNT_NAME  = 2;
    private static final int ACCOUNT_TYPE  = 3;
    private static final int DATA_SET      = 4;
    private static final int SUMMARY_COUNT = 5;

    private Context mContext;

    private long mId;                             // The ID of the group
    private String mTitle;                        // The title of the group
    private String mAccountName;                  // The name of the account which the group belongs to
    private String mAccountType;                  // The type of the account which the group belongs to
    private String mDataSet;                      // The dataset of the account which the group belongs to
    private int mSummaryCount;                    // The number of contacts in the group
    private ArrayList<PhoneNumber> mPhoneNumbers; // The phone numbers of the contacts of the group

    private boolean mIsChecked;

    private Group(Context context, long id, String title, String accountName, String accountType,
            String dataSet, int summaryCount, ArrayList<PhoneNumber> phoneNumbers) {
        mContext = context;

        mId = id;
        mTitle = title;
        mAccountName = accountName;
        mAccountType = accountType;
        mDataSet = dataSet;
        mSummaryCount = summaryCount;
        mPhoneNumbers = phoneNumbers;
    }

    private Group(Context context, Cursor cursor) {
        if (DEBUG) {
            Log.v(TAG, "Recipient constructor cursor");
        }

        fillFromCursor(context, this, cursor);
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAccountName() {
        return mAccountName;
    }

    public String getAccountType() {
        return mAccountType;
    }

    public String getDataSet() {
        return mDataSet;
    }

    public int getSummaryCount() {
        return mSummaryCount;
    }

    public ArrayList<PhoneNumber> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    public void addPhoneNumber(PhoneNumber phoneNumber) {
        if (!mPhoneNumbers.contains(phoneNumber)) {
            mPhoneNumbers.add(phoneNumber);
        }
    }

    /**
     * Returns true if this group is selected for a multi-operation.
     */
    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    /**
     * Fill the specified group with the values from the specified
     * cursor
     */
    private static void fillFromCursor(Context context, Group group, Cursor c) {
        group.mContext = context;

        group.mId = c.getLong(_ID);
        group.mTitle = c.getString(GROUP_TITLE);
        group.mAccountName = c.getString(ACCOUNT_NAME);
        group.mAccountType = c.getString(ACCOUNT_TYPE);
        group.mDataSet = c.getString(DATA_SET);
        group.mSummaryCount = c.getInt(SUMMARY_COUNT);

        group.mPhoneNumbers = new ArrayList<PhoneNumber>();

        if (Log.isLoggable(LogTag.THREAD_CACHE, Log.VERBOSE)) {
            Log.d(TAG, "fillFromCursor: recipient=" + group.mTitle + ", groupId=" + group.mId);
        }
    }

    /**
     * Get all groups
     * @param context
     * @return all possible recipients
     */
    public static ArrayList<Group> getGroups(Context context) {
        ArrayList<Group> groups = new ArrayList<Group>();

        final String[] groupsProjection = new String[] {
                Groups._ID,
                Groups.TITLE,
                Groups.ACCOUNT_NAME,
                Groups.ACCOUNT_TYPE,
                Groups.DATA_SET,
                Groups.SUMMARY_COUNT,
        };

        final String groupsSelection = Groups.ACCOUNT_TYPE + " NOT NULL AND "
                + Groups.ACCOUNT_NAME + " NOT NULL AND "
                + Groups.AUTO_ADD + "=0 AND "
                + Groups.DELETED + "=0 AND "
                + Groups.SUMMARY_COUNT + "!=0";

        final String groupsSort = Groups.ACCOUNT_TYPE + ", "
                + Groups.ACCOUNT_NAME + ", "
                + Groups.DATA_SET + ", "
                + Groups.TITLE + " COLLATE LOCALIZED ASC";

        final Cursor groupsCursor = context.getContentResolver().query(Groups.CONTENT_SUMMARY_URI,
                groupsProjection, groupsSelection, null, groupsSort);

        if (groupsCursor == null) {
            return null;
        }

        final int groupsCount = groupsCursor.getCount();
        if (groupsCount == 0) {
            groupsCursor.close();
            return null;
        }

        for (int i = 0; i < groupsCount; i++) {
            groupsCursor.moveToPosition(i);
            Group group = new Group(context, groupsCursor);
            groups.add(group);
        }
        return groups;
    }

    public static Group get(Context context, long id) {
        ArrayList<Group> groups = getGroups(context);

        int count = groups.size();
        for (int i = 0; i < count; i++) {
            Group group = groups.get(i);
            if (group.mId == id) {
                return group;
            }
        }
        return null;
    }
}
