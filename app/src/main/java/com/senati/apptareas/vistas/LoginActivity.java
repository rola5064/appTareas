package com.senati.apptareas.vistas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.senati.apptareas.MainActivity;
import com.senati.apptareas.R;
import com.senati.apptareas.bd.ConexionSQLiteHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;

    // Declaramos el ayudante de la Base de Datos
    private ConexionSQLiteHelper connHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logingestask);

        // Inicializamos el ayudante de SQLite
        connHelper = new ConexionSQLiteHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // VALIDACIÓN REAL CON SQLITE
                if (validarUsuario(email, password)) {
                    Toast.makeText(LoginActivity.this, "¡Bienvenido a GesTask!", Toast.LENGTH_SHORT).show();

                    // Ir a la pantalla principal
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Método que consulta en la base de datos si las credenciales son correctas
     */
    private boolean validarUsuario(String email, String password) {
        // 1. Abrimos la base de datos en modo lectura
        SQLiteDatabase db = connHelper.getReadableDatabase();
        boolean usuarioValido = false;

        // 2. Definimos la consulta SQL (Buscamos un correo y contraseña exactos)
        String query = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
        String[] parametros = {email, password};

        // 3. Ejecutamos la consulta usando un Cursor
        Cursor cursor = db.rawQuery(query, parametros);

        // 4. Si el cursor tiene al menos un registro, significa que los datos existen y coinciden
        if (cursor.moveToFirst()) {
            usuarioValido = true;
        }

        // 5. Cerramos los recursos para liberar memoria del celular
        cursor.close();
        db.close();

        return usuarioValido;
    }
}
