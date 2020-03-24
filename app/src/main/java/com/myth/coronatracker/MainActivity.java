package com.myth.coronatracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialAutoCompleteTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView recovered, deaths, confirmed, updated, txtNewCases, txtNewDeaths, total;
    MaterialAutoCompleteTextView countryName;
    ProgressBar progressBar;
    LineChart chart;
    Button button;


    String[] arr = {"Afghanistan","Albania","Algeria","Andorra","Angola","Anguilla","Antigua &amp; Barbuda","Argentina","Armenia","Aruba","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium","Belize","Benin","Bermuda","Bhutan","Bolivia","Bosnia &amp; Herzegovina","Botswana","Brazil","British Virgin Islands","Brunei","Bulgaria","Burkina Faso","Burundi","Cambodia","Cameroon","Cape Verde","Cayman Islands","Chad","Chile","China","Colombia","Congo","Cook Islands","Costa Rica","Cote D Ivoire","Croatia","Cruise Ship","Cuba","Cyprus","Czech Republic","Denmark","Djibouti","Dominica","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Estonia","Ethiopia","Falkland Islands","Faroe Islands","Fiji","Finland","France","French Polynesia","French West Indies","Gabon","Gambia","Georgia","Germany","Ghana","Gibraltar","Greece","Greenland","Grenada","Guam","Guatemala","Guernsey","Guinea","Guinea Bissau","Guyana","Haiti","Honduras","Hong Kong","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Isle of Man","Israel","Italy","Jamaica","Japan","Jersey","Jordan","Kazakhstan","Kenya","Kuwait","Kyrgyz Republic","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania","Luxembourg","Macau","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Mauritania","Mauritius","Mexico","Moldova","Monaco","Mongolia","Montenegro","Montserrat","Morocco","Mozambique","Namibia","Nepal","Netherlands","Netherlands Antilles","New Caledonia","New Zealand","Nicaragua","Niger","Nigeria","Norway","Oman","Pakistan","Palestine","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Puerto Rico","Qatar","Reunion","Romania","Russia","Rwanda","Saint Pierre &amp; Miquelon","Samoa","San Marino","Satellite","Saudi Arabia","Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","South Africa","South Korea","Spain","Sri Lanka","St Kitts &amp; Nevis","St Lucia","St Vincent","St. Lucia","Sudan","Suriname","Swaziland","Sweden","Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Timor L'Este","Togo","Tonga","Trinidad &amp; Tobago","Tunisia","Turkey","Turkmenistan","Turks &amp; Caicos","Uganda","Ukraine","United Arab Emirates","United Kingdom","Uruguay","Uzbekistan","Venezuela","Vietnam","Virgin Islands (US)","Yemen","Zambia","Zimbabwe"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryName = findViewById(R.id.textInputCountry);
        button = findViewById(R.id.buttonFind);
        chart = findViewById(R.id.chart);
        recovered = findViewById(R.id.textRecovered);
        deaths = findViewById(R.id.textDeaths);
        confirmed = findViewById(R.id.textConfirmed);
        progressBar = findViewById(R.id.progressBar);
        updated = findViewById(R.id.updatedTime);
        txtNewCases = findViewById(R.id.textNewCases);
        txtNewDeaths = findViewById(R.id.textNewDeaths);
        total = findViewById(R.id.textTotal);

        chart.setNoDataText("");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, arr);

        countryName.setThreshold(2);
        countryName.setAdapter(adapter);

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countryName.getText().toString().matches("")) {
                    countryName.setEnabled(false);
                    countryName.setEnabled(true);
                    progressBar.setVisibility(View.VISIBLE);
                    getCurrentFocus();
                    AndroidNetworking.get("https://covid19-api.weedmark.systems/api/v1/stats?country={country}")
                            .addPathParameter("country", countryName.getText().toString())
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Gson gson = new Gson();
                                    covidStats stats = null;
                                    try {
                                        stats = gson.fromJson(response.getJSONObject("data").getJSONArray("covid19Stats").get(0).toString(), covidStats.class);
                                        final covidStats finalStats = stats;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                confirmed.setText(finalStats.getConfirmed() + "");
                                                deaths.setText(finalStats.getDeaths() + "");
                                                recovered.setText(finalStats.getRecovered() + "");
                                                updated.setText(finalStats.getLastUpdate() + "");
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(ANError error) {
                                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Enter country name!", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!countryName.getText().toString().matches("")) {
                    countryName.setEnabled(false);
                    countryName.setEnabled(true);
                    recovered.setText("");
                    deaths.setText("");
                    confirmed.setText("");
                    updated.setText("");
                    txtNewCases.setText("");
                    txtNewDeaths.setText("");
                    total.setText("");
                    chart.clear();
                    chart.invalidate();

                    progressBar.setVisibility(View.VISIBLE);
                    AndroidNetworking.get("https://covid-193.p.rapidapi.com/statistics")
                            .addQueryParameter("country", countryName.getText().toString())
                            .addHeaders("x-rapidapi-host", "covid-193.p.rapidapi.com")
                            .addHeaders("x-rapidapi-key", "G2U0tJa3h5msh3uwlVbQCwyz0tf6p14q0nHjsnM8wWjLVV6hgQ")
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("RESPONSE", response.toString());
                                    Gson gson = new Gson();
                                    covidCases covidcases = null;
                                    covidDeaths coviddeaths = null;
                                    String day = null;
                                    String time = null;
                                    String newcases = null;
                                    String newdeaths = null;
                                    try {
                                        covidcases = gson.fromJson(response.getJSONArray("response").getJSONObject(0).getJSONObject("cases").toString(), covidCases.class);
                                        coviddeaths = gson.fromJson(response.getJSONArray("response").getJSONObject(0).getJSONObject("deaths").toString(), covidDeaths.class);
                                        day = response.getJSONArray("response").getJSONObject(0).getString("day");
                                        time = response.getJSONArray("response").getJSONObject(0).getString("time");
                                        newcases = response.getJSONArray("response").getJSONObject(0).getJSONObject("cases").getString("new");
                                        newdeaths = response.getJSONArray("response").getJSONObject(0).getJSONObject("deaths").getString("new");
                                        final covidCases finalCases = covidcases;
                                        final covidDeaths finalDeaths = coviddeaths;
                                        final String finalDay = day;
                                        final String finalTime = time;
                                        final String finalNewcases = newcases;
                                        final String finalNewdeaths = newdeaths;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                confirmed.setText(finalCases.getActive() + "");
                                                deaths.setText(finalDeaths.getTotal() + "");
                                                recovered.setText(finalCases.getRecovered() + "");
                                                updated.setText("last updated: "+finalTime.replace('T', ' '));
                                                total.setText(finalCases.getTotal() + "");
                                                txtNewCases.setText(finalNewcases);
                                                txtNewDeaths.setText(finalNewdeaths);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(ANError error) {
                                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                }
                            });

                    AndroidNetworking.get("https://covidapi.info/api/v1/country/{country}")
                            .addPathParameter("country", getCountryCode(countryName.getText().toString()))
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    int count = 0;
                                    Gson gson = new Gson();
                                    ArrayList<Entry> entries = new ArrayList<>();
                                    Map<String, String> map = new HashMap<>();
                                    String[] dates = null;
                                    String[] cases = null;
                                    Iterator<String> sIterator = null;
                                    try {
                                        int days = 0;
                                        sIterator = response.getJSONObject("result").keys();
                                        count = response.getInt("count");
                                        while (sIterator.hasNext()) {
                                            days++;
                                            // get key
                                            String key = sIterator.next();
                                            // get value
                                            String value = response.getJSONObject("result").getJSONObject(key).getInt("confirmed")+"";
                                            map.put(key, value);
                                            entries.add(new Entry(days, Integer.parseInt(value)));
                                        }
                                        final ArrayList<Entry> finalEntries = entries;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                LineDataSet dataSet = new LineDataSet(finalEntries, "Cases by day from 1st Jan");
                                                dataSet.setLineWidth(2);
                                                dataSet.setColor(ContextCompat.getColor(MainActivity.this, R.color.colorDeathsSecondary));
                                                dataSet.setValueTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorTextDark));
                                                dataSet.setValueTextSize(0);
                                                dataSet.setDrawCircleHole(false);
                                                dataSet.setDrawCircles(false);
                                                dataSet.setCircleColor(R.color.colorDeaths);
                                                Description description = chart.getDescription();
                                                description.setEnabled(false);

                                                chart.setDrawGridBackground(false);
                                                XAxis xAxis = chart.getXAxis();
                                                xAxis.setDrawGridLines(false);
                                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                                xAxis.setDrawAxisLine(false);

                                                YAxis yAxisRight = chart.getAxisRight();
                                                yAxisRight.setEnabled(false);

                                                YAxis yAxisLeft = chart.getAxisLeft();
                                                yAxisLeft.setGranularity(1f);
                                                yAxisLeft.setDrawAxisLine(false);
                                                yAxisLeft.setDrawGridLines(false);

                                                LineData data = new LineData(dataSet);
                                                chart.setData(data);
                                                chart.animateX(500);

                                                chart.invalidate();
                                            }
                                        });

                                    }catch (Exception e){

                                    }
                                }

                                @Override
                                public void onError(ANError anError) {

                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Enter country name!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public String getCountryCode(String countryName) {
        String[] isoCountryCodes = Locale.getISOCountries();
        for (String code : isoCountryCodes) {
            Locale locale = new Locale("", code);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return locale.getISO3Country();
            }
        }
        return countryName;
    }

}
