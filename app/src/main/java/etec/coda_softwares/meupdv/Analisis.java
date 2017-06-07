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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.Venda;

import static etec.coda_softwares.meupdv.Analisis.ChartMode.*;

public class Analisis extends AppCompatActivity {
    private TextView labelTitle;

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
                            self.updateChart(self.getWeekData());
                        } else if ( self.chartMode == Week ) {
                            self.chartMode = Month;
                            self.resetLabels(R.string.chart_time_mode_month);
                            self.updateChart(self.getMonthData());
                        } else if ( self.chartMode == Month ) {
                            self.chartMode = Year;
                            self.resetLabels(R.string.chart_time_mode_year);
                            self.updateChart(self.getYearData());
                        } else {
                            self.chartMode = Day;
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

        chartMode = Day;

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
        // Test simples por dia
        if ( this.chartMode == Day ) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(vendas.get(0).getData());

            int lastTime = calendar.get(Calendar.DAY_OF_YEAR);

            List<VendaData> dadosDasVendas = new ArrayList<>();
            for (Venda venda : vendas) {
                // TODO: Separar por data e guardar em dadosDasVendas
                // TODO: Depois passar para o chart cada VendaData

                // TODO:mini criar VendaData junto com lastTime, ou guardar lastTime na
                // TODO:mini primeira VendaData e ir somando
                calendar.setTimeInMillis(venda.getData());
                if(calendar.get(Calendar.DAY_OF_YEAR) > lastTime){
                    // Adiciona nova VendaData a dadosDasVendas
                    // e continua a soma-la até que o data mude
                    // e assim repete o processo(nova venda data)
                } else {
                    // Continua a somar
                }

            }
        } // TODO: Criar o resto

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

    /**
     * Necessário pois representa um aglumerado de dados
     * sendo eles por dia, semana, mês ou ano
     */
    private class VendaData {
        private int data;
        private BigDecimal recebido;
        private long quantidadeDeProdutosVendidos;
        /**
         *
         * @param data é representado como int pois é contado como um número commun
         * @param recebido "Ainda" não se refere aos lucros
         */
        public VendaData(int data, BigDecimal recebido, long quantidadeDeProdutosVendidos){
            this.data = data;
            this.recebido = recebido;
            this.quantidadeDeProdutosVendidos = quantidadeDeProdutosVendidos;
        }

        public int getData() {
            return data;
        }

        public BigDecimal getRecebido() {
            return recebido;
        }

        public long getQuantidadeDeProdutosVendidos() {
            return quantidadeDeProdutosVendidos;
        }
    }
}
