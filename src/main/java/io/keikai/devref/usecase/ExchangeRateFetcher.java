package io.keikai.devref.usecase;


import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * fetch exchange rates from https://exchangeratesapi.io/
 */
public class ExchangeRateFetcher {

    public static String[] interested_currency_list = new String[]{"USD", "GBP", "AUD", "CHF", "NZD", "JPY", "CAD"};
    private static String access_key = "b3a129fbe94e768ee520ec48788c6261";
    private static String EXCHANGE_RATES_URL = "http://api.exchangeratesapi.io/v1/latest?access_key=" + access_key
            + "&symbol=" + String.join(",", interested_currency_list);

    public static Map<String, Double> fetch() throws IOException {
        URL url = new URL(EXCHANGE_RATES_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
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

    public static void main(String[] args) throws IOException {
        Map rates = ExchangeRateFetcher.fetch();
        Iterator<String> currencyIterator = rates.keySet().iterator();
        while (currencyIterator.hasNext()) {
            String currency = currencyIterator.next();
            System.out.println(String.format("%s  %s", currency, rates.get(currency)));
        }
    }
}
