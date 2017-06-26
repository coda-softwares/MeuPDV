package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.Venda;

import static etec.coda_softwares.meupdv.Analisis.ChartMode.Day;

public class Analisis extends AppCompatActivity {
    private TextView labelTitle;

    private Enum chartMode;
    private List<Venda> todasVendas;
    private LineChart chart;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO:
//        MenuInflater menuInflater = new MenuInflater(this);
//        menuInflater.inflate(R.menu.menu_analisis, menu);
//        menu.findItem(R.id.chart_mode_button)
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Analisis self = Analisis.this;
//
//                        if ( self.chartMode == Day) {
//                            self.chartMode = Week;
//                            self.resetLabels(R.string.chart_time_mode_week);
//                            self.updateChart(self.getWeekData());
//                        } else if ( self.chartMode == Week ) {
//                            self.chartMode = Month;
//                            self.resetLabels(R.string.chart_time_mode_month);
//                            self.updateChart(self.getMonthData());
//                        } else if ( self.chartMode == Month ) {
//                            self.chartMode = Year;
//                            self.resetLabels(R.string.chart_time_mode_year);
//                            self.updateChart(self.getYearData());
//                        } else {
//                            self.chartMode = Day;
//                            self.resetLabels(R.string.chart_time_mode_day);
//                            self.updateChart(self.getDayData());
//                        }
//
//                        return true;
//                    }
//                });
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
        chart = (LineChart) findViewById(R.id.chart);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getXAxis().setAvoidFirstLastClipping(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setOnTouchListener(null);

        setupData();
    }

    /**
     * Muda o titulo da chart e demais quando for necessário
     * @param titleLabel
     */
    public void resetLabels(int titleLabel){
        labelTitle.setText( titleLabel );
    }

    private void setupData(){
        Venda.DBROOT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                todasVendas = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren())
                    todasVendas.add(data.getValue(Venda.class));

                lucroPorDiaAno();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.showToast(Analisis.this, "Carregamento dos dados cancelado");
            }
        });
    }

    private void lucroPorDiaAno() {
        if (todasVendas.isEmpty()) {
            AlertDialog.Builder er = new AlertDialog.Builder(this);
            er.setCancelable(false);
            er.setMessage("Sem dados suficientes para construir o gráfico.");
            er.setNeutralButton("Voltar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            er.show();
            return;
        }
        ArrayList<Venda> vendas = new ArrayList<>(todasVendas);
        Collections.sort(vendas, new Comparator<Venda>() {
            @Override
            public int compare(Venda o1, Venda o2) {
                return (int) (o1.getData() - o2.getData());
            }
        });
        ArrayList<Entry> dados = new ArrayList<>();
        Calendar lastTime = Calendar.getInstance();
        lastTime.set(2000, 1, 1);
        final int year = Util.getCalendar(vendas.get(0).getData()).get(Calendar.YEAR);

        BigDecimal ttl = new BigDecimal(0);
        for (Venda venda : vendas) {
            Calendar thisData = Util.getCalendar(venda.getData());
            if (thisData.get(Calendar.YEAR) != year) {
                return;
            }
            ttl = ttl.add(new BigDecimal(venda.getTotal()));
            if (thisData.get(Calendar.DAY_OF_YEAR) > lastTime.get(Calendar.DAY_OF_YEAR)) {
                dados.add(new Entry(thisData.get(Calendar.DAY_OF_YEAR), ttl.floatValue()));
                ttl = new BigDecimal(0);
            }
        }

        LineDataSet dataSet = new LineDataSet(dados, "Valor vendido.");
        dataSet.setColor(Color.parseColor("#ddbb00"));
        dataSet.setValueTextColor(Color.parseColor("#2828dd"));
        dataSet.setValueTextSize(16);

        LineData lineData = new LineData(dataSet);
        chart.getXAxis().setAxisMinimum(dados.get(0).getX() - 1);
        chart.getXAxis().setAxisMaximum(dados.get(dados.size() - 1).getX() + 1);
        Description desc = new Description();
        desc.setText("Dia do mês");
        chart.setDescription(desc);
        chart.setData(lineData);
        chart.postInvalidate(); // refresh
    }

    public enum ChartMode {Day, Week, Month, Year}
}
