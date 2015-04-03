package kr.co.starmark.kidsheriff;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by werwe on 2014. 7. 16..
 */
public class ContactProfileGetter {

    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{

            ContactsContract.Contacts._ID, // 0

            ContactsContract.Contacts.DISPLAY_NAME, // 1

            ContactsContract.Contacts.STARRED, // 2

            ContactsContract.Contacts.TIMES_CONTACTED, // 3

            ContactsContract.Contacts.CONTACT_PRESENCE, // 4

            ContactsContract.Contacts.PHOTO_ID, // 5

            ContactsContract.Contacts.LOOKUP_KEY, // 6

            ContactsContract.Contacts.HAS_PHONE_NUMBER, // 7

    };

    static final int SUMMARY_ID_COLUMN_INDEX = 0;

    static final int SUMMARY_NAME_COLUMN_INDEX = 1;

    static final int SUMMARY_STARRED_COLUMN_INDEX = 2;

    static final int SUMMARY_TIMES_CONTACTED_COLUMN_INDEX = 3;

    static final int SUMMARY_PRESENCE_statUS_COLUMN_INDEX = 4;

    static final int SUMMARY_PHOTO_ID_COLUMN_INDEX = 5;

    static final int SUMMARY_LOOKUP_KEY = 6;

    static final int SUMMARY_HAS_PHONE_COLUMN_INDEX = 7;


    Context mContext;

    public void ContactProfileGetter(Context context) {
        String select = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("

                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("

                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";

        Cursor c = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,

                CONTACTS_SUMMARY_PROJECTION,

                select, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
    }

}
