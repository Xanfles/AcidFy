package notebook.jonathan.com.acidfy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import notebook.jonathan.com.acidfy.model.Artist;

public class MainAcidFy extends AppCompatActivity {
    SearchView searchArtist;
    ListView listArtist;
    Artist artist;
    List<Artist> list;
    ProgressDialog progressDialog;

    String artista, contenido, id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_acid_fy);
        searchArtist = (SearchView) findViewById(R.id.searchArtist);
        listArtist = (ListView) findViewById(R.id.listArtist);
        searchArtist.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                progresBar();
                artista = searchArtist.getQuery().toString().trim().replace(" ","-");
                GetArtistID getArtistID = new GetArtistID();
                getArtistID.execute(new String[] {"https://api.spotify.com/v1/search/?q="+ artista + "&type=artist"});
                return false;
            }
        });

        listArtist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(MainAcidFy.this, AlbumsView.class);
                Bundle b = new Bundle();
                b.putString("id",list.get(position).getId());
                i.putExtra("bundle",b);
                startActivity(i);
            }
        });
    }

    public void progresBar(){
        progressDialog = new ProgressDialog(MainAcidFy.this);
                progressDialog.setMax(50);
                progressDialog.setMessage("Loading....");
                progressDialog.setTitle("Cargando contenido");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
                new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (progressDialog.getProgress() <= progressDialog
                            .getMax()) {
                        Thread.sleep(200);
                        handle.sendMessage(handle.obtainMessage());
                        if (progressDialog.getProgress() == progressDialog
                                .getMax()) {
                            progressDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class CustomAdapter extends ArrayAdapter {

        public CustomAdapter(Context context, int resource, List<Artist> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = convertView;
            if(item == null){
                item = getLayoutInflater().inflate(R.layout.layout_album,null);
            }
            TextView nombre = (TextView) item.findViewById(R.id.nombre);
            ImageView imagen = (ImageView) item.findViewById(R.id.Imagen);

            artist = (Artist) getItem(position);

            nombre.setText(artist.getNombre());
            String img = artist.getImagen().toString();

            try {
                URL url = new URL(img);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                imagen.setImageBitmap(bmp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return item;
        }
    }
    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.incrementProgressBy(1);
        }
    };
    private class GetArtistID extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {

            super.onPreExecute();
        }
        protected void onPostExecute(Boolean result) {
            JSONObject json = null;    // create JSON obj from string
            String imagen = "";
            try {
                json = new JSONObject(contenido);
                JSONArray json2 = json.getJSONObject("artists").getJSONArray("items");
                list = new ArrayList<Artist>();
                for(int i = 0; i < json2.length(); i++){
                    String name = json2.getJSONObject(i).getString("name");
                    String id = json2.getJSONObject(i).getString("id");
                    Log.e("id", id);
                    if(json2.getJSONObject(i).getJSONArray("images").isNull(0)){
                        imagen = "http://vignette2.wikia.nocookie.net/assassinscreed/images/3/39/Not-found.jpg/revision/latest?cb=20110517171552";
                    }else{
                        String json3 = json2.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url");
                        imagen = json3.toString();
                    }
                    list.add(new Artist(name, imagen, id));
                }
                ArrayAdapter<Artist> adapter = new CustomAdapter(MainAcidFy.this,R.layout.layout_album,list);
                listArtist.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected Boolean doInBackground(String... urls) {
            InputStream inputStream = null;
            for (String url1 : urls) {
                try {
                    URL url = new URL(url1);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milisegundos */);
                    conn.setConnectTimeout(15000 /* milisegundos */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    int response = conn.getResponseCode();
                    Log.d("SERVIDOR", "La respuesta del servidor es: " + response);
                    inputStream = conn.getInputStream();
                    // Convertir inputstream a string
                    contenido = new Scanner(inputStream).useDelimiter("\\A").next();
                    Log.i("CONTENIDO",contenido);
                } catch (Exception ex) {
                    Log.e("ERROR", ex.toString()); return false;
                }
            }
            return true;

        }
    }

}
