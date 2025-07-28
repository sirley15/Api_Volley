package com.example.apis_volley;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextBody;
    private Button buttonSubmit;
    private TextView textViewApiResult;

    private RequestQueue requestQueue;

    // URL de la API de JSONPlaceholder para posts (la misma que para GET)
    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextBody = findViewById(R.id.editTextBody);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        textViewApiResult = findViewById(R.id.textViewApiResult);

        // Inicializar la cola de solicitudes de Volley
        requestQueue = Volley.newRequestQueue(this);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos del usuario
                String title = editTextTitle.getText().toString().trim();
                String body = editTextBody.getText().toString().trim();

                if (title.isEmpty() || body.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Llamar al método para crear el post
                    createPost(title, body);
                }
            }
        });
    }

    private void createPost(String title, String body) {
        // Construir el objeto JSON que se enviará en el cuerpo de la solicitud
        JSONObject postData = new JSONObject();
        try {
            postData.put("title", title);
            postData.put("body", body);
            postData.put("userId", 1); // JSONPlaceholder requiere un userId
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crear la solicitud POST
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, // Método POST
                API_URL,
                postData, // El objeto JSON que se enviará
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // La solicitud fue exitosa, procesar la respuesta
                        try {
                            String id = response.getString("id");
                            String responseTitle = response.getString("title");
                            String responseBody = response.getString("body");

                            String result = "Post creado exitosamente:\n" +
                                    "ID: " + id + "\n" +
                                    "Título: " + responseTitle + "\n" +
                                    "Cuerpo: " + responseBody;
                            textViewApiResult.setText(result);
                            Toast.makeText(MainActivity.this, "Post creado con ID: " + id, Toast.LENGTH_LONG).show();

                            // Opcional: limpiar los campos después de un envío exitoso
                            editTextTitle.setText("");
                            editTextBody.setText("");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al parsear la respuesta JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores de la solicitud
                        Toast.makeText(MainActivity.this, "Error al crear post: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        textViewApiResult.setText("Error al conectar con la API: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            // Opcional: Sobreescribir getHeaders() si necesitas añadir encabezados (ej., para autenticación)
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Indicamos que el cuerpo es JSON
                // headers.put("Authorization", "Bearer your_token_here"); // Si necesitaras un token de autenticación
                return headers;
            }
        };

        // Añadir la solicitud a la cola de Volley
        requestQueue.add(jsonObjectRequest);
    }
}
