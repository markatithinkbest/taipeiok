package com.ithinkbest.taipeiok;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Created by u1 on 2015/4/2.
 * http://www.vogella.com/tutorials/AndroidServices/article.html
 */
public class UpdateService extends Service {

    static String LOG_TAG = "MARK987";
    String[] Taipei_District;
    String[] Certification_Category;
    static boolean[] cat_updated={false,false,false,false,false,false,false,false,false,false,false,false,false};


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Log.d(LOG_TAG, "...onStartCommand");

        Taipei_District =getResources().getStringArray(R.array.taipei_district);
        Certification_Category =getResources().getStringArray(R.array.certification_category);
        int [] catArray=intent.getIntArrayExtra("CATS");
        for (int i=0;i<catArray.length;i++){
            int intCat=catArray[i];
            Log.d(LOG_TAG, "...cat="+intCat);
            if (cat_updated[intCat]) {
                // updated, do nothing
                Log.d(LOG_TAG, "...cat_updated="+cat_updated[intCat]);

            }else {
                Log.d(LOG_TAG, "...cat_updated="+cat_updated[intCat]);
                processJson(intCat);
                cat_updated[intCat]=true;
            }
        }

        return Service.START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void processJson(int cat) {

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Vector<ContentValues> cVVector = null;


        String strJson = readRawJson(cat);







        if (strJson==null || strJson.length()==0){
            Log.d(LOG_TAG, "NO JSON" );
            return;
        }

        Log.d(LOG_TAG, "(first 50)input=" + strJson.substring(0, 50));
        try {
            JSONArray jsonArray = new JSONArray(strJson);
            cVVector = new Vector<ContentValues>(jsonArray.length());
//    * 標題 name
//    * Ok認證類別 certification_category
//    * 連絡電話 tel
//    * 顯示用地址 display_addr
//    * 系統辨識用地址 poi_addr

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString(TaipeiOkProvider.COLUMN_NAME).trim();
                String certification_category = jsonObject.getString(TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY).trim();
                String tel = jsonObject.getString(TaipeiOkProvider.COLUMN_TEL).trim();
                // not to show null
                if (tel==null || tel.equals("null")){
                    tel="";
                }
                String display_addr = jsonObject.getString(TaipeiOkProvider.COLUMN_DISPLAY_ADDR).trim();


                String poi_addr = jsonObject.getString(TaipeiOkProvider.COLUMN_POI_ADDR).trim();

                //
                String addr_dist = display_addr.substring(0,6);

                ContentValues weatherValues = new ContentValues();
                weatherValues.put(TaipeiOkProvider.COLUMN_NAME, name);
                weatherValues.put(TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY, certification_category);
                weatherValues.put(TaipeiOkProvider.COLUMN_TEL, tel);

                if (tel.equals("")){
                    weatherValues.put(TaipeiOkProvider.COLUMN_DISPLAY_ADDR, display_addr);

                }else{
                    weatherValues.put(TaipeiOkProvider.COLUMN_DISPLAY_ADDR, display_addr+"  tel: "+tel);

                }


                weatherValues.put(TaipeiOkProvider.COLUMN_POI_ADDR, poi_addr);

                //
                String strDist=getDistrict(display_addr);
                weatherValues.put(TaipeiOkProvider.COLUMN_DISTRICT, strDist);
         //       Log.d(LOG_TAG, "strDist=" + strDist + " COLUMN_DISPLAY_ADDR=" + display_addr);
                cVVector.add(weatherValues);

//                    Log.d(LOG_TAG, "json " + i + " is " + name);
            }
        } catch (JSONException e) {
//                e.printStackTrace();
            Log.d(LOG_TAG, "JSONException "+e.toString());
        } catch (Exception e){
            Log.d(LOG_TAG, "Exception "+e.toString());
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            String str=null;

            String selection = TaipeiOkProvider.COLUMN_CERTIFICATION_CATEGORY+"=\""+ TaipeiOkProvider.CATXX[cat]+"\"" ;

            int delCnt=getContentResolver().delete(TaipeiOkProvider.CONTENT_URI,
                    selection,
                    null);
            Log.d(LOG_TAG, "del cnt= "+ delCnt);




            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            int bulkCnt=getContentResolver().bulkInsert(TaipeiOkProvider.CONTENT_URI, cvArray);
            Log.d(LOG_TAG, "bulk cnt= "+ bulkCnt);


// delete old data so we don't build up an endless history
//           getContentResolver().delete(OkProvider.CONTENT_URI,
//                    WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
//                    new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});
            // notifyWeather();
            //      getContentResolver().
        }


    }

    String getDistrict(String address){
        String strDist=null;
        int knownDist= Taipei_District.length-1;
        for (int i=0;i< Taipei_District.length-1;i++){
            strDist= Taipei_District[i].substring(4);
            if (address.indexOf(strDist)>=0){
                knownDist=i;
                break;
            }
        }
        return Taipei_District[knownDist];
    }
    public String readRawJson(int cat) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet("https://bugzilla.mozilla.org/rest/bug?assigned_to=lhenry@mozilla.com");
        //  String str = "http://data.taipei.gov.tw/opendata/apply/json/QTdBNEQ5NkQtQkM3MS00QUI2LUJENTctODI0QTM5MkIwMUZE";
        String str= TaipeiOkProvider.JSNXX[cat];



        HttpGet httpGet = new HttpGet(str);
        Log.d(LOG_TAG, "new HttpGet(str) => " + str);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(LOG_TAG, "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }            catch (Exception e){

            Log.d(LOG_TAG, "Exception "+e.toString());

        }
        return builder.toString();
    }

}
