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
import com.loopj.android.http.TextHttpResponseHandler;
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

        String msg = new String(data);

        if(msg.startsWith("startGPS"))
        {
            Intent service = new Intent(mContext, LocationUploadService.class);
            mContext.startService(service);
            //http://kid-sheriff-001.appspot.com/apis/pushNoti/name={name}
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(mContext, "http://kid-sheriff-001.appspot.com/apis/pushNoti/name=" + SharedPref.get(mContext).loadDefaultAccount(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d(TAG, "status code:" + statusCode, throwable);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d(TAG, "status code:" + statusCode);
                }
            });
        }
        else if(msg.startsWith("endGPS"))
        {
            Intent service = new Intent(mContext, LocationUploadService.class);
            mContext.stopService(service);
        }
        else
        {
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
                outputFile = File.createTempFile("molca_"+System.currentTimeMillis(), ".png", outputDir);
                final FileOutputStream filestream = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, filestream);
                ImageStoreClient.get(mContext).uploadFile(outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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