package com.senati.apptareas.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    // Nombre del archivo de la base de datos (Este archivo lo podrás abrir en SQLite Database Browser)
    private static final String DATABASE_NAME = "gestask.db";
    private static final int DATABASE_VERSION = 1;

    // Sentencia SQL para crear la tabla de Usuarios
    private static final String CREAR_TABLA_USUARIOS = "CREATE TABLE usuarios (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "email TEXT UNIQUE, " +
            "password TEXT);";

    // Sentencia SQL para crear la tabla de Tareas
    private static final String CREAR_TABLA_TAREAS = "CREATE TABLE tareas (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "titulo TEXT, " +
            "descripcion TEXT, " +
            "estado TEXT, " + // "Todas", "Pendientes", "En Progreso"
            "fecha TEXT, " +
            "usuario_id INTEGER, " +
            "FOREIGN KEY(usuario_id) REFERENCES usuarios(id));";

    public ConexionSQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ejecutamos las sentencias para crear las tablas al iniciar la app por primera vez
        db.execSQL(CREAR_TABLA_USUARIOS);
        db.execSQL(CREAR_TABLA_TAREAS);

        // Insertamos un usuario administrador por defecto para que puedas probar el login de inmediato con la BD
        db.execSQL("INSERT INTO usuarios (email, password) VALUES ('admin@gestask.com', '1234');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si la base de datos se actualiza, borramos las tablas viejas y las creamos de nuevo
        db.execSQL("DROP TABLE IF EXISTS tareas");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }
}
