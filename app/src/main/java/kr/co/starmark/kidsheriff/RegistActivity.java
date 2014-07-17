package kr.co.starmark.kidsheriff;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import kr.co.starmark.kidsheriff.request.UserDataResult;


public class RegistActivity extends Activity {
    static final String TAG = "RegistActivity";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final int CHILD = 1;
    public static final int PARENT = 2;
    private final int ACCOUNT_ITEM_RESID = R.layout.account_item;

    @InjectView(R.id.account_scrollview)
    ScrollView mAccountScrollView;

    @InjectView(R.id.account_container)
    LinearLayout mAccoutListContainer;

    @InjectView(R.id.child)
    RadioButton mChildButton;
    @InjectView(R.id.parent)
    RadioButton mParentButton;

    @InjectView(R.id.icon_child)
    ImageView mIconChild;
    @InjectView(R.id.icon_parent)
    ImageView mIconParent;

    @InjectView(R.id.text_regist)
    TextView mTextView;

    ArrayList<View> mChild = new ArrayList<View>(20);

    private UserDataResult mUserData;

    private int mCheckId = R.id.parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.inject(this);
        mUserData = getIntent().getParcelableExtra("userinfo");

        mChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParentButton.setChecked(false);
                mIconParent.setImageResource(R.drawable.parent_icon_off);
                mIconChild.setImageResource(R.drawable.child_icon_on);
                mTextView.setText("보호자 휴대폰의 계정을 입력해 주세요.");
                mCheckId = R.id.parent;
            }
        });
        mParentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChildButton.setChecked(false);
                mIconParent.setImageResource(R.drawable.parent_icon_on);
                mIconChild.setImageResource(R.drawable.child_icon_off);
                mTextView.setText("아이 휴대폰의 계정을 입력해 주세요.");
                mCheckId = R.id.child;
            }
        });
        addAccountField();
    }

    @OnClick(R.id.btn_regist)
    void onRegistClick() {
        FormEditText editText;
        List<String> emailList = new ArrayList<String>();
        for (View v : mChild) {
            editText = ButterKnife.findById(v, R.id.account_field);
            if(!editText.isEnabled() && !editText.testValidity())
                return;
        }
        for (View v : mChild) {
            editText = ButterKnife.findById(v, R.id.account_field);

            if (!editText.isEnabled())
                emailList.add(editText.getText().toString());
        }

        if(emailList.size() == 0)
        {
            emptyListWarnning();
            return;
        }

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setIndeterminate(false);
        progress.setMessage("등록중입니다.");
        progress.show();

        Response.Listener<String> response = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String result) {
                //Log.d("result", result);
                if (result.equals("success")) {
                    progress.dismiss();
                    moveToLocationHistoryActivity();
                }
            }
        };

        Response.ErrorListener errorCallback = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Log.d("onErrorResponse", volleyError.getMessage());
                Toast.makeText(RegistActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        LinkRequestData data = new LinkRequestData();
        data.email = mUserData.getEmail();
        data.linkedAccounts = emailList;
        data.pushid = getRegistrationId(getApplicationContext());
        //Log.d("pushId", data.pushid);
        data.whichSide = mCheckId == R.id.parent ? 2 : 1 ;

        mUserData.setLinkedAccounts(data.linkedAccounts);
        mUserData.setWhichSide(data.whichSide);
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
            )
        );
    }

    private void emptyListWarnning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("최소 1개의 개정을 입력 해야 합니다.");
        builder.setPositiveButton("확인", null).show();
    }

    private void moveToLocationHistoryActivity() {
        //registration result save
        Intent intent = new Intent(this,LocationHistoryActivity.class);
        intent.putExtra("userinfo",mUserData);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
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
                if (accountEditText.isEnabled()) {
                    accountEditText.addValidator(new DuplicateValidator());
                    if (!accountEditText.testValidity())
                        return;
                    accountEditText.setEnabled(false);
                    addAccountField();
                } else {
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
        FormEditText fet = ButterKnife.findById(v, R.id.account_field);
        fet.addValidator(new SameAccountValidator());
        setItemActionCallback(v);
        mAccoutListContainer.addView(v);
        mChild.add(v);
        mAccountScrollView.post(new Runnable() {
            @Override
            public void run() {
                mAccountScrollView.smoothScrollTo(0, mAccountScrollView.getBottom());
            }
        });
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

    class SameAccountValidator extends Validator {
        public SameAccountValidator() {
            super("현재 휴대폰과 같은 계정 입니다.");
        }

        @Override
        public boolean isValid(EditText editText) {
            SharedPref pref = SharedPref.get(RegistActivity.this);
            //Log.d(TAG,"Same Account Validator");
            //Log.d(TAG,pref.loadDefaultAccount());
            //Log.d(TAG,editText.getText().toString());
            if (pref.loadDefaultAccount().equals(editText.getText().toString()))
            {
                return false;
            }
            return true;
        }
    }
}
