package kr.co.starmark.kidsheriff;

import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;


import com.samsung.android.sdk.accessory.SASocket;

import java.io.IOException;
import java.util.HashMap;

public class HelloAccessoryProviderConnection extends SASocket {
    public static final String TAG = "HelloAccessoryProviderService";

    public Boolean isAuthentication = false;
    public Context mContext = null;

    public static final int SERVICE_CONNECTION_RESULT_OK = 0;

    public static final int HELLOACCESSORY_CHANNEL_ID = 104;

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
        //위치 전송 서비스 를 시작
        Intent service = new Intent(mContext, LocationUploadService.class);
        mContext.startService(service);
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