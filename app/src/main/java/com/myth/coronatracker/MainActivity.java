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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView recovered, deaths, confirmed, updated, txtNewCases, txtNewDeaths, total;
    MaterialAutoCompleteTextView countryName;
    ProgressBar progressBar;
    LineChart chart;
    Button button;


    String[] arr = {"USA", "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Anguilla", "Antigua &amp; Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia &amp; Herzegovina", "Botswana", "Brazil", "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Cape Verde", "Cayman Islands", "Chad", "Chile", "China", "Colombia", "Congo", "Cook Islands", "Costa Rica", "Cote D Ivoire", "Croatia", "Cruise Ship", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Estonia", "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia", "French West Indies", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guam", "Guatemala", "Guernsey", "Guinea", "Guinea Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Jamaica", "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kuwait", "Kyrgyz Republic", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Mauritania", "Mauritius", "Mexico", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco", "Mozambique", "Namibia", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan", "Palestine", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russia", "Rwanda", "Saint Pierre &amp; Miquelon", "Samoa", "San Marino", "Satellite", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "South Africa", "South Korea", "Spain", "Sri Lanka", "St Kitts &amp; Nevis", "St Lucia", "St Vincent", "St. Lucia", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor L'Este", "Togo", "Tonga", "Trinidad &amp; Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks &amp; Caicos", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "Uruguay", "Uzbekistan", "Venezuela", "Vietnam", "Virgin Islands (US)", "Yemen", "Zambia", "Zimbabwe"};

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
                (this, android.R.layout.select_dialog_item, arr);

        countryName.setThreshold(2);
        countryName.setAdapter(adapter);

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
                            .addQueryParameter("country", countryName.getText().toString().matches("United Kingdom") ? "UK" : countryName.getText().toString().replace(' ', '-'))
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
                                        final String finalNewcases = newcases.matches("null") ? "" : newcases;
                                        final String finalNewdeaths = newdeaths.matches("null") ? "" : newdeaths;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                confirmed.setText(finalCases.getActive() + "");
                                                deaths.setText(finalDeaths.getTotal() + "");
                                                recovered.setText(finalCases.getRecovered() + "");
                                                updated.setText("last updated: " + finalTime.replace('T', ' '));
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
                                    ArrayList<Entry> entriesConfirmed = new ArrayList<>();
                                    ArrayList<Entry> entriesDeaths = new ArrayList<>();
                                    ArrayList<Entry> entriesRecovered = new ArrayList<>();

                                    Iterator<String> sIterator;
                                    try {
                                        int days = 0;
                                        sIterator = response.getJSONObject("result").keys();
                                        count = response.getInt("count");
                                        while (sIterator.hasNext()) {
                                            days++;
                                            // get key
                                            String key = sIterator.next();
                                            // get value
                                            String valueConfirmed = response.getJSONObject("result").getJSONObject(key).getInt("confirmed") + "";
                                            String valueDeaths = response.getJSONObject("result").getJSONObject(key).getInt("deaths") + "";
                                            String valueRecovered = response.getJSONObject("result").getJSONObject(key).getInt("recovered") + "";

                                            entriesConfirmed.add(new Entry(days, Integer.parseInt(valueConfirmed)));
                                            entriesDeaths.add(new Entry(days, Integer.parseInt(valueDeaths)));
                                            entriesRecovered.add(new Entry(days, Integer.parseInt(valueRecovered)));
                                        }

                                        final ArrayList<Entry> finalEntriesDeaths = entriesDeaths;
                                        final ArrayList<Entry> finalEntriesRecovered = entriesRecovered;
                                        final ArrayList<Entry> finalEntriesConfirmed = entriesConfirmed;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                LineDataSet dataSetTotal = new LineDataSet(finalEntriesConfirmed, "Total");
                                                dataSetTotal.setAxisDependency(YAxis.AxisDependency.LEFT);
                                                dataSetTotal.setLineWidth(2);
                                                dataSetTotal.setColor(ContextCompat.getColor(MainActivity.this, R.color.colorTextDark));
                                                dataSetTotal.setValueTextSize(0);
                                                dataSetTotal.setDrawCircleHole(false);
                                                dataSetTotal.setDrawCircles(false);

                                                LineDataSet dataSetDeaths = new LineDataSet(finalEntriesDeaths, "Deaths");
                                                dataSetDeaths.setAxisDependency(YAxis.AxisDependency.LEFT);
                                                dataSetDeaths.setLineWidth(2);
                                                dataSetDeaths.setColor(ContextCompat.getColor(MainActivity.this, R.color.colorDeaths));
                                                dataSetDeaths.setValueTextSize(0);
                                                dataSetDeaths.setDrawCircleHole(false);
                                                dataSetDeaths.setDrawCircles(false);

                                                LineDataSet dataSetRecovered = new LineDataSet(finalEntriesRecovered, "Recovered");
                                                dataSetRecovered.setAxisDependency(YAxis.AxisDependency.LEFT);
                                                dataSetRecovered.setLineWidth(2);
                                                dataSetRecovered.setColor(ContextCompat.getColor(MainActivity.this, R.color.colorRecovered));
                                                dataSetRecovered.setValueTextSize(0);
                                                dataSetRecovered.setDrawCircleHole(false);
                                                dataSetRecovered.setDrawCircles(false);

                                                Description description = chart.getDescription();
                                                description.setEnabled(true);
                                                description.setText("Last 60 days");

                                                chart.setDrawGridBackground(false);
                                                XAxis xAxis = chart.getXAxis();
                                                xAxis.setDrawGridLines(false);
                                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                                xAxis.setDrawAxisLine(false);
                                                xAxis.setAxisMinimum(0f);

                                                YAxis yAxisRight = chart.getAxisRight();
                                                yAxisRight.setEnabled(false);

                                                YAxis yAxisLeft = chart.getAxisLeft();
                                                yAxisLeft.setGranularity(1f);
                                                yAxisLeft.setDrawAxisLine(false);
                                                yAxisLeft.setDrawGridLines(false);

                                                List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                                                dataSets.add(dataSetRecovered);
                                                dataSets.add(dataSetDeaths);
                                                dataSets.add(dataSetTotal);

                                                LineData data = new LineData(dataSets);
                                                chart.setData(data);
                                                chart.animateX(500);

                                                chart.invalidate();
                                            }
                                        });

                                    } catch (Exception e) {

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
