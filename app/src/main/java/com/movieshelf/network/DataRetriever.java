package com.movieshelf.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.movieshelf.Constants;
import com.movieshelf.network.Appcontroller;

import org.json.JSONObject;


/**
 * Created by Eswar on 07-02-2016
 */
public class DataRetriever {

    private DataListener mListener;
    private static ImageLoader imageLoader;
    private String url;

    public DataRetriever(DataListener listener, String url) {
        mListener = listener;
        this.url = url;
    }

    public void makeRequest() {
        String url = Constants.Baseurl + this.url;
        System.out.println("requested url : " + url);

        JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                mListener.dataReceived(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mListener.error("data error");
                System.out.println("error in getting data");
            }
        });

        mListener.requestStart();
        Appcontroller.getmInstance().addtoRequestqueue(request);
    }

    public void makeImageRequest(String image_url) {
        imageLoader = Appcontroller.getmInstance().getImageLoader();
        imageLoader.get(image_url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer != null) {
                    System.out.println("image recieved..");
                    mListener.imageReceived(imageContainer);
                } else
                    System.out.println("error in image recieved..");
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("volley error");
                mListener.error("Image Error");
            }
        });
    }

    public interface DataListener {
        void requestStart();

        void dataReceived(JSONObject jsonObject);

        void imageReceived(ImageLoader.ImageContainer imageContainer);

        void error(String error);
    }
}
