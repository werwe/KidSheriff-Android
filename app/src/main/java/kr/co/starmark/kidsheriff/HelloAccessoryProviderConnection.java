package kr.co.starmark.kidsheriff;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;


import com.samsung.android.sdk.accessory.SASocket;

import java.io.File;
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
        if(channelId == HELP_CHANNEL_ID) {
            //위치 전송 서비스 를 시작
            Log.d(TAG, mConnectionId + " / " + channelId);
            Intent service = new Intent(mContext, LocationUploadService.class);
            mContext.startService(service);
        }else if(channelId == CAPTURE_CHANNEL_ID) {
            byte[] decode = Base64.decode(data, Base64.DEFAULT);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);

            File outputDir = mContext.getCacheDir(); // context being the Activity pointer
            Log.d(TAG, "out put dir:" + outputDir.getAbsolutePath());
            File outputFile = null;
            try {
                outputFile = File.createTempFile("temp001", "temp", outputDir);
                final FileOutputStream filestream = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, filestream);
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