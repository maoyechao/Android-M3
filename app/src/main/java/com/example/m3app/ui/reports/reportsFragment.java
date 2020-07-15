package com.example.m3app.ui.reports;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.m3app.R;
import com.example.m3app.ui.SharedPreference;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class reportsFragment extends Fragment {
    private ReportsViewModel reportsViewModel;
    private String start;
    private String end;
    private Context context;
    private Spinner year;
    private DatePicker datePicker;
    private String selectedYear;
    private BarChart chartBar;
    private PieChart chartPie;
    private Button btnSelectedYear;
    public reportsFragment() {
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        reportsViewModel =
                ViewModelProviders.of(this).get(ReportsViewModel.class);
        View root = inflater.inflate(R.layout.reports_fragment, container, false);
        chartBar = root.findViewById(R.id.chartBar);
        chartBar.setVisibility(View.GONE);
        chartPie = root.findViewById(R.id.chartPie);
        chartPie.setVisibility(View.GONE);
        btnSelectedYear = root.findViewById(R.id.select_year);
        datePicker = root.findViewById(R.id.datepickerStartEnd);
        datePicker.init(2019, 1, 1, new DatePicker.OnDateChangedListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year,monthOfYear,dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.CHINA);
                if (start!= null) {
                    end = sdf.format(c);
                    reportsViewModel.getMemoirByStartEndProcessing(start,end);
                    start = "";
                    end = "";
                }
                start = sdf.format(c);
            }
        });

        reportsViewModel.getMemoirStartEndResultLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("got")){
                    try {
                        chartPie.setVisibility(View.VISIBLE);
                        renderPieData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                }});

        year = root.findViewById(R.id.spinner_year);
        btnSelectedYear.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                selectedYear = year.getSelectedItem().toString().trim();
                reportsViewModel.getMemoirByYearProcessing(selectedYear);
            }
        });

        reportsViewModel.getMemoirByYearResultLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("got")){
                    try {
                        chartBar.setVisibility(View.VISIBLE);
                        renderBarData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }});
        return root;
    }

    public void renderPieData() throws JSONException {
        chartPie.setUsePercentValues(true);
        chartPie.setDragDecelerationFrictionCoef(0.95f);
        chartPie.setCenterText(generateCenterSpannableText());
        chartPie.setDrawHoleEnabled(true);
        chartPie.setHoleColor(Color.WHITE);
        chartPie.setTransparentCircleColor(Color.WHITE);
        chartPie.setTransparentCircleAlpha(110);
        chartPie.setHoleRadius(58f);
        chartPie.setTransparentCircleRadius(61f);
        chartPie.setDrawCenterText(true);
        chartPie.setRotationAngle(0);
        // enable rotation of the chart by touch
        chartPie.setRotationEnabled(true);
        chartPie.setHighlightPerTapEnabled(true);
        Legend l = chartPie.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        chartPie.setEntryLabelColor(Color.WHITE);
        chartPie.setEntryLabelTextSize(12f);
        setData(); }
    private void setData() throws JSONException {
        ArrayList<PieEntry> entries = new ArrayList<>();
        SharedPreference sp = SharedPreference.getInstance(context);
        for (int i = 0; i < sp.getInt("postcodeCount") ; i++) {
            JSONObject postcode = new JSONObject(sp.getString("postcodeMovies"+i));
            float movieWatched = postcode.getInt("totalNumofMovies");
            entries.add(new PieEntry(movieWatched,
                    Integer.toString(postcode.getInt("postcode")),
                    getResources().getDrawable(R.mipmap.ic_launcher_movie)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Postcode");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // setting pie chart data
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chartPie));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        chartPie.setData(data);
        chartPie.highlightValues(null);
        chartPie.invalidate();
         }

    private SpannableString generateCenterSpannableText() {
        SharedPreference sp = SharedPreference.getInstance(context);
        SpannableString s = new SpannableString("Total Movies %\n Watched per Postcode by User "+sp.getString("firstname"));
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    public  void renderBarData() throws JSONException {
        chartBar.setDrawBarShadow(false);
        chartBar.setDrawValueAboveBar(true);
        chartBar.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chartBar.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chartBar.setPinchZoom(false);

        chartBar.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chartBar);

        XAxis xAxis = chartBar.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);


        YAxis leftAxis = chartBar.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chartBar.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = chartBar.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        setBarData();
    }
    private void setBarData() throws JSONException {

        SharedPreference sp = SharedPreference.getInstance(context);

        ArrayList<BarEntry> values = new ArrayList<>();
        String[] mMonths = new String[]{
                "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
        };
        for (int i = 0; i < sp.getInt("monthCount"); i++) {
            JSONObject monthMovie = new JSONObject(sp.getString("monthMovies"+i));
            float val = (float) (monthMovie.getInt("totalNumofMovies"));
            String monthname = monthMovie.getString("monthName");
            int position = 0;
            for (int j =  0; j<mMonths.length;j++) {
                if (monthname.equals(mMonths[j])) {
                    position = j;
                }
            }
            if (Math.random() * 100 < 25) {
                values.add(new BarEntry((float)(position+1), val, getResources().getDrawable(R.mipmap.ic_launcher_movie)));
            } else {
                values.add(new BarEntry((float)(position+1), val));
            }
        }

        BarDataSet set1;

        if (chartBar.getData() != null &&
                chartBar.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chartBar.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chartBar.getData().notifyDataChanged();
            chartBar.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "The watched movie numbers per month for selected year");
            set1.setDrawIcons(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            chartBar.setData(data);
        }
        chartBar.invalidate();
}}
