package kr.co.starmark.kidsheriff;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;

/**
 * Created by werwe on 2014-07-13.
 */
public class SharedPref {

    private static final String PREF_NAME = "KidSheriff-pref";
    public static final String DEFAULT_ACCOUNT = "DEFAULT_ACCOUNT";

    private static SharedPref mInstance = null;

    private SharedPreferences mPref;
    private SharedPref(SharedPreferences pref)
    {
        mPref = pref;
    }
    public static SharedPref get(Context context)
    {
        if(mInstance == null)
            mInstance = new SharedPref(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE));

        return  mInstance;
    }

    public boolean saveDefaultAccount(String account)
    {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(DEFAULT_ACCOUNT,account);
        return editor.commit();
    }

    public String loadDefaultAccount()
    {
        return mPref.getString(DEFAULT_ACCOUNT,null);
    }
}
