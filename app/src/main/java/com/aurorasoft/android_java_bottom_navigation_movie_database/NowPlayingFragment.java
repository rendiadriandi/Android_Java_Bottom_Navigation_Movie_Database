package com.aurorasoft.android_java_bottom_navigation_movie_database;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment {

    View fragment_view;
    Context mainContext;
    ArrayList<NowPlaying> nowPlayings;
    RecyclerView rv;
    ProgressBar pb;
    SwipeRefreshLayout sw;

    public NowPlayingFragment(Context context) {
        this.mainContext = context;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_now_playing, container, false);
        fragment_view = rootview;
        pb = (ProgressBar) rootview.findViewById(R.id.pb_now_playing);
        sw = (SwipeRefreshLayout) rootview.findViewById(R.id.sw_now_playing);
        sw.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();

                //untuk delay saat menutup lingkaran sw
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sw.setRefreshing(false);
                    }
                },1000);

            }
        });

        //memberikan warna sw
        sw.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        load();
        return rootview;
    }

    private void load() {

        pb.setVisibility(ProgressBar.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=2ece2095aa583fdd1c537b9287a10c11&language=en-US";

        //Log.i("get seafood ", "load: " + url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("fgfgf: ", response.toString());
                        String id, poster_path, title, release_date;
                        nowPlayings = new ArrayList<>();

                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            nowPlayings.clear();

                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    id = data.getString("id").toString().trim();
                                    poster_path = data.getString("poster_path").toString().trim();
                                    title = data.getString("title").toString().trim();
                                    release_date = data.getString("release_date").toString().trim();

                                    nowPlayings.add(new NowPlaying(id, poster_path, title, release_date ));
                                }

                                showRecyclerGrid();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pb.setVisibility(ProgressBar.GONE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("Events: ", error.toString());

                pb.setVisibility(ProgressBar.GONE);
                Toast.makeText(getContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    private void showRecyclerGrid() {
        RecyclerView recyclerView = (RecyclerView)fragment_view.findViewById(R.id.rv_now_playing);
          //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //agar bisa deteksi orientasi layar potrait atau landscape

        if (mainContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(mainContext, 2));
        }else {
            recyclerView.setLayoutManager(new GridLayoutManager(mainContext, 4));
        }
        NowPlayingAdapter nowPlayingAdapter = new NowPlayingAdapter(getContext(),nowPlayings);
        recyclerView.setAdapter(nowPlayingAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainContext = context;
    }
}
