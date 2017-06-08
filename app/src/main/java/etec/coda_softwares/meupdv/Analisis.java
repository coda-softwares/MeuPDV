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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.Venda;

import static etec.coda_softwares.meupdv.Analisis.ChartMode.*;

public class Analisis extends AppCompatActivity {
    private TextView labelTitle;
    private LineChart chart;

    ArrayList<Venda> lastVendasLoaded;

    private Enum chartMode;
    public enum ChartMode { Day, Week, Month, Year }

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
        ArrayList<VendaData> dadosDasVendas = separateData(lastVendasLoaded);
        passToChart(dadosDasVendas);
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

    private ArrayList<VendaData> separateData(ArrayList<Venda> vendas){
        lastVendasLoaded = vendas;

        ArrayList<VendaData> dadosDasVendas = new ArrayList<>();

        // Test simples por dia
        if ( this.chartMode == Day ) {

            Collections.sort(vendas, new Comparator<Venda>() {
                @Override
                public int compare(Venda o1, Venda o2) {
                    if (o1.getData()>o2.getData()){
                        return 1;
                    } else if (o1.getData()==o2.getData()){
                        return 0;
                    } else return -1;
                }
            });

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(vendas.get(0).getData());

            int lastTime = calendar.get(Calendar.DAY_OF_YEAR);

            VendaData lastVendaData = new VendaData(lastTime, "0", 0);

            for (Venda venda : vendas) {
                // TODO: Separar por data e guardar em dadosDasVendas
                // TODO: Depois passar para o chart cada VendaData

                // TODO:mini criar VendaData junto com lastTime, ou guardar lastTime na
                // TODO:mini primeira VendaData e ir somando

                calendar.setTimeInMillis(venda.getData());
                if(calendar.get(Calendar.DAY_OF_YEAR) > lastVendaData.getData()){
                    // Adiciona nova VendaData a dadosDasVendas
                    // e continua a soma-la até que o data mude
                    // e assim repete o processo(nova venda data)
                    dadosDasVendas.add(lastVendaData);
                    lastVendaData = new VendaData(calendar.get(Calendar.DAY_OF_YEAR),
                            venda.getTotal(), venda.getProdutos().size());

                } else {
                    // Continua a somar
                    lastVendaData.sumQuantidade(venda.getProdutos().size());
                    lastVendaData.sumRecebido(venda.getTotal());
                }

            }
            dadosDasVendas.add(lastVendaData);
        } // TODO: Criar o resto
        return dadosDasVendas;
    }
    private void setupData(){
        Venda.DBROOT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Venda> vendas = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                    vendas.add(data.getValue(Venda.class));

                ArrayList<VendaData> dadosDasVendas = Analisis.this.separateData(vendas);
                Analisis.this.passToChart(dadosDasVendas);
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
