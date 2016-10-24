package de.dhbw.BackEndCommunication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import de.dhbw.exceptions.BackendCommunicationException;
import de.dhbw.exceptions.NetworkUnavailableException;

/**
 * Created by Tobias Berner on 17.10.2016.
 *
 */
public class RestBackendCommunication {

    /**
     * Führt einen Get-Request an die URL aus und gibt den Body der Serverantwort zurück.
     *
     * @param myurl  - URL für Get-Request
     * @param context - Aufrufende Activity
     * @return - Body der Serverantwort
     * @throws IOException - Wenn bei dem Zugriff auf den Input Stream ein Fehler auftritt
     * @throws BackendCommunicationException - Wenn get Request fehlschlägt
     * @throws NetworkUnavailableException - Wenn keine Internetverbindung besteht
     */
   public String getRequest(String myurl, Context context) throws IOException, BackendCommunicationException, NetworkUnavailableException {
       InputStream is = null;
       String jStringFromServer;

       ConnectivityManager connMgr = (ConnectivityManager)
               context.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
       if (networkInfo != null && networkInfo.isConnected()) {

           try {

               URL url = new URL(myurl);

               HttpURLConnection conn = (HttpURLConnection) url.openConnection();
               conn.setReadTimeout(10000 /* milliseconds */);
               conn.setConnectTimeout(15000 /* milliseconds */);
               conn.setRequestMethod("GET");

               conn.setDoInput(true);
               conn.connect();

               int response = conn.getResponseCode();
               if (response == HttpURLConnection.HTTP_OK) {
                   is = conn.getInputStream();
                   BufferedReader br = new BufferedReader(new InputStreamReader(is));
                   jStringFromServer = br.readLine();
                   br.close();
               } else {
                   throw new BackendCommunicationException(Integer.toString(response));
               }

               return jStringFromServer;
           } finally {
               //Stream schließen
               if (is != null) {
                   is.close();
               }
           }

       } else {
           //Netzwerk nicht verbunden
           throw new NetworkUnavailableException("Network not connected");
       }
   }

    /**
     *  Führt einen PostRequest an die URL aus.
     *
     * @param myurl - URL für den Post Request
     * @param jString - JSON Objekt als String, das an URL geschickt werden soll
     * @param context - Aufrufende Activity
     * @return  Body der Serverantwort
     * @throws NetworkUnavailableException - Wenn keine Internetverbindung besteht
     * @throws IOException - Wenn bei dem Zugriff auf den Input oder Output Stream ein Fehler auftritt
     * @throws BackendCommunicationException -  Wenn post Request fehlschlägt
     */
    public String postRequest(String myurl, String jString, Context context) throws NetworkUnavailableException, IOException, BackendCommunicationException {
        InputStream is = null;
        OutputStream os = null;
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            try {

                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");

                conn.setDoInput(true);
                conn.setDoOutput(true);

                os = conn.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                bw.write(jString);
                bw.flush();
                bw.close();

                conn.connect();
                int response = conn.getResponseCode();
                if (response == HttpURLConnection.HTTP_CREATED) {
                    is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String jsonAsString = br.readLine();
                    br.close();
                    return jsonAsString;
                } else {
                    throw new BackendCommunicationException(Integer.toString(response));
                }
            } finally {
                //Streams schließen
                if (is != null) {
                        is.close();
                }
                if(os != null){
                        os.close();
                }
            }
        }else {
            throw new NetworkUnavailableException("Network not connected");
        }
    }

    /**
     *  Führt einen Put-Request an die URL aus.
     *
     * @param myurl - URL für den Post Request
     * @param jString - JSON Objekt als String, das an URL geschickt werden soll
     * @param context - Aufrufende Activity
     * @return Body der Serverantwort
     * @throws NetworkUnavailableException - Wenn keine Internetverbindung besteht
     * @throws IOException - Wenn bei dem Zugriff auf den Input oder Output Stream ein Fehler auftritt
     * @throws BackendCommunicationException -  Wenn post Request fehlschlägt
     */
    public boolean putRequest(String myurl, String jString, Context context) throws NetworkUnavailableException, IOException, BackendCommunicationException {
        InputStream is = null;
        OutputStream os = null;
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            try {

                URL url = new URL(myurl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("PUT");

                conn.setDoInput(true);

                os = conn.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
                bw.write(jString);
                bw.flush();
                bw.close();

                conn.connect();
                int response = conn.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    throw new BackendCommunicationException(Integer.toString(response));
                }
            } finally {
                //Streams schließen
                if (is != null) {
                    is.close();
                    if (os != null) {
                        os.close();
                    }
                }
            }

        }else {
            throw new NetworkUnavailableException("Network not connected");
        }
    }

    /**
     * Führt ein Delete-Request and die URL aus.
     *
     * @param myurl - URL für den Delete-Request
     * @param context - Aufrufende Activity
     * @return true, wenn löschen erfolgreich
     * @throws NetworkUnavailableException - Wenn keine Internetverbindung besteht
     * @throws IOException - Wenn bei dem Zugriff auf den Input oder Output Stream ein Fehler auftritt
     * @throws BackendCommunicationException -  Wenn post Request fehlschlägt
     */
    public boolean deleteRequest(String myurl, Context context) throws NetworkUnavailableException, IOException, BackendCommunicationException {

        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
                URL url = new URL(myurl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("DELETE");

                conn.connect();
                int response = conn.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    throw new BackendCommunicationException(Integer.toString(response));
                }
                }
        else {
            throw new NetworkUnavailableException("Network not connected");
        }
    }





}
