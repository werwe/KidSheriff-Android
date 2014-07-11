package kr.co.starmark.kidsheriff;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.andreabaccega.formedittextvalidator.Validator;
import com.andreabaccega.widget.FormEditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import kr.co.starmark.kidsheriff.request.GsonRequest;
import kr.co.starmark.kidsheriff.request.LinkRequestData;


public class RegistActivity extends Activity {
    static final String TAG = "RegistActivity";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final int CHILD = 1;
    public static final int PARENT = 2;
    private final int ACCOUNT_ITEM_RESID = R.layout.account_item;

    @InjectView(R.id.account_container)
    LinearLayout mAccoutListContainer;


    ArrayList<View> mChild = new ArrayList<View>(20);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.inject(this);
        addAccountField();
    }

    @OnClick(R.id.btn_regitst)
    void onRegistClick() {
        EditText editText;
        List<String> emailList = new ArrayList<String>();
        for (View v : mChild) {
            editText = ButterKnife.findById(v, R.id.account_field);
            if (!editText.isEnabled())
                emailList.add(editText.getText().toString());
        }


        Response.Listener<String> response = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String result) {
                Log.d("result", result);
            }
        };


        Response.ErrorListener errorCallback = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("onErrorResponse", volleyError.getMessage());
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //GsonRequest<LinkRequestData> data = new GsonRequest<LinkRequestData>();
        LinkRequestData data = new LinkRequestData();
        data.email = "werwe.me@gmail.com";
        data.linkedAccounts = emailList;
        data.pushid = getRegistrationId(getApplicationContext());
        Log.d("pushId", data.pushid);
        data.whichSide = PARENT;
        Gson gson = new Gson();
        gson.toJson(data).toString();
        requestQueue.add(
                new GsonRequest<String>(
                        Request.Method.POST,
                        "http://kid-sheriff-001.appspot.com/apis/link",
                        String.class,
                        gson.toJson(data).toString(),
                        response,
                        errorCallback
                       ));




    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences(Context context) {
        return getSharedPreferences("GCMStorePref", Context.MODE_PRIVATE);
    }

    private void setItemActionCallback(final View view) {
        final FormEditText accountEditText = ButterKnife.findById(view, R.id.account_field);
        final Button addButton = ButterKnife.findById(view, R.id.btn_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                if (addButton.getText().equals("+")) {
                    accountEditText.addValidator(new DuplicateValidator());
                    if (!accountEditText.testValidity())
                        return;
                    accountEditText.setEnabled(false);
                    addButton.setText("-");
                    addAccountField();
                } else if (addButton.getText().equals("-")) {
                    removeAccountField(view);
                }
            }
        });
    }

    private void removeAccountField(final View view) {
        mChild.remove(view);
        mAccoutListContainer.removeView(view);
    }

    private void addAccountField() {
        View v = makeAccountFieldView();
        setItemActionCallback(v);
        mAccoutListContainer.addView(v);
        mChild.add(v);
    }

    private View makeAccountFieldView() {
        return View.inflate(this, ACCOUNT_ITEM_RESID, null);
    }

    class DuplicateValidator extends Validator {
        public DuplicateValidator() {
            super("중복된 메일이 있습니다.");

        }

        @Override
        public boolean isValid(EditText editText) {
            for (View v : mChild) {
                EditText et = ButterKnife.findById(v, R.id.account_field);
                if (et.hashCode() == editText.hashCode())
                    continue;
                if (et.getText().toString().equals(editText.getText().toString()))
                    return false;
            }
            return true;
        }
    }
}
