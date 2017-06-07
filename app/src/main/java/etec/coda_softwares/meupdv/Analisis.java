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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.HandlerPadrao;
import etec.coda_softwares.meupdv.entitites.Venda;

import static android.R.attr.data;

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
                            self.updateChart(self.getWeekData());
                        } else if ( self.ChartMode == Week ) {
                            self.ChartMode = Month;
                            self.resetLabels(R.string.chart_time_mode_month);
                            self.updateChart(self.getMonthData());
                        } else if ( self.ChartMode == Month ) {
                            self.ChartMode = Year;
                            self.resetLabels(R.string.chart_time_mode_year);
                            self.updateChart(self.getYearData());
                        } else {
                            self.ChartMode = Day;
                            self.resetLabels(R.string.chart_time_mode_day);
                            self.updateChart(self.getDayData());
                        }

                        return true;
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

        setupData();
    }

    /**
     * Muda o titulo da chart e demais quando for necessário
     * @param titleLabel
     */
    public void resetLabels(int titleLabel){
        labelTitle.setText( titleLabel );
    }

    private void updateChart(List<Entry> entries){
    }

    private void separateData(List<Venda> vendas){

    }
    private void setupData(){
        Venda.DBROOT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Venda> vendas = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                    vendas.add(data.getValue(Venda.class));

                Analisis.this.separateData(vendas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.showToast(Analisis.this, "Carregamento dos dados cancelado");
            }
        });
    }
    public List<Entry> getYearData(){
        return new ArrayList<Entry>();
    }
    public List<Entry> getMonthData(){
        return new ArrayList<Entry>();
    }
    public List<Entry> getWeekData(){
        return new ArrayList<Entry>();
    }
    public List<Entry> getDayData(){
        return new ArrayList<Entry>();
    }
}
