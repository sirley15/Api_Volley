package com.example.apis_volley;

import android.annotation.SuppressLint;
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

public class MainActivity2 extends AppCompatActivity {

    private EditText editTextPostId;
    private EditText editTextNewTitle;
    private EditText editTextNewBody;
    private Button buttonUpdate;
    private TextView textViewApiResult;

    private RequestQueue requestQueue;

    // URL base de la API de JSONPlaceholder para posts
    private static final String BASE_API_URL = "https://jsonplaceholder.typicode.com/posts/";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editTextPostId = findViewById(R.id.editTextPostId);
        editTextNewTitle = findViewById(R.id.editTextNewTitle);
        editTextNewBody = findViewById(R.id.editTextNewBody);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        textViewApiResult = findViewById(R.id.textViewApiResult);

        // Inicializar la cola de solicitudes de Volley
        requestQueue = Volley.newRequestQueue(this);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postId = editTextPostId.getText().toString().trim();
                String newTitle = editTextNewTitle.getText().toString().trim();
                String newBody = editTextNewBody.getText().toString().trim();

                if (postId.isEmpty() || newTitle.isEmpty() || newBody.isEmpty()) {
                    Toast.makeText(MainActivity2.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Llamar al método para actualizar el post
                    updatePost(postId, newTitle, newBody);
                }
            }
        });
    }
    private void updatePost(String postId, String newTitle, String newBody) {
        // Construir la URL completa para el post específico
        String apiUrl = BASE_API_URL + postId;

        // Construir el objeto JSON que se enviará en el cuerpo de la solicitud
        JSONObject postData = new JSONObject();
        try {
            // Para una solicitud PUT, generalmente se envían todos los campos que se quieren actualizar.
            // JSONPlaceholder requiere el userId, incluso si no lo actualizamos.
            postData.put("id", Integer.parseInt(postId)); // Asegurarse de enviar el ID en el cuerpo también si la API lo espera
            postData.put("title", newTitle);
            postData.put("body", newBody);
            postData.put("userId", 1); // Asumimos un userId fijo para JSONPlaceholder
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crear la solicitud PUT
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, // Método PUT
                apiUrl,
                postData, // El objeto JSON con los datos actualizados
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // La solicitud fue exitosa, procesar la respuesta
                        try {
                            String id = response.getString("id");
                            String responseTitle = response.getString("title");
                            String responseBody = response.getString("body");
                            int userId = response.getInt("userId");

                            String result = "Post actualizado exitosamente:\n" +
                                    "ID: " + id + "\n" +
                                    "Título: " + responseTitle + "\n" +
                                    "Cuerpo: " + responseBody + "\n" +
                                    "UserID: " + userId;
                            textViewApiResult.setText(result);
                            Toast.makeText(MainActivity2.this, "Post " + id + " actualizado", Toast.LENGTH_LONG).show();

                            // Opcional: limpiar los campos después de un envío exitoso
                            editTextPostId.setText("");
                            editTextNewTitle.setText("");
                            editTextNewBody.setText("");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity2.this, "Error al parsear la respuesta JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores de la solicitud
                        Toast.makeText(MainActivity2.this, "Error al actualizar post: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        textViewApiResult.setText("Error al conectar con la API: " + error.getMessage());
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Añadir la solicitud a la cola de Volley
        requestQueue.add(jsonObjectRequest);
    }

}
