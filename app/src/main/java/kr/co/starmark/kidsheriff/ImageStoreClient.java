package kr.co.starmark.kidsheriff;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.OnClick;
import kr.co.starmark.kidsheriff.resource.Entity;
import kr.co.starmark.kidsheriff.resource.FileMeta;
import kr.co.starmark.kidsheriff.resource.FileUrl;

/**
 * Created by werwe on 2014. 7. 16..
 */
public class ImageStoreClient {
    private static final String TAG = ImageStoreClient.class.getName();

    private Context mContext;
    private static ImageStoreClient mClinet;

    private ImageStoreClient(Context context)
    {
        mContext = context;
    }

    public static synchronized ImageStoreClient get(Context context)
    {
        if(mClinet == null)
            mClinet = new ImageStoreClient(context.getApplicationContext());

        return mClinet;
    }

    public void uploadFile(final File file) {
        String getUrl = "http://kid-sheriff-001.appspot.com/apis/file/url";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(mContext, getUrl, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("onFailure", "status code:" + statusCode + "/" + responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("onSuccess", responseString);
                Gson gson = new Gson();
                FileUrl uploadUrl = gson.fromJson(responseString, FileUrl.class);
                upload(file, uploadUrl);
            }
        });
    }

    private void upload(final File file , FileUrl uploadUrl)
    {
        //parameters
        //String emailId = req.getParameter("emailId");
        //double lat = Double.parseDouble(req.getParameter("lat"));
        //double lng = Double.parseDouble(req.getParameter("lng"));
        //String date = req.getParameter("date");

        Log.d(TAG, "Upload Url:" + uploadUrl.getUrl());

        RequestParams params = new RequestParams();
        SharedPref pref = SharedPref.get(mContext);
        params.put("emailId",pref.loadDefaultAccount());
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        params.put("date", DateTime.now().toString(fmt));
        params.put("lat", pref.loadLastLat());
        params.put("lng", pref.loadLastLng());

        try {
            params.put("file", file);
        } catch(FileNotFoundException e) {}

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(mContext,uploadUrl.getUrl(),params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("uploadFile", "onFailure: statusCode: " + statusCode + "/" + responseString);
                Log.d("uploadFile","error",throwable);
                if(file.exists())
                    file.delete();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("uploadFile", "onSuccess:" + responseString);
                Gson gson = new Gson();
                Entity entity = gson.fromJson(responseString, Entity.class);
                FileMeta meta = entity.getFiles().get(0);
                Log.d(TAG, "download Url:" + "http://kid-sheriff-001.appspot.com"+meta.getUrl());
                //downloadFile(meta.getFiles().get(0));
                if(file.exists())
                    file.delete();
            }
        });
    }

    private void downloadFile(FileMeta meta) {

        //Download Url Request
        //Volley로 변경 해야함.
        AsyncHttpClient client = new AsyncHttpClient();

        Log.d(TAG, "download Url:" + "http://kid-sheriff-001.appspot.com"+meta.getUrl());
        client.get(mContext,"http://kid-sheriff-001.appspot.com"+meta.getUrl(),null,new FileAsyncHttpResponseHandler(mContext) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Log.d("downloadFile", "onFailure:" + file.getAbsolutePath());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Log.d("downloadFile", "onSuccess:" + file.getAbsolutePath());
            }
        });
    }

}
