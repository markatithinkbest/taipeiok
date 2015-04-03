/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ithinkbest.taipeiok;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;



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

//import com.example.android.navigationdrawer.R;
//Toast com.example.android.navigationdrawer.R;

//import com.example.android.navigationdrawer.R;

/**
 * This example illustrates a common usage of the DrawerLayout widget
 * in the Android support library.
 * <p/>
 * <p>When a navigation (left) drawer is present, the host activity should detect presses of
 * the action bar's Up affordance as a signal to open and org.apache.commons:commons-io:1.3.2close the navigation drawer. The
 * ActionBarDrawerToggle facilitates this behavior.
 * Items within the drawer should fall into one of two categories:</p>
 * <p/>
 * <ul>
 * <li><strong>View switches</strong>. A view switch follows the same basic policies as
 * list or tab navigation in that a view switch does not create navigation history.
 * This pattern should only be used at the root activity of a task, leaving some form
 * of Up navigation active for activities further down the navigation hierarchy.</li>
 * <li><strong>Selective Up</strong>. The drawer allows the user to choose an alternate
 * parent for Up navigation. This allows a user to jump across an app's navigation
 * hierarchy at will. The application should treat this as it treats Up navigation from
 * a different task, replacing the current task stack using TaskStackBuilder or similar.
 * This is the only form of navigation drawer that should be used outside of the root
 * activity of a task.</li>
 * </ul>
 * <p/>
 * <p>Right side drawers should be used for actions, not navigation. This follows the pattern
 * established by the Action Bar that navigation should be to the left and actions to the right.
 * An action should be an operation performed on the current contents of the window,
 * for example enabling or disabling a data overlay on top of the current content.</p>
 */
public class NavigationDrawerActivity extends Activity implements PlanetAdapter.OnItemClickListener {

    static String LOG_TAG = "MARK987";


    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        //
        //
        notifyGooglePlay();
        notifyAppWebpage();


        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.certification_category);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // improve performance by indicating the list if fixed size.
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new PlanetAdapter(mPlanetTitles, this));
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        //     boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //    menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
//            case R.id.action_websearch:
//                // create intent to perform web search for this planet
//                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//                intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//                // catch event that there's no activity to handle intent
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//                }
//                return true;
            case R.id.action_update:
                Log.d(LOG_TAG, "...DEBUG action_update");
                // use this to start and trigger a service
                Intent i = new Intent(getApplicationContext(), UpdateService.class);
