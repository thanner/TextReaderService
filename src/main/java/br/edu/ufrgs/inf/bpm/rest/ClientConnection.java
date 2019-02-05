package br.edu.ufrgs.inf.bpm.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientConnection {

    public static boolean makeConnectionTest(String urlConnection) {
        boolean hasConnected = false;
        try {
            URL url = new URL(urlConnection);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            hasConnected = connection.getResponseCode() == HttpURLConnection.HTTP_OK;
            connection.disconnect();
        } catch (IOException ignored) {
        }
        return hasConnected;
    }

    public static String makeConnection(String urlConnection, String input) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlConnection);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "text/plain");

            if (!input.isEmpty()) {
                OutputStream os = connection.getOutputStream();
                os.write(input.getBytes());
                os.flush();
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + connection.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            String aux;

            while ((aux = br.readLine()) != null) {
                response.append(aux).append("\n");
            }
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

}
