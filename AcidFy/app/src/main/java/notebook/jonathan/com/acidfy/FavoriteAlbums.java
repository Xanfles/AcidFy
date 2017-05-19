package notebook.jonathan.com.acidfy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import notebook.jonathan.com.acidfy.model.Artist;

public class FavoriteAlbums extends AppCompatActivity {
    ListView listFavorite;
    List<Artist> list;
    SQLiteDatabase db;
    String contenido,id;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_albums);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        listFavorite = (ListView) findViewById(R.id.listAlbums);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras().getBundle("bundle");
        id = extras.getString("id");
        progresBar();
        SQLUtilities conexion = new SQLUtilities(FavoriteAlbums.this,"Favoritos", null,1);
        db = conexion.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT nombre FROM Favoritos",null);
        c.moveToFirst();
        Log.e("Sql",c.getString(0));
        List<String> lista = new ArrayList<String>();
        if(c.moveToFirst()){
            do {
                lista.add(c.getString(0));
            }while(c.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(FavoriteAlbums.this,android.R.layout.simple_list_item_1,lista);
        listFavorite.setAdapter(adapter);


    }

    public void progresBar(){
        progressDialog = new ProgressDialog(FavoriteAlbums.this);
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
}
