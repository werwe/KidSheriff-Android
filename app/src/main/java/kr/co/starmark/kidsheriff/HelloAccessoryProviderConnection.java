package kr.co.starmark.kidsheriff;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.samsung.android.sdk.accessory.SASocket;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class HelloAccessoryProviderConnection extends SASocket {
    public static final String TAG = "HelloAccessoryProviderService";

    public Boolean isAuthentication = false;
    public Context mContext = null;

    public static final int SERVICE_CONNECTION_RESULT_OK = 0;

    public static final int HELP_CHANNEL_ID = 104;
    public static final int CAPTURE_CHANNEL_ID = 105;

    private int mConnectionId;
    private HashMap<Integer, HelloAccessoryProviderConnection> mConnectionsMap;

    public HelloAccessoryProviderConnection() {
        super(HelloAccessoryProviderConnection.class.getName());
    }

    @Override
    public void onError(int channelId, String errorString, int error) {
        Log.e(TAG, "Connection is not alive ERROR: " + errorString + "  "
                + error);
    }

    @Override
    public void onReceive(int channelId, byte[] data) {
        Log.d(TAG, "onReceive");
        if(data.length == 0) {
            //위치 전송 서비스 를 시작
            //&& push notification
            Log.d(TAG, mConnectionId + " / " + channelId);
            Intent service = new Intent(mContext, LocationUploadService.class);
            mContext.startService(service);

        }else {

            String s = new String(data).split(",")[1];
            Log.d(TAG, "data:" + s);

            byte[] decode = Base64.decode(s, Base64.DEFAULT);

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);

            File outputDir = mContext.getExternalCacheDir(); // context being the Activity pointer
            Log.d(TAG, "out put dir:" + outputDir.getAbsolutePath());
            if(!outputDir.exists())
                outputDir.mkdirs();

            File outputFile = null;
            try {
                outputFile = File.createTempFile("temp001", ".png", outputDir);
                final FileOutputStream filestream = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, filestream);
                UploadImage(outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadImage(File file) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            String emailId = SharedPref.get(mContext).loadDefaultAccount();

            params.put("emailid", emailId);
            params.put("image", file);
        } catch(FileNotFoundException e) {}
        client.post(mContext,"http://kid-sheriff-001.appspot.com/apis/uploadImg",params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "Upload onSuccess:"+statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Upload onFailure:"+statusCode);
            }
        });
    }

    @Override
    protected void onServiceConnectionLost(int errorCode) {
        Log.e(TAG, "onServiceConectionLost  for peer = " + mConnectionId + "error code =" + errorCode);

        if (mConnectionsMap != null) {
            mConnectionsMap.remove(mConnectionId);
        }
    }

    public void setmConnectionMap(HashMap<Integer, HelloAccessoryProviderConnection> mConnectionMap) {
        this.mConnectionsMap = mConnectionMap;
    }

    public void setConnectionId(int mConnectionId) {
        this.mConnectionId = mConnectionId;
    }
    public int getConnectionId() {
        return this.mConnectionId;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }
}