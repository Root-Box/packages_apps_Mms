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
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.mms.LogTag;
import com.android.mms.R;
import com.android.mms.data.Contact;
import com.android.mms.data.Group;
import com.android.mms.data.PhoneNumber;

public class AddRecipientsListItem extends RelativeLayout {
    private static final String TAG = "AddRecipientsListItem";

    private LinearLayout mHeader;
    private LinearLayout mFooter;
    private TextView mSeparator;
    private TextView mNameView;
    private TextView mNumberView;
    private TextView mNumberViewNoAvatar;
    private TextView mLabelView;
    private QuickContactBadge mAvatarView;
    private CheckBox mCheckBox;

    static private Drawable sDefaultContactImage;

    private PhoneNumber mPhoneNumber;
    private Group mGroup;

    public AddRecipientsListItem(Context context, PhoneNumber phoneNumber) {
        super(context);
        mContext = context;
        mPhoneNumber = phoneNumber;
        mGroup = null;
    }

    public AddRecipientsListItem(Context context, Group group) {
        super(context);
        mContext = context;
        mGroup = group;
        mPhoneNumber = null;
    }

    public AddRecipientsListItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (sDefaultContactImage == null) {
            sDefaultContactImage = context.getResources().getDrawable(R.drawable.ic_contact_picture);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHeader = (LinearLayout) findViewById(R.id.header);
        mFooter = (LinearLayout) findViewById(R.id.footer);
        mSeparator = (TextView) findViewById(R.id.separator);
        mNameView = (TextView) findViewById(R.id.name);
        mNumberView = (TextView) findViewById(R.id.number);
        mNumberViewNoAvatar = (TextView) findViewById(R.id.number_no_avatar);
        mLabelView = (TextView) findViewById(R.id.label);
        mAvatarView = (QuickContactBadge) findViewById(R.id.avatar);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);

    }

    public PhoneNumber getPhoneNumber() {
        return mPhoneNumber;
    }

    public Group getGroup() {
        return mGroup;
    }

    public boolean isGroup() {
        return (mPhoneNumber == null) ? true : false;
    }

    private void updateAvatarView() {
        Drawable avatarDrawable;

        Contact contact = mPhoneNumber.getContact();
        avatarDrawable = contact.getAvatar(mContext, sDefaultContactImage);

        if (contact.existsInDatabase()) {
            mAvatarView.assignContactUri(contact.getUri());
        } else {
            mAvatarView.assignContactFromPhone(contact.getNumber(), true);
        }

        mAvatarView.setImageDrawable(avatarDrawable);
        mAvatarView.setVisibility(View.VISIBLE);
    }

    public final void bind(Context context, final PhoneNumber phoneNumber, boolean showHeader, boolean showFooter) {
        String name = phoneNumber.getName();

        if (showHeader) {
            mHeader.setVisibility(View.VISIBLE);
            mSeparator.setText(name.substring(0, 1).toUpperCase());
        } else {
            mHeader.setVisibility(View.GONE);
        }

        if (showFooter) {
            mFooter.setVisibility(View.VISIBLE);
        } else {
            mFooter.setVisibility(View.GONE);
        }

        mPhoneNumber = phoneNumber;

        if (phoneNumber.isFirst()) {
            mNameView.setVisibility(View.VISIBLE);
            mNameView.setText(name);
            mNumberViewNoAvatar.setVisibility(View.GONE);
            mNumberView.setVisibility(View.VISIBLE);
            mNumberView.setText(phoneNumber.getNumber());
            updateAvatarView();
        } else {
            mNameView.setVisibility(View.GONE);
            mNumberView.setVisibility(View.GONE);
            mNumberViewNoAvatar.setVisibility(View.VISIBLE);
            mNumberViewNoAvatar.setText(phoneNumber.getNumber());
            mAvatarView.setVisibility(View.GONE);
        }

        mLabelView.setText(Phone.getTypeLabel(getResources(), phoneNumber.getType(), phoneNumber.getLabel()));
        mCheckBox.setChecked(phoneNumber.isChecked());
    }

    public final void bind(Context context, final Group group, boolean showHeader) {
        if (showHeader) {
            mHeader.setVisibility(View.VISIBLE);
            mSeparator.setText(R.string.groups_header);
        } else {
            mHeader.setVisibility(View.GONE);
        }

        mFooter.setVisibility(View.VISIBLE);
        mGroup = group;

        int memberCount = group.getSummaryCount();
        mNameView.setVisibility(View.VISIBLE);
        mNameView.setText(group.getTitle() + " (" + memberCount + ")");

        mNumberViewNoAvatar.setVisibility(View.GONE);
        mNumberView.setVisibility(View.VISIBLE);
        mNumberView.setText(group.getAccountName());
        mLabelView.setText("");
        mCheckBox.setChecked(group.isChecked());
        mAvatarView.setVisibility(View.GONE);
    }

    public final void unbind() {
        if (Log.isLoggable(LogTag.CONTACT, Log.DEBUG)) {
            Log.v(TAG, "unbind: contacts.removeListeners " + this);
        }
    }
}
