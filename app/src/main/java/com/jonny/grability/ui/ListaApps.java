package com.jonny.grability.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.jonny.grability.R;
import com.jonny.grability.data.Apps;
import com.jonny.grability.data.AppsAdapter;

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

public class ListaApps extends AppCompatActivity {

    private ListView mListViewApps;
    private AppsAdapter mAppsAdapter;
    private ArrayList<Apps> mAppses;
    private GridView mGridViewList;

    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        File httpCacheDir = getExternalCacheDir();
        try {
//            File httpCacheDir = new File(this.getCacheDir(), "http");
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

//

        setTitle(getString(R.string.tituloListaApps));
        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setMessage(getString(R.string.mDialogText));


        mListViewApps = (ListView) findViewById(R.id.listViewMain);
        mGridViewList = (GridView) findViewById(R.id.gridView1);


        new ViewHolder().execute("https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                new ViewHolder().execute("https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json");
                return true;

        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new ViewHolder().execute("https://itunes.apple.com/us/rss/topfreeapplications/limit=20/json");
    }


    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDialog!=null)
            if(mDialog.isShowing()){
                mDialog.cancel();}
    }

    public class ViewHolder extends AsyncTask<String, String, List<Apps>>{

        @Override
        protected void onPreExecute() {
            mDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final List<Apps> s) {
            super.onPostExecute(s);

            final AppsAdapter adapter;

            if (mDialog.isShowing()) {
                mDialog.cancel();
            }
            if(s.size() == 0){

                Toast.makeText(ListaApps.this, R.string.toastSEmpty, Toast.LENGTH_SHORT).show();
            }
            Intent intentDatos = getIntent();
            String filtro = intentDatos.getStringExtra("Filtro");


            List<Apps> result = new ArrayList<Apps>();
            for (Apps apps : s) {
                   if (apps.getCategoria().equals(filtro) || "Total".equals(filtro)) {
                           result.add(apps);
                   }
            }

            adapter = new AppsAdapter(ListaApps.this, android.R.layout.simple_expandable_list_item_1, result);

            Configuration config = getResources().getConfiguration();
            if (config.smallestScreenWidthDp >= 600)
            {
                mGridViewList.setAdapter(adapter);
                mGridViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        parent.getItemAtPosition(position);
                        Intent intent = new Intent(ListaApps.this, AppsDetails.class);
                        intent.putExtra("Title", adapter.getItem(position).getTitulo());
                        intent.putExtra("Imagen", adapter.getItem(position).getImagen2());
                        intent.putExtra("Descrip", adapter.getItem(position).getDescripcion());
                        intent.putExtra("Categoria", adapter.getItem(position).getCategoria());
                        intent.putExtra("Autor", adapter.getItem(position).getAutor());
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(

                                ListaApps.this,


                                new Pair<View, String>(view.findViewById(R.id.textoTitulo),
                                        getString(R.string.transition_name_name)),
                                new Pair<View, String>(view.findViewById(R.id.imagenApp),
                                        getString(R.string.transition_name_Image)),
                                new Pair<View, String>(view.findViewById(R.id.textoArtista),
                                        getString(R.string.transition_name_Cate))
                        );



                        ActivityCompat.startActivity(ListaApps.this, intent, options.toBundle());
                    }
                });
            }
            else
            {
                mListViewApps.setAdapter(adapter);
                mListViewApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        parent.getItemAtPosition(position);
                        Intent intent = new Intent(ListaApps.this, AppsDetails.class);
                        intent.putExtra("Title", adapter.getItem(position).getTitulo());
                        intent.putExtra("Imagen", adapter.getItem(position).getImagen2());
                        intent.putExtra("Descrip", adapter.getItem(position).getDescripcion());
                        intent.putExtra("Categoria", adapter.getItem(position).getCategoria());
                        intent.putExtra("Autor", adapter.getItem(position).getAutor());

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(

                                ListaApps.this,


                                new Pair<View, String>(view.findViewById(R.id.textoTitulo),
                                        getString(R.string.transition_name_name)),
                                new Pair<View, String>(view.findViewById(R.id.imagenApp),
                                getString(R.string.transition_name_Image)),
                                new Pair<View, String>(view.findViewById(R.id.textoArtista),
                                        getString(R.string.transition_name_Cate))
                        );



                        ActivityCompat.startActivity(ListaApps.this, intent, options.toBundle());
                    }
                });
            }


        }


        Bitmap downloadImage(String source) {
            try {
                //   'source' is the URL of the image
                URL url = new URL(source);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //   Value of stale indicates how old the cache content can be
                //   before using it. Here the value is 1 hour(in secs)
                conn.addRequestProperty("Cache-Control", "max-stale=" + 60 * 60);

                //   Indicating the connection to use caches
                conn.setUseCaches(true);

                //   Using the stream to download the image
                InputStream stream = conn.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(stream);
                stream.close();

                return image;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected List<Apps> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            List<Apps> appsList = new ArrayList<Apps>();
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


                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject secondObject = parentArray.getJSONObject(i);
                        JSONObject titleObject = secondObject.getJSONObject("im:name");
                        JSONObject summaryObject = secondObject.getJSONObject("summary");
                        JSONObject categoryObjetct = secondObject.getJSONObject("category");

                            JSONObject categoriaFinalObject = categoryObjetct.getJSONObject("attributes");
                            JSONArray imgArrayObject = secondObject.getJSONArray("im:image");
                            JSONObject indexImageObject = imgArrayObject.getJSONObject(2);
                            JSONObject artistObject = secondObject.getJSONObject("im:artist");


                            Apps appsModel = new Apps();
                            appsModel.setTitulo(titleObject.getString("label"));
                            appsModel.setDescripcion(summaryObject.getString("label"));
                            appsModel.setCategoria(categoriaFinalObject.getString("label"));
//                            appsModel.setImagen(indexImageObject.getString("label"));
                            appsModel.setImagen2(downloadImage(indexImageObject.getString("label")));
                            appsModel.setAutor(artistObject.getString("label"));


                            appsList.add(appsModel);


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

    }




}
