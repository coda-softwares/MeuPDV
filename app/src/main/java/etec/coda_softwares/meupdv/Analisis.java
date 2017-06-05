package etec.coda_softwares.meupdv;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class Analisis extends AppCompatActivity {
    private TextView labelTitle;

    private Enum ChartMode;
    private Enum Day, Week, Month, Year;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_analisis, menu);
        menu.findItem(R.id.chart_mode_button)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Analisis self = Analisis.this;

                        if ( self.ChartMode == Day) {
                            self.ChartMode = Week;
                            self.resetLabels(R.string.chart_time_mode_week);
                        } else if ( self.ChartMode == Week ) {
                            self.ChartMode = Month;
                            self.resetLabels(R.string.chart_time_mode_month);
                        } else if ( self.ChartMode == Month ) {
                            self.ChartMode = Year;
                            self.resetLabels(R.string.chart_time_mode_year);
                        } else {
                            self.ChartMode = Day;
                            self.resetLabels(R.string.chart_time_mode_day);
                        }

                        return true;
                    }
                });
        return true;
    }

    /**
     * Muda o titulo da chart e demais quando for necessário
     * @param titleLabel
     */
    public void resetLabels(int titleLabel){
        labelTitle.setText( titleLabel );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analisis);

        Toolbar tbar = (Toolbar)findViewById(R.id.analisis_toolbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChartMode = Month;

        labelTitle = (TextView) findViewById(R.id.chart);

        LineChart test_chart = (LineChart) findViewById(R.id.chart);

        int[] time = new int[14];
        int[] money = new int[14];

        // Create test data
        for(int i=0;i<14;i++){
            time[i] = i;
            if(i%2==0) money[i] = i+3;
            else money[i] = i-3;
        }

        List<Entry> entries = new ArrayList<>();

        for(int i=0;i<time.length;i++)
            entries.add(new Entry(time[i], money[i]));

        LineDataSet dataSet = new LineDataSet(entries, "Exemplo");
        dataSet.setColor(Color.parseColor("#1111ae"));
        dataSet.setValueTextColor(Color.WHITE);

        LineData lineData = new LineData(dataSet);
        test_chart.setData(lineData);
        test_chart.invalidate(); // refresh
    }
}
