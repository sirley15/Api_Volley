package com.example.apis_volley;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private RequestQueue requestQueue;

    // URL de la API de JSONPlaceholder para posts
    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        // Inicializar la cola de solicitudes de Volley
        requestQueue = Volley.newRequestQueue(this);

        // Llamar al método para obtener los posts de la API
        fetchPosts();
    }

    private void fetchPosts() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null, // No se envía cuerpo en una solicitud GET
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Procesar la respuesta JSON
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject postObject = response.getJSONObject(i);

                                int userId = postObject.getInt("userId");
                                int id = postObject.getInt("id");
                                String title = postObject.getString("title");
                                String body = postObject.getString("body");

                                Post post = new Post(userId, id, title, body);
                                postList.add(post);
                            }
                            // Notificar al adaptador que los datos han cambiado
                            postAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al parsear JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores de la solicitud
                        Toast.makeText(MainActivity.this, "Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
        );

        // Añadir la solicitud a la cola de Volley
        requestQueue.add(jsonArrayRequest);
    }
}
