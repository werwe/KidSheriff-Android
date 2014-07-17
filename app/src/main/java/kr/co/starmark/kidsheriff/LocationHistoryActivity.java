package kr.co.starmark.kidsheriff;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import kr.co.starmark.kidsheriff.request.GsonRequest;
import kr.co.starmark.kidsheriff.request.HistoryRequestData;
import kr.co.starmark.kidsheriff.request.LocationHistoryResult;
import kr.co.starmark.kidsheriff.request.UserDataResult;
import kr.co.starmark.kidsheriff.resource.ImageStoreInfo;
import kr.co.starmark.kidsheriff.resource.ImageStoreInfoList;

public class LocationHistoryActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = "LocalHistoryActivity";
    private GoogleMap mGoogleMap = null;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private LocationManager mLocManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private CharSequence mTitle;

    private UserDataResult mUserData;
    private ArrayList<String> mChildAccountList = new ArrayList<String>();

    @InjectView(R.id.progress_container)
    View mProgressContainer;

    @InjectView(R.id.location_controll_panel)
    View mControllPanel;

    View mInfoWindow;

    private List<kr.co.starmark.kidsheriff.request.Location> mLocations = new ArrayList<kr.co.starmark.kidsheriff.request.Location>();
    private Polyline mCurrentPolyLine = null;
    private Circle mCurrentCircle = null;
    private Marker mCurrentMarker = null;
    private int mLocationHead = 0;
    private int mStoredSection = 0;

    HashMap<Marker, ImageStoreInfo> mMarkerMaps = new HashMap<Marker, ImageStoreInfo>();
    private BitmapDescriptor pinDescriptor;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        ButterKnife.inject(this);
        setActionBar();
        pinDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.pin);
        mUserData = getIntent().getParcelableExtra("userinfo");
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setUserData(mUserData);
        mNavigationDrawerFragment.setUpListView();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                mDrawerLayout
        );
        try {
            initilizeMap();
            updateLocationHistory();
            updateImageHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateLocationHistory()
    {
        mProgressContainer.setVisibility(View.VISIBLE);
        Response.Listener<LocationHistoryResult> response = new Response.Listener<LocationHistoryResult>()
        {
            @Override
            public void onResponse(LocationHistoryResult result) {
                Log.d("result", result.toString());
                mLocations = result.getList();
                if(result.getList().size() == 0)
                {
                    removePolyLine();
                    removeCurrentCircle();
                    hideControlPanel();
                    mProgressContainer.setVisibility(View.GONE);

                    return;
                }
                sortList();
                showControlPanel();
                displayLocationLine(result);
//                displayMarker(getLocation(mLocationHead));
//                displayLocationCircle(result.getList().get(0));
                mProgressContainer.setVisibility(View.GONE);
                moveToCurrentLocation(result.getList().get(0));
            }
        };

        Response.ErrorListener errorCallback = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("onErrorResponse", " "+volleyError.getMessage());
                //Toast.makeText(LocationHistoryActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                removePolyLine();
                removeCurrentCircle();
                mProgressContainer.setVisibility(View.GONE);
                hideControlPanel();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        HistoryRequestData data = new HistoryRequestData();
        data.setRequestorId(mUserData.getEmail());
        Log.d(TAG, mNavigationDrawerFragment.getSelectedAccount());
        data.setTargetUserId(mNavigationDrawerFragment.getSelectedAccount());
        data.setLimit(100);
        Gson gson = new Gson();
        gson.toJson(data).toString();
        requestQueue.add(
                new GsonRequest<LocationHistoryResult>(
                        Request.Method.POST,
                        "http://kid-sheriff-001.appspot.com/apis/getLoc",
                        LocationHistoryResult.class,
                        gson.toJson(data).toString(),
                        response,
                        errorCallback
                )
        );
    }

    private void updateImageHistory()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://kid-sheriff-001.appspot.com/apis/getImages/name="+mNavigationDrawerFragment.getSelectedAccount();
        Log.d(TAG, "updateImageHistory:"+url);
        client.get(this,url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "status Code:" + statusCode + "/"+ responseString,throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, "status Code:" + statusCode + "/"+ responseString);
                Gson gson = new Gson();
                ImageStoreInfoList list = gson.fromJson(responseString, ImageStoreInfoList.class);
                mNavigationDrawerFragment.updatePhoto(list.getList(),0);
                //makeImageMarker(list);
            }


        });
    }

    private void sortList() {
        String strArray = Arrays.toString(mLocations.toArray(new kr.co.starmark.kidsheriff.request.Location[mLocations.size()]));
        //Log.d("LocalHistoryActivity","before: \n" + strArray);
        Collections.sort(mLocations, new Comparator<kr.co.starmark.kidsheriff.request.Location>() {
            @Override
            public int compare(kr.co.starmark.kidsheriff.request.Location location, kr.co.starmark.kidsheriff.request.Location location2) {
                DateTime date1 = DateTime.parse(location.getDate());
                DateTime date2 = DateTime.parse(location2.getDate());
                return date2.compareTo(date1);
            }
        });
        strArray = Arrays.toString(mLocations.toArray(new kr.co.starmark.kidsheriff.request.Location[mLocations.size()]));
        //Log.d("LocalHistoryActivity","before: \n" + strArray);
    }

    private void hideControlPanel() {
        mControllPanel.setVisibility(View.GONE);
    }

    private void showControlPanel()
    {
        mControllPanel.setVisibility(View.VISIBLE);
    }

    private void displayMarker(kr.co.starmark.kidsheriff.request.Location loc)
    {
        DateTimeFormatter fmt = DateTimeFormat.mediumDateTime();
        String date = DateTime.parse(loc.getDate()).toString(fmt);
        StringBuilder builder = new StringBuilder();
//        Address address = getAddress(getApplicationContext(),loc);
        String title = "위도:"+loc.getLat()+"\n경도 :"+loc.getLng();

//        if(address != null) {
//            int maxLine = address.getMaxAddressLineIndex();
//            for (int i = 0; i < maxLine; i++)
//                builder.append(address.getAddressLine(i)).append("\n");
//            title = builder.toString();
//            Log.d(TAG, title);
//        }

        mCurrentMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(loc.getLat(), loc.getLng()))
                .icon(pinDescriptor)
                .title(title)
                .snippet(date)
        );
    }
    private void displayMarker(kr.co.starmark.kidsheriff.request.Location loc,String msg)
    {

        mCurrentMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(loc.getLat(), loc.getLng()))
                .icon(pinDescriptor)
                .title(msg));
    }

    private void makeImageMarker(ImageStoreInfoList list) {
        DateTimeFormatter fmt = DateTimeFormat.mediumDateTime();

        List<ImageStoreInfo> imageInfoList = list.getList();

        if(imageInfoList == null) return;
        int length = imageInfoList.size();
        if(length == 0) return;
        for(int i = 0 ; i < length ; i++) {
            ImageStoreInfo info = imageInfoList.get(i);
            Log.d(TAG, info.toString());
            //String date = DateTime.parse(info.getDate()).toString(fmt);
            String date = "";
            String title = "위도:"+info.getLat()+"\n경도 :"+info.getLng();
            mMarkerMaps.put(makeMarker(new LatLng(info.getLat(), info.getLng()), title, date), info);
        }
    }

    public Marker makeMarker(LatLng latLng,String date,String title) {
        return mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(pinDescriptor)
                .snippet(date)
                .title(title));
    }


    private void removeMarker()
    {
        if(mCurrentMarker != null)
            mCurrentMarker.remove();
        mCurrentMarker = null;
    }

    private void displayLocationLine(LocationHistoryResult result) {
        removePolyLine();
        List<kr.co.starmark.kidsheriff.request.Location> locations = result.getList();
        List<LatLng> lists = new ArrayList<LatLng>(locations.size());
        for(kr.co.starmark.kidsheriff.request.Location loc:locations)
            lists.add(new LatLng(loc.getLat(),loc.getLng()));

        PolylineOptions options = new PolylineOptions();
        options.color(Color.parseColor("#f0be21"));
        options.width(6f);
        options.geodesic(true);
        options.addAll(lists);
        options.zIndex(0f);
        mCurrentPolyLine = mGoogleMap.addPolyline(options);
    }

    private void removePolyLine()
    {
        if(mCurrentPolyLine != null)
            mCurrentPolyLine.remove();
        mCurrentPolyLine = null;
    }

    private void displayLocationCircle(kr.co.starmark.kidsheriff.request.Location loc)
    {
        //removeCurrentCircle();
        LatLng center = new LatLng(loc.getLat(), loc.getLng());
        CircleOptions options = new CircleOptions();
        options.fillColor(Color.parseColor("#FFBB33"));
        options.strokeColor(Color.parseColor("#FF8800"));
        options.center(center);
        options.radius(3f);
        options.strokeWidth(2f);
        options.zIndex(1);
        mCurrentCircle =  mGoogleMap.addCircle(options);
    }

    private void removeCurrentCircle(){
        if(mCurrentCircle !=  null)
            mCurrentCircle.remove();
        mCurrentCircle = null;
    }

    private void moveToCurrentLocation(kr.co.starmark.kidsheriff.request.Location loc) {
        LatLng latLng = new LatLng(loc.getLat(),loc.getLng());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    @OnClick(R.id.map_nav_next)
    void nextLocation()
    {
        int length = mLocations.size()-1;
        if(mLocationHead >= 0)
            mLocationHead--;

        if(mLocationHead == -1)
            mLocationHead = 0;

        displayHeadLocation();

        removeMarker();
        displayMarker(getLocation(mLocationHead));
        mCurrentMarker.showInfoWindow();
    }

    @OnClick(R.id.map_nav_prev)
    void prevLocation()
    {
        int length = mLocations.size()-1;
        if(mLocationHead < length)
            mLocationHead++;
        else
            mLocationHead = length;

        displayHeadLocation();
        removeMarker();
        displayMarker(getLocation(mLocationHead));
        mCurrentMarker.showInfoWindow();
    }

    @OnClick(R.id.map_nav_recent)
    void recentLocation()
    {
        mLocationHead = 0;
        removeMarker();
        displayMarker(getLocation(mLocationHead));
        mCurrentMarker.showInfoWindow();
        displayHeadLocation();
    }

    private kr.co.starmark.kidsheriff.request.Location getLocation(int locationHead) {
        return  mLocations.get(locationHead);
    }

    private void moveToHeadLocation( kr.co.starmark.kidsheriff.request.Location headLocation) {
        moveToCurrentLocation(headLocation);
    }

    private void displayHeadLocation() {
        kr.co.starmark.kidsheriff.request.Location headLocation = getLocation(mLocationHead);
        //displayLocationCircle(headLocation);
        moveToHeadLocation(headLocation);
    }

    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    private void initilizeMap() {
        if (mGoogleMap == null) {
            mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    if(mInfoWindow == null)
                        mInfoWindow = View.inflate(getApplicationContext(),R.layout.info_window, null);

                    TextView address = (TextView) mInfoWindow.findViewById(R.id.adress);
                    address.setText(marker.getTitle());

                    TextView title = (TextView) mInfoWindow.findViewById(R.id.date);
                    title.setText(marker.getSnippet());

                    return mInfoWindow;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    ImageStoreInfo info = mMarkerMaps.get(marker);
                    if(info == null)
                        return;
                    mNavigationDrawerFragment.updatePhoto(info.getImgUrl());
                    //mDrawerLayout.openDrawer(mDrawerLayout);
                }
            });
            // check if map is created successfully or not
            if (mGoogleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        removeAllMapObject();
        updateLocationHistory();
        updateImageHistory();
    }

    public void removeAllMapObject()
    {
        removeAllImageMarker();
        removeMarker();
        //removeCurrentCircle();
    }

    public void removeAllImageMarker()
    {
        Set<Marker> keys = mMarkerMaps.keySet();
        for(Marker m:keys)
            m.remove();
        mMarkerMaps.clear();
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    public static Address getAddress(final Context context,
                                     final kr.co.starmark.kidsheriff.request.Location loc) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(loc.getLat(), loc.getLng(), 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return addresses.get(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.location_history, menu);
       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == R.id.action_refresh)
        {
            updateLocationHistory();
            updateImageHistory();
            return true;
        }
        if (id == R.id.action_settings) {
            //startSettingActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startSettingActivity() {
        Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_location_history, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((LocationHistoryActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