// potentially add data to the intent
                int[] cats = {0, 1};
                i.putExtra("CATS", cats);
                getApplicationContext().startService(i);


                return true;

            case R.id.action_del_all:
                StrictMode.ThreadPolicy policy = new StrictMode.
                        ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                getContentResolver().delete(OkProvider.CONTENT_URI, null, null);
                return true;
            case R.id.action_debug:
                doingDebug();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doingDebug() {
    }

    private void notifyGooglePlay() {
        int idGooglePlay = 12345;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.to_google_play));
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ToGooglePlayActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ToGooglePlayActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(idGooglePlay, mBuilder.build());

    }

    private void notifyAppWebpage() {
        int idGooglePlay = 12346;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.to_app_webpage));
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ToAppWebpageActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ToAppWebpageActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(idGooglePlay, mBuilder.build());

    }

    /* The click listener for RecyclerView in the navigation drawer */
    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = TaipeiFragment.newInstance(position);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();

        // update selected item title, then close the drawer
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */
    public static class TaipeiFragment extends Fragment implements AdapterView.OnItemSelectedListener {
        String[] Taipei_District = null;// = res.getStringArray(R.array.planets_array);
        String[] Certification_Category = null;
        ListView listView;
        Spinner spinner;
        SimpleCursorAdapter spinnerAdapter;
        int selectedCategory = 0;


        public static final String ARG_PLANET_NUMBER = "planet_number";

        public TaipeiFragment() {
            // Empty constructor required for fragment subclasses
        }

        public static Fragment newInstance(int position) {
            Fragment fragment = new TaipeiFragment();
            Bundle args = new Bundle();
            args.putInt(TaipeiFragment.ARG_PLANET_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }


        private Cursor getSummary(int cat) {
            Uri uri = OkProvider.CONTENT_URI_RAW_QUERY;
            String[] projection = new String[]{OkProvider.COLUMN_ID,
                    OkProvider.COLUMN_NAME, OkProvider.COLUMN_DISPLAY_ADDR};
            //
            String selection = OkProvider.COLUMN_CERTIFICATION_CATEGORY + "=\"" + Certification_Category[cat] + "\"";

            String[] selectionArgs = null;
            String sortOrder = null;

            return getActivity().managedQuery(uri, projection, selection, selectionArgs,
                    sortOrder);


            //return null;
        }


        private void updateSpinner(int cat) {


            spinnerAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_2, getSummary(cat), new String[]{OkProvider.COLUMN_DISTRICT, "CNT"}, new int[]{
                    android.R.id.text1, android.R.id.text2});
            spinnerAdapter.notifyDataSetChanged();
            spinner.setAdapter(spinnerAdapter);

        }

        private Cursor getList(int cat) {
            Uri uri = OkProvider.CONTENT_URI;
            String[] projection = new String[]{OkProvider.COLUMN_ID,
                    OkProvider.COLUMN_NAME, OkProvider.COLUMN_DISPLAY_ADDR};
            //
            String selection = OkProvider.COLUMN_CERTIFICATION_CATEGORY + "=\"" + OkProvider.CATXX[cat] + "\"";

            String[] selectionArgs = null;
            String sortOrder = OkProvider.COLUMN_DISPLAY_ADDR;

            return getActivity().managedQuery(uri, projection, selection, selectionArgs,
                    sortOrder);
        }


//        private Cursor getListSummary(int cat) {
//           // getActivity().getContentResolver().
//
//
//            return null;
////            Uri uri = OkProvider.CONTENT_URI;
////            String[] projection = new String[]{OkProvider.COLUMN_ID,
////                    OkProvider.COLUMN_NAME, OkProvider.COLUMN_DISPLAY_ADDR};
////            //
////            String selection =OkProvider.COLUMN_CERTIFICATION_CATEGORY+"=\""+OkProvider.CATXX[cat]+"\"" ;
////
////            String[] selectionArgs = null;
////            String sortOrder = OkProvider.COLUMN_DISPLAY_ADDR;
////
////            return getActivity().managedQuery(uri, projection, selection, selectionArgs,
////                    sortOrder);
//        }


        private Cursor getList(int cat, String district) {
            Uri uri = OkProvider.CONTENT_URI;
            String[] projection = new String[]{OkProvider.COLUMN_ID,
                    OkProvider.COLUMN_NAME, OkProvider.COLUMN_DISPLAY_ADDR};
            //
            String selection = OkProvider.COLUMN_CERTIFICATION_CATEGORY + "=\"" + OkProvider.CATXX[cat] + "\""
                    + " AND " + OkProvider.COLUMN_DISTRICT + " LIKE '%" + district + "%'";
            //name like '% LIM %'

            String[] selectionArgs = null;
            String sortOrder = OkProvider.COLUMN_DISPLAY_ADDR;

            return getActivity().managedQuery(uri, projection, selection, selectionArgs,
                    sortOrder);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

//            View rootView = inflater.inflate(R.layout.fragment_listview, container, false);
//            ListView listView=(ListView)rootView.findViewById(R.id.listView);

            Taipei_District = getResources().getStringArray(R.array.taipei_district);
            Certification_Category = getResources().getStringArray(R.array.certification_category);


            View rootView = inflater.inflate(R.layout.fragment_listview_v2, container, false);
            spinner = (Spinner) rootView.findViewById(R.id.spinner);
            listView = (ListView) rootView.findViewById(R.id.listView2);

            selectedCategory = getArguments().getInt(ARG_PLANET_NUMBER);
            spinner.setOnItemSelectedListener(this);

           // new UpdateService().processJson(selectedCategory);
            processJson(selectedCategory);
            spinnerAdapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_2, getSummary(selectedCategory), new String[]{OkProvider.COLUMN_DISTRICT, "CNT"}, new int[]{
                    android.R.id.text1, android.R.id.text2});

            spinner.setAdapter(spinnerAdapter);



         //   updateSpinner(selectedCategory);
            // NOT TO UPDATE HERE
            //  processJson(selectedCategory);
//            Intent i = new Intent(getActivity().getApplicationContext(), UpdateService.class);
//// potentially add data to the intent
//            int[] cats = {selectedCategory};
//            i.putExtra("CATS", cats);
//            getActivity().getApplicationContext().startService(i);


            Cursor mGrpMemberCursor = getList(selectedCategory);
            getActivity().startManagingCursor(mGrpMemberCursor);
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                    android.R.layout.simple_list_item_2, mGrpMemberCursor, new String[]{OkProvider.COLUMN_NAME, OkProvider.COLUMN_DISPLAY_ADDR}, new int[]{
                    android.R.id.text1, android.R.id.text2});

            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView textView2 = (TextView) view.findViewById(android.R.id.text2);


