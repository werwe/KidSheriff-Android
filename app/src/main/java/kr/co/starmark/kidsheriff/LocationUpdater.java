package kr.co.starmark.kidsheriff;

import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;

import kr.co.starmark.kidsheriff.request.GsonRequest;
import kr.co.starmark.kidsheriff.request.LinkRequestData;
import kr.co.starmark.kidsheriff.request.LocationUploadRequestData;

/**
 * Created by werwe on 2014. 7. 14..
 */
public class LocationUpdater implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener
{

    public static final String TAG = "LocationUpdate";
    private Context mContext;
    private LocationClient mLocationClient = null;

    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    LocationRequest mLocationRequest;
    boolean mUpdatesRequested;

    public void LocationChecker(Context context) {
        mContext = context;
        mLocationClient = new LocationClient(context, this, this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    public void connect()
    {
        mLocationClient.connect();
    }

    public void disconnect()
    {
        // If the client is connected
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates(this);
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        mLocationClient.disconnect();
    }

    public Location getLastLocation()
    {
        return mLocationClient.getLastLocation();
    }

    public void requestLocationUpdates()
    {
        if (mUpdatesRequested) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(mContext, "Connected", Toast.LENGTH_SHORT).show();
        requestLocationUpdates();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(mContext, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
        disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void uploadLocation(kr.co.starmark.kidsheriff.request.Location location)
    {
        Log.d(TAG, "uplaod location:" + location.toString());
        Response.Listener<String> response = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String result) {
                Log.d(TAG, result);
            }
        };

        Response.ErrorListener errorCallback = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(TAG, volleyError.getMessage());
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        SharedPref pref = SharedPref.get(mContext);
        String defaultAccount = pref.loadDefaultAccount();
        LocationUploadRequestData data = new LocationUploadRequestData();
        data.setUserId(defaultAccount);
        data.setLoc(new kr.co.starmark.kidsheriff.request.Location(location.getDate(),location.getLat(),location.getLng()));

        Gson gson = new Gson();
        gson.toJson(data).toString();
        requestQueue.add(
                new GsonRequest<String>(
                        Request.Method.POST,
                        "http://kid-sheriff-001.appspot.com/apis/updateLoc",
                        String.class,
                        gson.toJson(data).toString(),
                        response,
                        errorCallback
                )
        );

    }
}
//    private static final int TWO_MINUTES = 1000 * 60 * 2;
//    /** Determines whether one Location reading is better than the current Location fix
//     * @param location The new Location that you want to evaluate
//     * @param currentBestLocation The current Location fix, to which you want to compare the new one
//     */
//    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
//        if (currentBestLocation == null) {
//            // A new location is always better than no location
//            return true;
//        }
//        // Check whether the new location fix is newer or older
//        long timeDelta = location.getTime() -  currentBestLocation.getTime();
//        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
//        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
//        boolean isNewer = timeDelta > 0;
//        // If it’s been more than two minutes since the current location, use the new location
//        // because the user has likely moved
//        if (isSignificantlyNewer) {
//            return true;
//            // If the new location is more than two minutes older, it must be worse
//        } else if (isSignificantlyOlder) {
//            return false;
//        }
//        // Check whether the new location fix is more or less accurate
//        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
//        boolean isLessAccurate = accuracyDelta > 0;
//        boolean isMoreAccurate = accuracyDelta < 0;
//        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
//        // Check if the old and new location are from the same provider
//        boolean isFromSameProvider = isSameProvider(location.getProvider(),
//                currentBestLocation.getProvider());
//        // Determine location quality using a combination of timeliness and accuracy
//        if (isMoreAccurate) {
//            return true;
//        } else if (isNewer && !isLessAccurate) {
//            return true;
//        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
//            return true;
//        }
//        return false;
//    }
//    /** Checks whether two providers are the same */
//    private boolean isSameProvider(String provider1, String provider2) {
//        if (provider1 == null) {
//            return provider2 == null;
//        }
//        return provider1.equals(provider2);
//    }
