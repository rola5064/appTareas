package com.senati.apptareas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.senati.apptareas.bd.ConexionSQLiteHelper;
import com.senati.apptareas.vistas.NuevaTareaActivity;
import com.senati.apptareas.vistas.EditarTareasActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnNuevaTarea;
    private EditText etBuscar;

    private TextView btnTodas, btnPendientes, btnCompletadas;
    private LinearLayout containerTareas;
    private ConexionSQLiteHelper connHelper;

    private String filtroActual = "Todas";
    // Variable para almacenar el texto que escribe el usuario en la barra de búsqueda
    private String textoBusqueda = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connHelper = new ConexionSQLiteHelper(this);
        containerTareas = findViewById(R.id.containerTareas);

        btnNuevaTarea = findViewById(R.id.btnNuevaTarea);
        etBuscar = findViewById(R.id.etBuscar);

        btnTodas = findViewById(R.id.btnTodas);
        btnPendientes = findViewById(R.id.btnPendientes);
        btnCompletadas = findViewById(R.id.btnCompletadas);

        btnNuevaTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NuevaTareaActivity.class);
                startActivity(intent);
            }
        });

        // EVENTOS DE CLIC PARA FILTRAR PESTAÑAS
        btnTodas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroActual = "Todas";
                actualizarEstiloBotones();
                cargarTareasDesdeBD();
            }
        });

        btnPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroActual = "Pendiente";
                actualizarEstiloBotones();
                cargarTareasDesdeBD();
            }
        });

        btnCompletadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtroActual = "Completada";
                actualizarEstiloBotones();
                cargarTareasDesdeBD();
            }
        });

        // ===================================================================
        // MOTOR DE BÚSQUEDA EN TIEMPO REAL (TEXTWATCHER)
        // ===================================================================
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita implementar nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Capturamos el texto en vivo y actualizamos la lista
                textoBusqueda = s.toString().trim();
                cargarTareasDesdeBD();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita implementar nada aquí
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarEstiloBotones();
        cargarTareasDesdeBD();
    }

    /**
     * Altera el diseño del óvalo azul con letras blancas para la pestaña activa
     */
    private void actualizarEstiloBotones() {
        btnTodas.setBackgroundResource(0);
        btnTodas.setTextColor(Color.BLACK);
        btnTodas.setTypeface(null, Typeface.NORMAL);

        btnPendientes.setBackgroundResource(0);
        btnPendientes.setTextColor(Color.BLACK);
        btnPendientes.setTypeface(null, Typeface.NORMAL);

        btnCompletadas.setBackgroundResource(0);
        btnCompletadas.setTextColor(Color.BLACK);
        btnCompletadas.setTypeface(null, Typeface.NORMAL);

        if (filtroActual.equals("Todas")) {
            btnTodas.setBackgroundResource(R.drawable.bg_icono);
            btnTodas.setTextColor(Color.WHITE);
            btnTodas.setTypeface(null, Typeface.BOLD);
        } else if (filtroActual.equals("Pendiente")) {
            btnPendientes.setBackgroundResource(R.drawable.bg_icono);
            btnPendientes.setTextColor(Color.WHITE);
            btnPendientes.setTypeface(null, Typeface.BOLD);
        } else if (filtroActual.equals("Completada")) {
            btnCompletadas.setBackgroundResource(R.drawable.bg_icono);
            btnCompletadas.setTextColor(Color.WHITE);
            btnCompletadas.setTypeface(null, Typeface.BOLD);
        }
    }

    /**
     * Consulta SQLite filtrando por pestaña y por el texto de la barra de búsqueda (LIKE)
     */
    private void cargarTareasDesdeBD() {
        if (containerTareas == null) return;

        containerTareas.removeAllViews();
        SQLiteDatabase db = connHelper.getReadableDatabase();
        Cursor cursor;

        // Estructura de la query base
        String query;
        String[] parametros;

        if (filtroActual.equals("Todas")) {
            // Buscamos en todo si coincide con el título o descripción
            query = "SELECT * FROM tareas WHERE (titulo LIKE ? OR descripcion LIKE ?) ORDER BY id DESC";
            parametros = new String[]{"%" + textoBusqueda + "%", "%" + textoBusqueda + "%"};
        } else {
            // Buscamos respetando el estado de la pestaña y que coincida el texto
            query = "SELECT * FROM tareas WHERE estado = ? AND (titulo LIKE ? OR descripcion LIKE ?) ORDER BY id DESC";
            parametros = new String[]{filtroActual, "%" + textoBusqueda + "%", "%" + textoBusqueda + "%"};
        }

        cursor = db.rawQuery(query, parametros);

        if (cursor.moveToFirst()) {
            do {
                final int idTarea = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

                View tarjeta = LayoutInflater.from(this).inflate(R.layout.item_tarea, containerTareas, false);

                TextView tvTitulo = tarjeta.findViewById(R.id.tvItemTitulo);
                TextView tvEstado = tarjeta.findViewById(R.id.tvItemEstado);
                TextView tvDesc = tarjeta.findViewById(R.id.tvItemDescripcion);
                TextView tvFecha = tarjeta.findViewById(R.id.tvItemFecha);
                ImageView imgItemEstado = tarjeta.findViewById(R.id.imgItemEstado);

                tvTitulo.setText(titulo);
                tvEstado.setText("Estado: " + estado);
                tvDesc.setText(descripcion);
                tvFecha.setText(fecha);

                // ASIGNACIÓN DINÁMICA DE COLORES E ICONOS
                if (estado.equalsIgnoreCase("Pendiente") || estado.equalsIgnoreCase("Pendientes")) {
                    tvEstado.setTextColor(Color.parseColor("#FF9100"));
                    imgItemEstado.setImageResource(android.R.drawable.ic_dialog_alert);
                } else if (estado.equalsIgnoreCase("Completada") || estado.equalsIgnoreCase("Completadas")) {
                    tvEstado.setTextColor(Color.parseColor("#388E3C"));
                    imgItemEstado.setImageResource(R.drawable.check);
                } else {
                    tvEstado.setTextColor(Color.parseColor("#888888"));
                    imgItemEstado.setImageResource(android.R.drawable.ic_menu_help);
                }

                tarjeta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, EditarTareasActivity.class);
                        intent.putExtra("TAREA_ID", idTarea);
                        startActivity(intent);
                    }
                });

                containerTareas.addView(tarjeta);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
}
