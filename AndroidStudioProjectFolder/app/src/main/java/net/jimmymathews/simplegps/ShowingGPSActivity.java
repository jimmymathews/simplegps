package net.jimmymathews.simplegps;

import java.io.*;
import java.util.Scanner;
import java.util.Calendar;
import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.Menu;
import android.widget.Button;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.EditText;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class ShowingGPSActivity extends AppCompatActivity {
    private LocationManager lm;
    private LocationListener locationListener;
    ArrayList<LocStorer> myLSs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_showing_gps);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getActionBar();
//        actionBar.hide();

        myLSs = new ArrayList<LocStorer>();
        myLSs.add(new LocStorer(0,0,0));

//        String w = readFromFile(getApplicationContext());
        EditText etp = (EditText) findViewById(R.id.fulllog);
        etp.setText("");
        etp.getBackground().clearColorFilter();

        final Button b = (Button) findViewById(R.id.logaction);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();//blah
                String d = c.getTime().toString();
                LocStorer ls = myLSs.get(myLSs.size()-1);
                LocStorer cpy = new LocStorer(ls.lat, ls.lng, ls.alt);
                ls.setDate(d);
                myLSs.add(cpy);

                String current;
                if(myLSs.size()>0)
                {
                    String whole = "";
                    for(int i=0; i<myLSs.size()-1; i++)
                    {
                        LocStorer myLS = myLSs.get(i);
                        String myLat = myLS.getLat();
                        String myLng = myLS.getLng();
                        String myAlt = myLS.getAlt();
                        String myDate = myLS.getDate();
                        whole = whole + myDate+"\nLat: "+myLat +"\nLng: "+ myLng+"\nAlt: "+myAlt+"\n\n";
                    }
                    current = whole;
                }
                else{
                    current = "";
                }
                writeToFile(current,getApplicationContext());
                EditText et = (EditText) findViewById(R.id.fulllog);
                et.setText(current);
            }
        });

        final Button b2 = (Button) findViewById(R.id.logclear);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToFile("",getApplicationContext());
                EditText et = (EditText) findViewById(R.id.fulllog);
                myLSs.clear();
                myLSs.add(new LocStorer(0,0,0));
                et.setText("");
            }
        });

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        TextView tv = (TextView) findViewById(R.id.mtv);
        tv.setText("Haven't gotten first fix of location.");
        locationListener = new MyLocationListener(tv);
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException e){}

    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("simple_gps_log.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return ret;
    }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("simple_gps_log.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
        }
    }

//    public void onStop()
//    {
//        finish();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_showing_g, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LocStorer
    {
        public double lat=0;
        public double lng=0;
        public double alt=0;
        String date="";
        public LocStorer(double l1, double l2, double l3)
        {
            lat = l1;
            lng = l2;
            alt = l3;
        }
        void update(double l1, double l2, double l3)
        {
            lat = l1;
            lng = l2;
            alt = l3;
        }
        public String getLat()
        {
            return fmt(lat);
        }
        public String getLng()
        {
            return fmt(lng);
        }
        public String getAlt()
        {
            return fmt(alt);
        }
        public String getDate()
        {
            return date;
        }
        public void setDate(String d)
        {
            date = d;
        }
        public String fmt(double d)
        {
            NumberFormat formatter = new DecimalFormat("#0.0000000");
            return formatter.format(d);
        }
    }

    private class MyLocationListener implements LocationListener
    {
        TextView txtView;
        public MyLocationListener(TextView t)
        {
            txtView = t;
        }
        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                txtView.setText("Lat: " + loc.getLatitude() +
                        "\nLng: " + loc.getLongitude()+ "\nAlt: " +loc.getAltitude());
                myLSs.get(myLSs.size()-1).update(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
            }
        }
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            txtView.setText("GPS provider disabled.");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            txtView.setText("GPS provider enabled.");
        }
        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // TODO Auto-generated method stub
        }}
}

