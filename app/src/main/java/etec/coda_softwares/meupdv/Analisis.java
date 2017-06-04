package etec.coda_softwares.meupdv;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class Analisis extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_analisis, menu);
        menu.findItem(R.id.chart_mode_button)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO: Mudar o modo temporal da chart (Dia>Semana>Mês e vice-versa)
                        return false;
                    }
                });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analisis);

        Toolbar tbar = (Toolbar)findViewById(R.id.analisis_toolbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LineChart test_chart = (LineChart) findViewById(R.id.test_chart);

        int[] day = new int[14];
        int[] money = new int[14];

        // Create test data
        for(int i=0;i<14;i++){
            day[i] = i;
            if(i%2==0) money[i] = i+3;
            else money[i] = i-3;
        }

        List<Entry> entries = new ArrayList<>();

        for(int i=0;i<day.length;i++)
            entries.add(new Entry(day[i], money[i]));

        LineDataSet dataSet = new LineDataSet(entries, "Test Data");
        dataSet.setColor(Color.parseColor("#1111ae"));
        dataSet.setValueTextColor(Color.WHITE);

        LineData lineData = new LineData(dataSet);
        test_chart.setData(lineData);
        test_chart.invalidate(); // refresh
    }
}
