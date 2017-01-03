package com.pcgeekbrain.bplocate;

import android.os.AsyncTask;
import android.util.Log;

import com.pcgeekbrain.bplocate.interfaces.AsyncResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
            switch (eventType){
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item")){
                        currentBranch = new Branch();
                    } else if (currentBranch != null){
                        if (name.equalsIgnoreCase("title")){
                            currentBranch.setName(parser.nextText());
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
        String[] hours = new String[]{"n/a","n/a","n/a","n/a","n/a","n/a","n/a"};

        //TODO PARSE HTML
        Document doc = Jsoup.parse(HTMLData);
        Element table = doc.select("table").first();
        Iterator<Element> ite = table.select("td").iterator();
        ite.next(); //Address:
        String address = ite.next().text();
        address = address.replaceAll("Brooklyn, NY", "\nBrooklyn, NY ");
        Log.d(TAG, "parseHTMLData: address -> "+address);
        currentBranch.setAddress(address);
        ite.next(); //Phone:
        currentBranch.setNumber(ite.next().text());
        ite.next();  //Monday
        hours[0] = ite.next().text();
        ite.next();  //Tuesday
        hours[1] = ite.next().text();
        ite.next();  //Wednesday
        hours[2] = ite.next().text();
        ite.next();  //Thursday
        hours[3] = ite.next().text();
        ite.next();  //Friday
        hours[4] = ite.next().text();
        ite.next();  //Saturday
        hours[5] = ite.next().text();
        ite.next();  //Sunday
        hours[6] = ite.next().text();
        currentBranch.setHours(hours);
    }
}
