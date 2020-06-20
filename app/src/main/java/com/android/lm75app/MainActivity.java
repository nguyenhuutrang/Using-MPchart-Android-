package com.android.lm75app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView txtvSensor1, txtvSensor2;
    Long times;
    LineChart lineChart;
    LineData data;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    ArrayList<Entry> dataSensor1 = new ArrayList<Entry>();
    ArrayList<Entry> dataSensor2 = new ArrayList<Entry>();
    LineDataSet lineDataSet1 = new LineDataSet(dataSensor1,"Sensor 1");
    LineDataSet lineDataSet2 = new LineDataSet(dataSensor2,"Sensor 2");

    float temp1Pre, temp2Pre;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtvSensor1 = (TextView) findViewById(R.id.txtv1);
        txtvSensor2 = (TextView) findViewById(R.id.txtv2);
        lineChart = (LineChart) findViewById(R.id.linechart);

        times = (long)0;
        temp1Pre = temp2Pre = -100;

        ReadData("TemperatureInfo/temperaturePoint1",1);
        ReadData("TemperatureInfo/temperaturePoint2",2);
        //ReadData("TemperatureInfo/timeReceived");


    }

    private void ReadData(String path, final int sensor) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(path);

        myRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float value = dataSnapshot.getValue(Float.class);
                Log.d(TAG, "Value is: " + value + " time = " + times);

                times = (times + 1)%10000;
                if(sensor == 1){
                    txtvSensor1.setText("Sensor 1: " + value);
                    dataSensor1.add(new Entry(times,value));
                    if(temp2Pre != -100){
                        dataSensor2.add(new Entry(times,temp2Pre));
                    }

                    temp1Pre = value;
                }else if(sensor == 2){
                    txtvSensor2.setText("Sensor 2: " + value);
                    dataSensor2.add(new Entry(times,value));
                    if(temp1Pre != -100){
                        dataSensor1.add(new Entry(times,temp1Pre));
                    }
                    temp2Pre = value;
                }

                lineDataSet1.setValues(dataSensor1);
                lineDataSet2.setValues(dataSensor2);
                lineDataSet1.setColor(Color.BLUE);
                lineDataSet2.setColor(Color.RED);
                lineDataSet1.setDrawCircles(false);
                lineDataSet2.setDrawCircles(false);
                dataSets.clear();
                dataSets.add(lineDataSet1);
                dataSets.add(lineDataSet2);

                data = new LineData(dataSets);
                //lineChart.setViewPortOffsets(10, 0, 10, 0);

                lineChart.setData(data);

                // get the legend (only possible after setting data)
                /*Legend l = lineChart.getLegend();
                l.setEnabled(false);

                lineChart.getAxisLeft().setEnabled(true);
                lineChart.getAxisLeft().setSpaceTop(40);
                lineChart.getAxisLeft().setSpaceBottom(40);
                lineChart.getAxisRight().setEnabled(false);

                lineChart.getXAxis().setEnabled(false);*/

                // animate calls invalidate()...
                //lineChart.animateX(2500);
                lineChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}