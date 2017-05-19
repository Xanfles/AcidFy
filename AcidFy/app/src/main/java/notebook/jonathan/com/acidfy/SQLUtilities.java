package notebook.jonathan.com.acidfy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Soporte on 22/07/2016.
 */
public class SQLUtilities extends SQLiteOpenHelper {
    String sqlCreate = "CREATE TABLE Favoritos (id TEXT, nombre TEXT, imagen TEXT)";

    public SQLUtilities(Context contexto, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto,nombre,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
