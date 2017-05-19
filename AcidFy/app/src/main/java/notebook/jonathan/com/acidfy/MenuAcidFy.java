package notebook.jonathan.com.acidfy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;

import notebook.jonathan.com.acidfy.model.Artist;

public class MenuAcidFy extends AppCompatActivity {
    Button btnSalir, btnFavoritos, btnBuscar;
    SQLiteDatabase db;
    List<Artist> list;
    Cursor c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_acid_fy);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnSalir = (Button) findViewById(R.id.btnSalir);
        btnFavoritos = (Button) findViewById(R.id.btnFavoritos);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuAcidFy.this,MainAcidFy.class);
                startActivity(i);
            }
        });
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        btnFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SQLUtilities conexion = new SQLUtilities(MenuAcidFy.this,"Favoritos", null,1);
            db = conexion.getWritableDatabase();
            c = db.rawQuery("SELECT id,nombre,imagen FROM Favoritos",null);
            if(c.getCount() == 0){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuAcidFy.this);
                alertDialogBuilder.setTitle("Favoritos");
                alertDialogBuilder.setMessage("Usted no posee lista de favoritos");
                alertDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
            }else {
                Intent i = new Intent(MenuAcidFy.this, FavoriteAlbums.class);
                startActivity(i);
            }

            }
        });
    }

}
