package com.tecnoservicescr.songo.sqliterecordatorios.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class recordatorioSQLiteHelper extends SQLiteOpenHelper {

    //String sentencia SQL para crear la clase del modelo
    String sqlCreateRecordatorio = "CREATE TABLE Recordatorio (Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Nombre TEXT, Texto TEXT)";

    public recordatorioSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //Ejecuta sentencia sql para crear la tabla
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateRecordatorio);
    }

    //Ejecuta sentencia sql para crear subir la tabla, elimina y crea la tabla del modelo
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Recordatorio");

        db.execSQL(sqlCreateRecordatorio);
    }
}
