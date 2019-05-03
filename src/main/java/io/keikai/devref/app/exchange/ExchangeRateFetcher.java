package io.keikai.devref.app.exchange;


import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * fetch from https://exchangeratesapi.io/
 */
public class ExchangeRateFetcher {

    private static String GET_URL = "https://api.exchangeratesapi.io/latest";

    public static Map<String, Double> fetch() throws IOException {
        URL obj = new URL(GET_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new Gson();
            Map rates = (Map)gson.fromJson(response.toString(), Map.class).get("rates");
            return rates;
        } else {
            throw new IOException("failed to fetch exchange rates");
        }

    }

//    static private JsonElement parseResponse(String response) {
//

//    }
    public static void main(String[] args) throws IOException {
        ExchangeRateFetcher.fetch();
    }
}
