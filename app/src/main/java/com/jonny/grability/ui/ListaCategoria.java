package com.jonny.grability.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.jonny.grability.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListaCategoria extends AppCompatActivity {

    private static final String TAG = ListaCategoria.class.getSimpleName();
    private ListView mListViewCate;
    public GridView mGridViewCate;
    public boolean isTablet;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_categoria);
        final Configuration config = getResources().getConfiguration();
        isTablet = config.smallestScreenWidthDp >= 600;


        File httpCacheDir = getExternalCacheDir();

        try {
            //File httpCacheDir = new File(this.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        }catch (IOException e) {
            Log.i("ListaApps", "HTTP response cache installation failed:" + e);
        }

        if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
        else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (isTablet){

            mGridViewCate = (GridView) findViewById(R.id.gridviewCategorias);
        }else {
            mListViewCate = (ListView) findViewById(R.id.listacategorias);
        }
        new Categorias().execute("https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actualizar:
                new Categorias().execute("https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json");
                return true;

        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGridViewCate = (GridView) findViewById(R.id.gridviewCategorias);
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGridViewCate = (GridView) findViewById(R.id.gridviewCategorias);
    }

    public class Categorias extends AsyncTask<String, String, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            ArrayList<String> appsList = new ArrayList<String>();
                try {
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setUseCaches(true);
                    int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                    connection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
                    connection.connect();




                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    String jsonResult = buffer.toString();

                    JSONObject parentObject = new JSONObject(jsonResult);
                    JSONObject entryObject = parentObject.getJSONObject("feed");
                    JSONArray parentArray = entryObject.getJSONArray("entry");


                    appsList.add("Total");
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject secondObject = parentArray.getJSONObject(i);
                        JSONObject categoryObjetct = secondObject.getJSONObject("category");
                        JSONObject finalCategoryObject = categoryObjetct.getJSONObject("attributes");

                        String added = finalCategoryObject.getString("label");

                        if (appsList.contains(added)){
                            Log.d(TAG, "se detectó la igualdad");
                        }else {

                            appsList.add(finalCategoryObject.getString("label"));
                        }

                    }



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                    try {
                        if (reader != null) {
                            reader.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            return appsList;

        }

        @Override
        protected void onPostExecute(final List<String> s) {
            super.onPostExecute(s);

            Log.d(TAG, "Resultado " + s);

            if(s.size() == 0){

                Toast.makeText(ListaCategoria.this, R.string.toastSEmpty, Toast.LENGTH_SHORT).show();
            }

            final ArrayAdapter arrayAdapter = new ArrayAdapter<>(ListaCategoria.this,
                    android.R.layout.simple_expandable_list_item_1, s);


            if (isTablet)  {
                mGridViewCate.setAdapter(arrayAdapter);

                mGridViewCate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    parent.getItemAtPosition(position);
                    Intent intent = new Intent(ListaCategoria.this, ListaApps.class);
                    intent.putExtra("Filtro", s.get(position));

                    startActivity(intent);
                }
            });


        }
        else {
            mListViewCate.setAdapter(arrayAdapter);

            mListViewCate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    parent.getItemAtPosition(position);
                    Intent intent = new Intent(ListaCategoria.this, ListaApps.class);
                    intent.putExtra("Filtro", s.get(position));

                    startActivity(intent);
                }
            });


        }




        }

}

}

