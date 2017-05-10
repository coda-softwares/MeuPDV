package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import etec.coda_softwares.meupdv.entitites.Produto;

public class Caixa extends AppCompatActivity {
    private Produto lastProduto = new Produto();
    private DecoratedBarcodeView leitor;
    private Map<String, Produto> produtos;
    private Stack<Produto> carrinho;
    private NumberPicker quantidade;
    private BarcodeCallback callback = new BarcodeCallback() {
        List<BarcodeFormat> excluded = Arrays.asList(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.DATA_MATRIX
        );

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (excluded.contains(result.getBarcodeFormat())) {
                return;
            }

            String res = result.getText(); // Nunca será nulo.

            Produto produto = produtos.get(res);

            if (produto == null) {
                leitor.setStatusText("Produto " + res + " não encontrado");
                return;
            }

            if (res.equals(lastProduto.getCodDBarras())) {
                return;
            }

            String anterior = "";
            int total = quantidade.getValue();
            lastProduto.setQuantidade(total);
            carrinho.push(lastProduto);

            if (carrinho.size() >= 2) {
                anterior = total + " " + lastProduto.getNome() + " adicionados ao carrinho e\n";
            }

            quantidade.setValue(1);
            lastProduto = produto;

            leitor.setStatusText(anterior + produto.getNome() + " lido com sucesso.");
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void nenhumProduto() {
        AlertDialog.Builder erro = new AlertDialog.Builder(this);
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
        carrinho = new Stack<>();

        quantidade = (NumberPicker) findViewById(R.id.caixa_quantidade);
        quantidade.setMaxValue(100);
        quantidade.setValue(1);
        quantidade.setMinValue(1);

        leitor = (DecoratedBarcodeView) findViewById(R.id.caixa_leitor);
        leitor.setStatusText("Carregando...");
        final Dialog s = Util.dialogoCarregando(this);

        Produto.DBROOT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Produto>> tipo =
                        new GenericTypeIndicator<Map<String, Produto>>() {
                        };
                long time = System.currentTimeMillis();
                produtos = dataSnapshot.getValue(tipo);
                long total = System.currentTimeMillis() - time;

                if (produtos == null) {
                    nenhumProduto();
                    return;
                }

                leitor.setStatusText("Pronto!");
                s.dismiss();
                //FIXME remove!
                Util.showToast(Caixa.this, "Lido com " + total + " millisegundos");
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

    public void removerUltimoProduto(View v) {
        Produto fora = carrinho.pop();
        leitor.setStatusText(fora.getNome() + " foi removido da lista");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        produtos = null;
    }
}
