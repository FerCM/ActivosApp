package com.fernandacm.plantilla;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;

public class BaseDatos extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ActivosBD.db";

    public BaseDatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE  ACTIVOS ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nombre TEXT NOT NULL,"
                + " tipo_activo TEXT NOT NULL,"
                + " descripcion TEXT NOT NULL,"
                + " fecha_adqui TEXT NOT NULL,"
                + " imagen BLOB,"
                + " fecha_alta TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insert( articuloModal articulo) {
        SQLiteDatabase  db = this.getWritableDatabase();

        Date date = Calendar.getInstance().getTime();
        ContentValues values = new ContentValues();
        values.put("nombre", articulo.getNombre());
        values.put("tipo_activo",  articulo.getTipo_activo());
        values.put("descripcion",  articulo.getDescripcion());
        values.put("fecha_adqui",  articulo.getFecha());
        values.put("imagen", articulo.getImagen());
        values.put("fecha_alta",date.toString());

        System.out.println("Info?");
        System.out.println(values);
        return db.insert(
                "ACTIVOS",
                null,
                values);
    }
}
