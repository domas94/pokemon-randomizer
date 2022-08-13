package com.example.pokemonrandomizer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Variable declaration
    Button btnRandom;
    TextView textName;
    TextView textFront;
    TextView textBack;
    TextView textMoves;
    TextView textStats;
    ListView lvMoves;
    ListView lvStats;
    String pokeURL;
    WebView pokeBack;
    WebView pokeFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Variable initialization
        btnRandom = findViewById(R.id.btn_random);
        textName = findViewById(R.id.text_name);
        textFront = findViewById(R.id.front_default);
        textBack = findViewById(R.id.back_default);
        textMoves = findViewById(R.id.moves);
        textStats = findViewById(R.id.stats);
        lvMoves = findViewById(R.id.lv_moves);
        lvStats = findViewById(R.id.lv_stats);
        pokeFront = findViewById(R.id.poke_front);
        pokeBack = findViewById(R.id.poke_back);

        // Code that is triggered after button click
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // URL with all available pokemon from pokeapi
                String url = "https://pokeapi.co/api/v2/pokemon?limit=100000&offset=0";
                // firstRequest used to get a random pokemon from pokeapi and get the url of the corresponding pokemon
                JsonObjectRequest firstRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject randomPokemon;
                        String randomPokeName;
                        String randomPokeURL;
                        try {

                            randomPokemon = PokemonDataService.getRandomPokemon(response);
                            // if random pokemon is null, draw ditto instead
                            if (randomPokemon == null) {
                                textName.setText("Ditto");
                                pokeURL = "https://pokeapi.co/api/v2/pokemon/132/";

                            } else {
                                // get random pokemon name
                                randomPokeName = randomPokemon.getString("name");
                                // make first letter upperacase
                                randomPokeName = randomPokeName.substring(0, 1).toUpperCase() + randomPokeName.substring(1).toLowerCase();
                                textName.setText(randomPokeName);
                                // get random pokemon url
                                randomPokeURL = randomPokemon.getString("url");
                                pokeURL = randomPokeURL;
                            }

                            // secondRequest used to get the moves, stats, front and back images of a random pokemon
                            JsonObjectRequest secondRequest = new JsonObjectRequest(Request.Method.GET, pokeURL, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        // get array of pokemon moves
                                        JSONArray moves = response.getJSONArray("moves");
                                        // if pokemon has 0 moves, inform
                                        ArrayList<String> moveList = new ArrayList<>();
                                        if (moves.length() == 0) {
                                            textMoves.setText("No moves found");
                                        } else {
                                            textMoves.setText("Moves");
                                        }
                                        // iterate through pokemon moves
                                        for (int i = 0; i < moves.length(); i++) {
                                            JSONObject index = moves.getJSONObject(i);
                                            JSONObject move = index.getJSONObject("move");
                                            String moveName = move.getString("name");
                                            // add move name to the list of pokemon moves
                                            moveList.add(moveName);

                                        }
                                        ArrayAdapter moveAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, moveList);
                                        lvMoves.setAdapter(moveAdapter);

                                        // get array of pokemon stats
                                        JSONArray stats = response.getJSONArray("stats");
                                        // iterate through pokemon stats
                                        ArrayList<String> statList = new ArrayList<>();
                                        // if pokemon has 0 stats, inform
                                        if (stats.length() == 0) {
                                            textStats.setText("No stats found");
                                        } else {
                                            textStats.setText("Stats");
                                        }
                                        for (int i = 0; i < stats.length(); i++) {
                                            JSONObject index = stats.getJSONObject(i);
                                            int statValue = index.getInt("base_stat");
                                            String stringStatValue = String.valueOf(statValue);
                                            JSONObject stat = index.getJSONObject("stat");
                                            String statName = stat.getString("name");
                                            // add stat name and stat value to the list of pokemon stats
                                            statList.add(statName + " " + stringStatValue);

                                        }
                                        ArrayAdapter statAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, statList);
                                        lvStats.setAdapter(statAdapter);

                                        JSONObject sprites = response.getJSONObject("sprites");
                                        // get front pokemon image
                                        String front_url = sprites.getString("front_default");
                                        // get back pokemon image
                                        String back_url = sprites.getString("back_default");
                                        // if front url or back url are null, draw shining ditto
                                        if (front_url == "null") {
                                            textFront.setText("Shining Ditto appears!");
                                            Toast.makeText(MainActivity.this, "Front image not found", Toast.LENGTH_SHORT).show();
                                            pokeFront.loadUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/shiny/132.png");
                                        } else {
                                            textFront.setText("Front image");
                                            pokeFront.loadUrl(front_url);
                                        }
                                        if (back_url == "null") {
                                            textBack.setText("Shining Ditto appears!");
                                            Toast.makeText(MainActivity.this, "Back image not found", Toast.LENGTH_SHORT).show();
                                            pokeBack.loadUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/shiny/132.png");
                                        } else {
                                            textBack.setText("Back image");
                                            pokeBack.loadUrl(back_url);
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, "Second request error", Toast.LENGTH_SHORT).show();

                                }
                            });
                            // create single instance of RequestQueue to increase efficiency
                            RequestSingleton.getInstance(MainActivity.this).addToRequestQueue(secondRequest);
                            //Volley.newRequestQueue(getApplicationContext()).add(request2);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "First request error", Toast.LENGTH_SHORT).show();

                    }
                });
                // create single instance of RequestQueue to increase efficiency
                RequestSingleton.getInstance(MainActivity.this).addToRequestQueue(firstRequest);
                //Volley.newRequestQueue(getApplicationContext()).add(request);


            }
        });

    }
}