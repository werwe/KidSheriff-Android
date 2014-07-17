package kr.co.starmark.kidsheriff;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import kr.co.starmark.kidsheriff.request.Location;

public class LocationUploadService extends IntentService {
    private static final String TAG = "LocationUploadService";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "kr.co.starmark.kidsheriff.action.FOO";
    private static final String ACTION_BAZ = "kr.co.starmark.kidsheriff.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "kr.co.starmark.kidsheriff.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "kr.co.starmark.kidsheriff.extra.PARAM2";
    private LocationUpdater mLocationUpdater;


    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LocationUploadService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, LocationUploadService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startUploadService(Context context)
    {
        Intent intent = new Intent(context, LocationUploadService.class);
        context.startService(intent);
    }

    public LocationUploadService() {
        super("LocationUploadService");
        Log.d(TAG, "LocationUploadService - constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }


    private void handleActionFoo(String param1, String param2) {
        Log.d(TAG, "handleActionFoo");
    }

    private void handleActionBaz(String param1, String param2) {
        Log.d(TAG, "handleActionFoo");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Location Upload Service onCreate");
        mLocationUpdater = new LocationUpdater(getApplicationContext());
        mLocationUpdater.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartCommand");
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Location Upload Service Destory");
    }
}
