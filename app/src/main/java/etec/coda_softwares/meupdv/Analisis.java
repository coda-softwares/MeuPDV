package etec.coda_softwares.meupdv;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.Venda;

import static etec.coda_softwares.meupdv.Analisis.ChartMode.Day;
import static etec.coda_softwares.meupdv.Analisis.ChartMode.Month;
import static etec.coda_softwares.meupdv.Analisis.ChartMode.Week;
import static etec.coda_softwares.meupdv.Analisis.ChartMode.Year;

public class Analisis extends AppCompatActivity {
    private TextView labelTitle;
    private LineChart chart;

    private Enum chartMode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_analisis, menu);
        menu.findItem(R.id.chart_mode_button)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Analisis self = Analisis.this;

                        if ( self.chartMode == Day) {
                            self.chartMode = Week;
                            self.resetLabels(R.string.chart_time_mode_week);
                            self.updateChart();
                        } else if ( self.chartMode == Week ) {
                            self.chartMode = Month;
                            self.resetLabels(R.string.chart_time_mode_month);
                            self.updateChart();
                        } else if ( self.chartMode == Month ) {
                            self.chartMode = Year;
                            self.resetLabels(R.string.chart_time_mode_year);
                            self.updateChart();
                        } else {
                            self.chartMode = Day;
                            self.resetLabels(R.string.chart_time_mode_day);
                            self.updateChart();
                        }

                        return true;
                    }
                });
        return true;
    }

    private void updateChart(){
        //
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analisis);

        Toolbar tbar = (Toolbar)findViewById(R.id.analisis_toolbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chartMode = Day;

        labelTitle = (TextView) findViewById(R.id.chart_title);

        chart = (LineChart) findViewById(R.id.chart);

        setupData();
    }

    /**
     * Muda o titulo da chart e demais quando for necessário
     * @param titleLabel
     */
    public void resetLabels(int titleLabel){
        labelTitle.setText( titleLabel );
    }

    private void populateProfitByDay(List<Venda> vendas) {
        if (vendas.isEmpty()) {
            return;
        }

        Collections.sort(vendas, new Comparator<Venda>() {
            @Override
            public int compare(Venda o1, Venda o2) {
                if (o1.getData() > o2.getData()) {
                    return 1;
                } else if (o1.getData() == o2.getData()) {
                    return 0;
                } else
                    return -1;
            }
        });
        ArrayList<Entry> dadosDasVendas = new ArrayList<>();
        LineDataSet dataSet = new LineDataSet(dadosDasVendas, "Recebido");
        dataSet.setColor(Color.parseColor("#1111ae"));
        dataSet.setValueTextColor(Color.BLACK);

        Iterator<Venda> a = vendas.iterator();
        Calendar last = Calendar.getInstance();
        last.setTimeInMillis(a.next().getData());

        LineData lineData = new LineData(dataSet);

        for (Venda venda : vendas) {
        }
        chart.setData(lineData);
        chart.invalidate();
        // TODO: Criar o resto
    }

    private void setupData(){
        Venda.DBROOT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Venda>> t = new GenericTypeIndicator<
                        HashMap<String, Venda>>() {
                };
                HashMap<String, Venda> todas = dataSnapshot.getValue(t);
                populateProfitByDay(new ArrayList<>(todas.values()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.showToast(Analisis.this, "Carregamento dos dados cancelado");
            }
        });
    }

    private void passToChart(ArrayList<VendaData> dadosDasVendas) {
        List<Entry> entries = new ArrayList<>();

        for(VendaData data : dadosDasVendas)
            entries.add(new Entry(data.getData(), data.getRecebido()));

        LineDataSet dataSet = new LineDataSet(entries, "Recebido");
        dataSet.setColor(Color.parseColor("#1111ae"));
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh
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

    public enum ChartMode {Day, Week, Month, Year}

    /**
     * Necessário pois representa um aglumerado de dados
     * sendo eles por dia, semana, mês ou ano
     */
    private class VendaData {
        private int data;
        private float recebido;
        private long quantidadeDeProdutosVendidos;
        /**
         *
         * @param data é representado como int pois é contado como um número commun
         * @param recebido "Ainda" não se refere aos lucros
         */
        public VendaData(int data, String recebido, long quantidadeDeProdutosVendidos){
            this.data = data;
            this.recebido = Float.parseFloat(recebido);
            this.quantidadeDeProdutosVendidos = quantidadeDeProdutosVendidos;
        }

        public int getData() {
            return data;
        }

        public float getRecebido() {
            return recebido;
        }
        public void sumRecebido(String v){
            recebido += Float.parseFloat(v);
        }

        public long getQuantidadeDeProdutosVendidos() {
            return quantidadeDeProdutosVendidos;
        }
        public void sumQuantidade(long v){
            quantidadeDeProdutosVendidos += v;
        }
    }
}
