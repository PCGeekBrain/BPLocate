package com.pcgeekbrain.bplocate;

import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.util.Log;

import com.pcgeekbrain.bplocate.interfaces.AsyncResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Mendel on 12/12/2016.
 * Static class for updating data.
 */

public class UpdateData extends AsyncTask<String, Void, ArrayList<Branch>>{
    private static final String TAG = "UPDATE DATA";
    private AsyncResponse response;

    public UpdateData(AsyncResponse response){
        this.response = response;
    }

    @Override
    protected ArrayList<Branch> doInBackground(String... urls) {
        return downloadData(urls[0]);
    }
    @Override
    protected void onPostExecute(ArrayList<Branch> result) {
        response.processFinish(result);
    }

    private ArrayList<Branch> downloadData(String input_url) {
        ArrayList<Branch> result = new ArrayList<>();
        InputStream inputStream = null;
        String data = null;
        //download the data
        try {
            try {
                //Create the url and the connection object
                URL url = new URL(input_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000 /*10 Sec*/);
                connection.setConnectTimeout(15000 /*15 Sec*/);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();   //Connect.
                //Post Connection operations:
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "response code: " + responseCode);
                inputStream = connection.getInputStream();
                //convert the input stream into a string
                XmlPullParser xmlParser = XmlPullParserFactory.newInstance().newPullParser();
                xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlParser.setInput(inputStream, null);
                result = parseXMLData(xmlParser);
            } finally {
                if (inputStream != null) {inputStream.close();}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ArrayList<Branch> parseXMLData(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Branch> result = new ArrayList<>();
        int eventType = parser.getEventType();
        Branch currentBranch = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            Log.d(TAG, "parseXMLData: eventType -> " + eventType);
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item")){
                        currentBranch = new Branch();
                    } else if (currentBranch != null){
                        if (name.equalsIgnoreCase("title")){
                            currentBranch.name = parser.nextText();
                        } else if (name.equalsIgnoreCase("description")){
                            parseHTMLData(parser.nextText(), currentBranch);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && currentBranch != null){
                        result.add(currentBranch);
                    }
            }
            eventType = parser.next();
        }

        return result;
    }
    private void parseHTMLData(String HTMLData, Branch currentBranch){
        Log.d(TAG, "parseHTMLData: STARTED");
        String address = "Unknown", number = "xxx-xxx-xxxx";
        String[] hours = new String[]{"n/a","n/a","n/a","n/a","n/a","n/a","n/a"};

        //TODO PARSE HTML
        Document doc = Jsoup.parse(HTMLData);
        Element table = doc.select("table").first();
        Elements ite = table.select("td");
        for (Element item : ite){
            Log.d(TAG, "parseHTMLData: item.text -> "+item.text());
        }
    }

    private String readInput(BufferedReader bufferedReader) {
        String status = "" , result = "";
        int lines = 0;

        while (status != null)
            try {
                status = bufferedReader.readLine();
                if (status != null){
                    result += status;
                    lines++;
                }
            } catch (Exception e) {
                status = null;
                Log.e(TAG, "readInput -> Exception:" + e.getMessage() +"\n", e.fillInStackTrace());
            }
        Log.d(TAG, "readInput -> Lines: " + lines);
        return result;
    }
}
