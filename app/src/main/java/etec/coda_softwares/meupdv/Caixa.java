package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.NumberPicker;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import etec.coda_softwares.meupdv.entitites.Produto;

public class Caixa extends AppCompatActivity {
    private String last;
    private DecoratedBarcodeView leitor;
    private Map<String, Produto> produtos;
    private ArrayList<Produto> carrinho;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            String res = result.getText();
            if (res.equals(last)) {
                return;
            }

            last = res;
            Produto produto = produtos.get(res);

            if (produto == null) {
                leitor.setStatusText("Produto " + res + " não encontrado");
            }

            leitor.setStatusText(produto.getNome() + " lido com sucesso.");
            carrinho.add(produto);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void nenhumProduto() {
        AlertDialog.Builder erro = new AlertDialog.Builder(Caixa.this);
        erro.setMessage("Nenhum produto foi cadastrado ainda.");
        erro.setCancelable(false);
        erro.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Caixa.this.finish();
            }
        });
        erro.setPositiveButton("Criar novo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Caixa.this, CadastrarProduto.class));
            }
        });
        erro.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caixa);
        Toolbar bar = (Toolbar) findViewById(R.id.caixa_toolbar);
        bar.setTitle("");
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        carrinho = new ArrayList<>();

        NumberPicker picker = (NumberPicker) findViewById(R.id.caixa_quantidade);
        picker.setMaxValue(100);
        picker.setValue(1);
        picker.setMinValue(1);

        leitor = (DecoratedBarcodeView) findViewById(R.id.caixa_leitor);
        leitor.setStatusText("Carregando...");

        Produto.DBROOT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Produto>> tipo =
                        new GenericTypeIndicator<Map<String, Produto>>() {
                        };
                long time = System.currentTimeMillis();
                produtos = dataSnapshot.getValue(tipo);
                long total = System.currentTimeMillis() - time;

                Util.showToast(Caixa.this, "Lido com " + total + " millisegundos");

                if (produtos == null) {
                    nenhumProduto();
                    return;
                }

                leitor.setStatusText("Pronto!");
                //leitor.resume();
                leitor.decodeContinuous(callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
                System.exit(1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            leitor.resume();
        } catch (Exception e) {
            Log.wtf(getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        leitor.pause();
    }

}
