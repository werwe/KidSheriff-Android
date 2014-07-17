
package kr.co.starmark.kidsheriff;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nineoldandroids.view.ViewHelper;

import org.apache.http.Header;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


import butterknife.ButterKnife;
import butterknife.InjectView;
import kr.co.starmark.kidsheriff.request.GsonRequest;
import kr.co.starmark.kidsheriff.request.UserDataResult;


public class SplashActivity extends Activity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int ACCOUNT_PICKER_REQUEST = 10000;
    String SENDER_ID = "1098403155208";

    static final String TAG = "SplashScreen";


    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    String regid;

    private ViewPropertyAnimator mLogoAnimator = null;
    private Runnable mStartAct = new Runnable() {
        @Override
        public void run() {
            checkDefaultAccount();
        }
    };

    private Handler mHandler = new Handler();

    private void checkDefaultAccount() {
        SharedPref pref = SharedPref.get(this);
        String defaultAccount = pref.loadDefaultAccount();
        if(defaultAccount == null)
        {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                    false, null, null, null, null);
            startActivityForResult(intent, ACCOUNT_PICKER_REQUEST);
        }
        else
        {
            checkServerAccount(defaultAccount);
        }
    }

    protected void onActivityResult(final int requestCode, final int resultCode,final Intent data) {
        if (requestCode == ACCOUNT_PICKER_REQUEST && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            //Log.d(TAG, "accountName:" + accountName);
            SharedPref.get(this).saveDefaultAccount(accountName);
            checkServerAccount(accountName);
        }
        else
        {
            finish();
        }
    }

    private void checkServerAccount(final String name) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(false);
        dialog.setMessage(name + "의 계정 정보를 확인합니다");
        dialog.show();

        Response.Listener<UserDataResult> response = new Response.Listener<UserDataResult>() {
            @Override
            public void onResponse(UserDataResult result) {
                //Log.d("result", result.getResult());
                dialog.dismiss();
                Intent intent = null;
                if (result.getResult().equals("notExist"))
                    intent = new Intent(getApplicationContext(), RegistActivity.class);
                else {
                    intent = new Intent(getApplicationContext(), LocationHistoryActivity.class);
                    updateAccount();
                }
                result.setEmail(name);
                intent.putExtra("userinfo", result);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        };

        Response.ErrorListener errorCallback = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //Log.d("onErrorResponse", volleyError.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("계정을 확인하던 중 오류가 발생했습니다.");
                builder.setMessage(volleyError.getMessage());
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(
                new GsonRequest(
                        Request.Method.GET,
                        "http://kid-sheriff-001.appspot.com/apis/check/account="+name,
                        UserDataResult.class,
                        null,
                        response,
                        errorCallback
                )
        );
    }

    private void updateAccount() {
        String emailId = SharedPref.get(getApplicationContext()).loadDefaultAccount();
        String registId = getRegistrationId(getApplicationContext());
        //MyApplication.updateAccount(emailId, registId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //LocationUploadService.startUploadService(this);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        context = getApplicationContext();

        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        //ShowLogo();
        mHandler.postDelayed(mStartAct, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        //Log.d(TAG, getRegistrationId(this));
    }

//    private void ShowLogo() {
//        mLogo.post(new Runnable() {
//            @Override
//            public void run() {
//                float height = mLogo.getHeight();
//                ViewHelper.setY(mLogo, height / 2 + mLogo.getY());
//                mLogoAnimator = mLogo.animate().translationY(0).alpha(1).setDuration(1000).setInterpolator(new DecelerateInterpolator(1f));
//                mLogoAnimator.setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mStartAct.run();
//                    }
//                });
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
//        mLogo.removeCallbacks(mStartAct);
//        mLogoAnimator.setListener(null);
//        mLogoAnimator.cancel();
        mHandler.removeCallbacks(mStartAct);
        super.onBackPressed();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        //앱이 업데이트 되면 기존registration id 가 푸시를 받는 걸 보장 하지 않는다.
        //해서 빈 아이디를 던지고 새로 받도록 처리한다.
        //서버의 push id 도 같이 갱신 해야한다.
        //default account로 push id 갱신하는 코드를 넣어야 함.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
        }.execute(null, null, null);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences(Context context) {
        return getSharedPreferences("GCMStorePref", Context.MODE_PRIVATE);
    }

    public static class UpdateAccountAsyncTask extends AsyncTask<Void,Void,Void>
    {
        private Context mContext;
        private String emailId;
        private String registId;

        public UpdateAccountAsyncTask(Context context,String emailAccount ,String registId)
        {
            mContext = context;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("emailId",emailId);
            params.put("pushId",registId);
            client.post(mContext,"http://kid-sheriff-001.appspot.com/apis/updateAccount",params,new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    //Log.d(TAG, "status code:" + statusCode, throwable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    //Log.d(TAG, "status code:" + statusCode + "responseString");
                }
            });
            return null;
        }
    }
}


//    private void accountMissingAlert() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("1개 이상의 구글 계정이 필요합니다.");
//        builder.setPositiveButton("확인",new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(SplashActivity.this.getApplicationContext(), "앱을 종료합니다", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        });
//    }

//send message
//            new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground(Void... params) {
//                    String msg = "";
//                    try {
//                        Bundle data = new Bundle();
//                        data.putString("my_message", "Hello World");
//                        data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
//                        String id = Integer.toString(msgId.incrementAndGet());
//                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
//                        msg = "Sent message";
//                    } catch (IOException ex) {
//                        msg = "Error :" + ex.getMessage();
//                    }
//                    return msg;
//                }