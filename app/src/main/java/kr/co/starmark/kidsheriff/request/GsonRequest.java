package kr.co.starmark.kidsheriff.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;


public class GsonRequest<T> extends JsonRequest<T> {
    private Gson mGson = null;
    private Class<T> mClazz = null;
    private Listener<T> mListener = null;

    public GsonRequest(int method, String url, Class<T> clazz, String json, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, json, listener, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        mGson = new Gson();
    }

    public GsonRequest(int method, String url, Class<T> clazz, String json, Listener<T> listener, ErrorListener errorListener, Gson userGson) {
        super(method, url, json, listener, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        mGson = userGson;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(json, mClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}