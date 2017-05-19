package notebook.jonathan.com.acidfy;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;
import android.widget.Toast;

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

public class AlbumsView extends AppCompatActivity {
    ListView listAlbum;
    List<Artist> list;
    SQLiteDatabase db;
    String contenido,id;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_view);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        listAlbum = (ListView) findViewById(R.id.listAlbums);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras().getBundle("bundle");
        id = extras.getString("id");
        progresBar();
        GetAlbum getArtistID = new GetAlbum();
        getArtistID.execute(new String[] {"https://api.spotify.com/v1/artists/"+id+"/albums"});
        SQLUtilities conexion = new SQLUtilities(AlbumsView.this,"Favoritos", null,1);
        db = conexion.getWritableDatabase();

        listAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlbumsView.this);
                alertDialogBuilder.setTitle("Favoritos");
                alertDialogBuilder.setMessage("¿Desea agregar el album a favoritos?");
                alertDialogBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        db.execSQL("INSERT INTO Favoritos(id,nombre,imagen) VALUES('"+list.get(position).getId() +"','"+  list.get(position).getNombre() +"','"+ list.get(position).getImagen() +"')");
                        Toast.makeText(AlbumsView.this,"Album agregado a Favoritos",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialogBuilder.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(AlbumsView.this,"Cancelado",Toast.LENGTH_SHORT);
                    }

                });
                alertDialogBuilder.show();
            }
        });
    }

    public void progresBar(){
        progressDialog = new ProgressDialog(AlbumsView.this);
        progressDialog.setMax(20);
        progressDialog.setMessage("Loading....");
        progressDialog.setTitle("Cargando contenido");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
    Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.incrementProgressBy(1);
        }
    };

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

            Artist artist = (Artist) getItem(position);

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
    private class GetAlbum extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {

            super.onPreExecute();
        }
        protected void onPostExecute(Boolean result) {
            if (result){
                JSONObject json = null;    // create JSON obj from string
                String imagen = "";
                try {
                    json = new JSONObject(contenido);
                    JSONArray json3 = json.getJSONArray("items");
                    Log.e("contenido",json.getJSONArray("items").getJSONObject(1).getJSONArray("images").getJSONObject(0).getString("url").toString());
                    list = new ArrayList<Artist>();
                    for(int i = 0; i < json3.length(); i++){
                        String img = json.getJSONArray("items").getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url").toString();
                        String name = json.getJSONArray("items").getJSONObject(i).getString("name");
                        String id = json.getJSONArray("items").getJSONObject(i).getString("id");
                        Log.e("id", id);
                        if(img.isEmpty()){
                            imagen = "http://vignette2.wikia.nocookie.net/assassinscreed/images/3/39/Not-found.jpg/revision/latest?cb=20110517171552";
                        }else{
                            imagen = img.toString();
                        }
                        list.add(new Artist(name, imagen, id));
                    }
                    ArrayAdapter<Artist> adapter = new AlbumsView.CustomAdapter(AlbumsView.this,R.layout.layout_album,list);
                    listAlbum.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(AlbumsView.this,"No existen coincidencias",Toast.LENGTH_SHORT).show();
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
