package kr.co.starmark.kidsheriff;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        this.setAppContext(getApplicationContext());
    }

    public static MyApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    public static void updateAccount(Context context,final String emailId,final String registId) {
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("emailId",emailId);
//        params.put("pushId", registId);
//        Log.d("MyApplication", "emailId:" + params.has("emailId") + "/ reg id:" + params.has("pushId"));
//        client.post(context,"http://kid-sheriff-001.appspot.com/apis/updateAccount",params,new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.d("MyApplication", "status code:" + statusCode, throwable);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Log.d("MyApplication", "status code:" + statusCode + "/"+responseString);
//            }
//        });
    }
}