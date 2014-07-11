package kr.co.starmark.kidsheriff;

import android.content.Context;
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

        Time time = new Time();

        time.set(System.currentTimeMillis());

        String timeStr = " " + String.valueOf(time.minute) + ":"
                + String.valueOf(time.second);

        String strToUpdateUI = new String(data);

        final String message = strToUpdateUI.concat(timeStr);

        final HelloAccessoryProviderConnection uHandler = mConnectionsMap.get(Integer
                .parseInt(String.valueOf(mConnectionId)));
        if (uHandler == null) {
            Log.e(TAG, "Error, can not get HelloAccessoryProviderConnection handler");
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    uHandler.send(HELLOACCESSORY_CHANNEL_ID, message.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onServiceConnectionLost(int errorCode) {
        Log.e(TAG, "onServiceConectionLost  for peer = " + mConnectionId
                + "error code =" + errorCode);

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
}