//                    String check = "臺北市松山區八德路四段138號B3F（京華城股份有限公司";


                    String check = textView2.getText().toString();
                    if (!(check.indexOf("台北市") == 0 || check.indexOf("臺北市") == 0)) {
                        Log.d(LOG_TAG, "before  @@@@@ " + check);

                        check = "台北市" + check;
                        Log.d(LOG_TAG, "after adding prefix 台北市 @@@@@ " + check);

                    }

                    int temp = check.indexOf("tel");
                    if (temp > 0) {
                        Log.d(LOG_TAG, "to remove tel, before  @@@@@ " + check);

                        check = check.substring(0, temp);
                        Log.d(LOG_TAG, "to remove tel, after  @@@@@ " + check);

                    }

                    //  check=textView1.getText().toString()+", "+check;
                    Log.d(LOG_TAG, "addr for map is " + check);
                    String map = "http://maps.google.com/maps?q=" + check;

// where check is the address string

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));

                    // NOT TO START MAP AT THIS MOMENT
//                    startActivity(i);
                }
            });
            //http://stackoverflow.com/questions/9987551/how-to-open-google-maps-using-address


            // for title

//            String planet = getResources().getStringArray(R.array.planets_array)[i];
//
//            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
//                    "drawable", getActivity().getPackageName());
//            ImageView iv = ((ImageView) rootView.findViewById(R.id.image));
//            iv.setImageResource(imageId);
            // Resources res = getResources();
            String[] certification_category = getResources().getStringArray(R.array.certification_category);
            getActivity().setTitle(certification_category[selectedCategory]);
            return rootView;
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

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString(OkProvider.COLUMN_NAME).trim();
                    String certification_category = jsonObject.getString(OkProvider.COLUMN_CERTIFICATION_CATEGORY).trim();
                    String tel = jsonObject.getString(OkProvider.COLUMN_TEL).trim();
                    // not to show null
                    if (tel==null || tel.equals("null")){
                        tel="";
                    }
                    String display_addr = jsonObject.getString(OkProvider.COLUMN_DISPLAY_ADDR).trim();


                    String poi_addr = jsonObject.getString(OkProvider.COLUMN_POI_ADDR).trim();

                    //


                    ContentValues weatherValues = new ContentValues();
                    weatherValues.put(OkProvider.COLUMN_NAME, name);
                    weatherValues.put(OkProvider.COLUMN_CERTIFICATION_CATEGORY, certification_category);
                    weatherValues.put(OkProvider.COLUMN_TEL, tel);

                    if (tel.equals("")){
                        weatherValues.put(OkProvider.COLUMN_DISPLAY_ADDR, display_addr);

                    }else{
                        weatherValues.put(OkProvider.COLUMN_DISPLAY_ADDR, display_addr+"  tel: "+tel);

                    }


                    weatherValues.put(OkProvider.COLUMN_POI_ADDR, poi_addr);

                    //
                    String strDist=getDistrict(display_addr);
                    weatherValues.put(OkProvider.COLUMN_DISTRICT, strDist);
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

                String selection =OkProvider.COLUMN_CERTIFICATION_CATEGORY+"=\""+OkProvider.CATXX[cat]+"\"" ;

                int delCnt=getActivity().getContentResolver().delete(OkProvider.CONTENT_URI,
                        selection,
                        null);
                Log.d(LOG_TAG, "del cnt= "+ delCnt);




                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                int bulkCnt=getActivity().getContentResolver().bulkInsert(OkProvider.CONTENT_URI, cvArray);
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
            String str=OkProvider.JSNXX[cat];



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



        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
                Resources res = getResources();
                String[] taipei_district = res.getStringArray(R.array.taipei_district);
                //  Log.d(LOG_TAG," position:"+position+ " "+taipei_district[position]);
                //  String district=taipei_district[position].substring(4);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String strDist = textView.getText().toString();
                Log.d(LOG_TAG, " textView:" + strDist);

                Cursor mGrpMemberCursor = getList(selectedCategory, strDist);
                getActivity().startManagingCursor(mGrpMemberCursor);
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                        android.R.layout.simple_list_item_2, mGrpMemberCursor, new String[]{OkProvider.COLUMN_NAME, OkProvider.COLUMN_DISPLAY_ADDR}, new int[]{
                        android.R.id.text1, android.R.id.text2});

                listView.setAdapter(adapter);
            } catch (Exception e) {
                Log.d(LOG_TAG, "onItemSelected, textView is null");

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }





}
