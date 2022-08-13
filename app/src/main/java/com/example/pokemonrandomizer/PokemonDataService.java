package com.example.pokemonrandomizer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class PokemonDataService {

    public static JSONObject getRandomPokemon(JSONObject response) {
        Integer count;
        JSONObject randomPokemon = null;
        try {
            // get maximum number of pokemons
            count = response.getInt("count");
            // get random number between 0 and maximum pokemon number (1154)
            Integer random = new Random().nextInt(count); // [0, count] => [0, 1154]
            // get JSON array of all pokemon JSON objects
            JSONArray results = response.getJSONArray("results");
            // get random JSON object from JSON array
            randomPokemon = results.getJSONObject(random);
            return randomPokemon;
        } catch (JSONException e) {
            e.printStackTrace();
            // return null if error
            return randomPokemon;
        }
    }


}
