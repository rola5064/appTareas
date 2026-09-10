package com.senati.apptareas.vistas;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.senati.apptareas.R;
import com.senati.apptareas.bd.ConexionSQLiteHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NuevaTareaActivity extends AppCompatActivity {

    // Componentes de la interfaz
    private EditText etNewTitle, etNewDescription, etNewStatus;
    private EditText etNewAssignedUser, etNewCreatedDate, etNewDueDate;
    private TextView btnCloseDialog, btnCancelNewTask;
    private Button btnCreateTask;

    private ConexionSQLiteHelper connHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nueva_tarea);

        // Inicializamos el ayudante de la base de datos
        connHelper = new ConexionSQLiteHelper(this);

        // Enlazamos todos los componentes del XML con Java (Parte 1 y Parte 2)
        btnCloseDialog = findViewById(R.id.btnCloseDialog);
        btnCancelNewTask = findViewById(R.id.btnCancelNewTask);
        btnCreateTask = findViewById(R.id.btnCreateTask);

        etNewTitle = findViewById(R.id.etNewTitle);
        etNewDescription = findViewById(R.id.etNewDescription);
        etNewStatus = findViewById(R.id.etNewStatus);
        etNewAssignedUser = findViewById(R.id.etNewAssignedUser);
        etNewCreatedDate = findViewById(R.id.etNewCreatedDate);
        etNewDueDate = findViewById(R.id.etNewDueDate);

        // Ponemos automáticamente la fecha de hoy en el campo de creación
        String fechaHoy = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault()).format(new Date());
        etNewCreatedDate.setText(fechaHoy);

        // Acciones para cerrar el formulario (X y Cancelar)
        View.OnClickListener cerrarPantalla = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra esta actividad y regresa a la anterior
            }
        };
        btnCloseDialog.setOnClickListener(cerrarPantalla);
        btnCancelNewTask.setOnClickListener(cerrarPantalla);

        // Acción del botón principal "Crear Tabla / Guardar Tarea"
        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarTarea();
            }
        });
    }

    /**
     * Captura los datos del formulario e inserta el registro en la tabla de SQLite
     */
    private void registrarTarea() {
        String titulo = etNewTitle.getText().toString().trim();
        String descripcion = etNewDescription.getText().toString().trim();
        String estado = etNewStatus.getText().toString().trim();
        String fechaVencimiento = etNewDueDate.getText().toString().trim();
        String fechaCreacion = etNewCreatedDate.getText().toString().trim();

        // Validamos campos obligatorios (Título y Vencimiento tienen asterisco *)
        if (titulo.isEmpty() || fechaVencimiento.isEmpty()) {
            Toast.makeText(this, "Por favor, llene los campos obligatorios (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si el estado se dejó en blanco, le ponemos uno por defecto
        if (estado.isEmpty()) {
            estado = "Pendientes";
        }

        // 1. Abrimos la base de datos en modo escritura
        SQLiteDatabase db = connHelper.getWritableDatabase();

        // 2. Agrupamos los valores a insertar usando un contenedor ContentValues
        ContentValues valores = new ContentValues();
        valores.put("titulo", titulo);
        valores.put("descripcion", descripcion);
        valores.put("estado", estado);
        valores.put("fecha", fechaVencimiento);
        valores.put("usuario_id", 1); // Asociado temporalmente al usuario administrador por defecto (id: 1)

        // 3. Ejecutamos el comando INSERT de SQLite de forma segura
        long resultado = db.insert("tareas", null, valores);
        db.close(); // Cerramos la conexión para no corromper el archivo .db

        // 4. Verificamos si se guardó con éxito
        if (resultado != -1) {
            Toast.makeText(this, "¡Tarea guardada exitosamente!", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la pantalla y regresa al panel principal
        } else {
            Toast.makeText(this, "Error al intentar registrar la tarea", Toast.LENGTH_SHORT).show();
        }
    }
}
