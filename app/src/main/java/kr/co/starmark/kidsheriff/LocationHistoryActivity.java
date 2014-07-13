package kr.co.starmark.kidsheriff;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import kr.co.starmark.kidsheriff.request.GsonRequest;
import kr.co.starmark.kidsheriff.request.HistoryRequestData;
import kr.co.starmark.kidsheriff.request.LocationHistoryResult;
import kr.co.starmark.kidsheriff.request.UserDataResult;

public class LocationHistoryActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
    @InjectView(R.id.btn_refresh_map)
    View mRefreshButton;
    @InjectView(R.id.location_controll_panel)
    View mControllPanel;

    private List<kr.co.starmark.kidsheriff.request.Location> mLocations = new ArrayList<kr.co.starmark.kidsheriff.request.Location>();
    private Polyline mCurrentPolyLine = null;
    private Circle mCurrentCircle = null;
    private int mLocationHead = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        ButterKnife.inject(this);

        mUserData = getIntent().getParcelableExtra("userinfo");
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUserData(mUserData);
        mNavigationDrawerFragment.setUpListView();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        try {
            initilizeMap();
            updateLocationHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_refresh_map)
    private void updateLocationHistory()
    {
        mProgressContainer.setVisibility(View.VISIBLE);
        mRefreshButton.setVisibility(View.GONE);
        Response.Listener<LocationHistoryResult> response = new Response.Listener<LocationHistoryResult>()
        {
            @Override
            public void onResponse(LocationHistoryResult result) {
                Log.d("result", result.toString());
                if(result.getList().size() == 0)
                {
                    removePolyLine();
                    removeCurrentCircle();
                    hideControlPanel();
                }
                displayLocationLine(result);
                displayLocationCircle(result.getList().get(0));
                mProgressContainer.setVisibility(View.GONE);
                mRefreshButton.setVisibility(View.VISIBLE);
                moveToCurrentLocation(result.getList().get(0));
            }
        };

        Response.ErrorListener errorCallback = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("onErrorResponse", volleyError.getMessage());
                Toast.makeText(LocationHistoryActivity.this, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                mProgressContainer.setVisibility(View.GONE);
                mRefreshButton.setVisibility(View.VISIBLE);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        HistoryRequestData data = new HistoryRequestData();
        data.setRequestorId(mUserData.getEmail());
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

    private void hideControlPanel() {
        mControllPanel.setVisibility(View.GONE);
    }

    private void showControlPanel()
    {
        mControllPanel.setVisibility(View.VISIBLE);
    }

    private void displayLocationLine(LocationHistoryResult result) {
        removePolyLine();
        List<kr.co.starmark.kidsheriff.request.Location> locations = result.getList();
        List<LatLng> lists = new ArrayList<LatLng>(locations.size());
        for(kr.co.starmark.kidsheriff.request.Location loc:locations)
            lists.add(new LatLng(loc.getLat(),loc.getLng()));

        PolylineOptions options = new PolylineOptions();
        options.color(Color.parseColor("#99CC00"));
        options.width(2f);
        options.geodesic(true);
        options.addAll(lists);
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
        removeCurrentCircle();
        LatLng center = new LatLng(loc.getLat(), loc.getLng());
        CircleOptions options = new CircleOptions();
        options.fillColor(Color.parseColor("#FFBB33"));
        options.strokeColor(Color.parseColor("#FF8800"));
        options.center(center);
        options.radius(1f);
        options.strokeWidth(1f);
        mCurrentCircle =  mGoogleMap.addCircle(options);
    }

    private void removeCurrentCircle(){
        if(mCurrentCircle !=  null)
            mCurrentCircle.remove();
        mCurrentCircle = null;
    }

    private void moveToCurrentLocation(kr.co.starmark.kidsheriff.request.Location loc) {
        LatLng latLng = new LatLng(loc.getLat(),loc.getLng());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    @OnClick(R.id.map_nav_next)
    void nextLocation()
    {
        int length = mLocations.size()-1;
        if(mLocationHead < length)
            mLocationHead++;
        else
            mLocationHead = 0;
        displayLocationCircle(getLocation(mLocationHead));
    }

    @OnClick(R.id.map_nav_prev)
    private void prevLocation()
    {
        int length = mLocations.size()-1;
        if(mLocationHead >= 0)
            mLocationHead--;
        if(mLocationHead == -1)
            mLocationHead = length;
        displayLocationCircle(getLocation(mLocationHead));
    }

    @OnClick(R.id.map_nav_recent)
    private void recentLocation()
    {
        mLocationHead = 0;
        displayLocationCircle(getLocation(mLocationHead));
    }

    private kr.co.starmark.kidsheriff.request.Location getLocation(int locationHead) {
        return  mLocations.get(locationHead);
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
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
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
            mTitle = mUserData.getLinkedAccounts().get(number);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

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
