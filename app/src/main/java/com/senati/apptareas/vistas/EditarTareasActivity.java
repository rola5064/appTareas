package com.senati.apptareas.vistas;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.senati.apptareas.R;
import com.senati.apptareas.bd.ConexionSQLiteHelper;

public class EditarTareasActivity extends AppCompatActivity {

    private TextView tvTaskTitle, btnDelete;
    private EditText etTaskDescription, etTaskId, etAssignedTo, etCreatedDate, etDueDate;
    private RadioGroup rgStatus;
    private RadioButton rbPending, rbInProgress, rbCompleted;
    private Button btnEditTask;

    private ConexionSQLiteHelper connHelper;
    private int idTareaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_tareas); // Vincula con editar_tareas.xml

        connHelper = new ConexionSQLiteHelper(this);

        // 1. Enlazamos los componentes con los IDs de tu XML
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskId = findViewById(R.id.etTaskId);
        etAssignedTo = findViewById(R.id.etAssignedTo);
        etCreatedDate = findViewById(R.id.etCreatedDate);
        etDueDate = findViewById(R.id.etDueDate);
        rgStatus = findViewById(R.id.rgStatus);
        rbPending = findViewById(R.id.rbPending);
        rbInProgress = findViewById(R.id.rbInProgress);
        rbCompleted = findViewById(R.id.rbCompleted);
        btnDelete = findViewById(R.id.btnDelete);
        btnEditTask = findViewById(R.id.btnEditTask);

        // 2. Recibimos el ID enviado desde el MainActivity al hacer clic en una tarjeta
        idTareaSeleccionada = getIntent().getIntExtra("TAREA_ID", -1);

        // 3. Cargamos los datos actuales de SQLite en los campos de texto
        cargarDatosTarea();

        // 4. Acción del botón para GUARDAR CAMBIOS (UPDATE)
        btnEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarTarea();
            }
        });

        // 5. Acción del botón para ELIMINAR (DELETE)
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarTarea();
            }
        });
    }

    private void cargarDatosTarea() {
        SQLiteDatabase db = connHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tareas WHERE id = ?", new String[]{String.valueOf(idTareaSeleccionada)});

        if (cursor.moveToFirst()) {
            etTaskId.setText(String.valueOf(idTareaSeleccionada));
            tvTaskTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
            etTaskDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
            etDueDate.setText(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
            etCreatedDate.setText("10/09/2026");
            etAssignedTo.setText("Kevin Muñoz");

            // Marcamos el RadioButton correcto según lo que guardó la base de datos
            String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
            if (estado.equalsIgnoreCase("En Progreso")) {
                rbInProgress.setChecked(true);
            } else if (estado.equalsIgnoreCase("Completada")) {
                rbCompleted.setChecked(true);
            } else {
                rbPending.setChecked(true);
            }
        }
        cursor.close();
        db.close();
    }

    private void actualizarTarea() {
        String nuevaDesc = etTaskDescription.getText().toString().trim();
        String nuevaFecha = etDueDate.getText().toString().trim();

        String nuevoEstado = "Pendiente";
        if (rbInProgress.isChecked()) nuevoEstado = "En Progreso";
        if (rbCompleted.isChecked()) nuevoEstado = "Completada";

        SQLiteDatabase db = connHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("descripcion", nuevaDesc);
        valores.put("fecha", nuevaFecha);
        valores.put("estado", nuevoEstado);

        // Modifica la fila exacta en SQLite usando el ID
        int filasAfectadas = db.update("tareas", valores, "id = ?", new String[]{String.valueOf(idTareaSeleccionada)});
        db.close();

        if (filasAfectadas > 0) {
            Toast.makeText(this, "¡Tarea modificada con éxito!", Toast.LENGTH_SHORT).show();
            finish(); // Cierra y vuelve al MainActivity
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarTarea() {
        SQLiteDatabase db = connHelper.getWritableDatabase();
        // Borra físicamente el registro de la tabla
        int filasBorradas = db.delete("tareas", "id = ?", new String[]{String.valueOf(idTareaSeleccionada)});
        db.close();

        if (filasBorradas > 0) {
            Toast.makeText(this, "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}
