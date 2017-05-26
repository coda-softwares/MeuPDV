package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import etec.coda_softwares.meupdv.entitites.Produto;

public class Caixa extends AppCompatActivity {
    public static final int REQ_CAIXA = 7847;
    private Produto dummy = new Produto("$$$DUMMY", new Date(), "", 0, "", null);
    private Produto lastProduto = dummy;
    private DecoratedBarcodeView leitor;
    private Map<String, Produto> produtos;
    private Stack<Produto> carrinho;
    private NumberPicker quantidade;
    private BarcodeCallback callback = new BarcodeCallback() {
        List<BarcodeFormat> excluded = Arrays.asList( //TODO faltam mais tipos pra excluir
                BarcodeFormat.QR_CODE,
                BarcodeFormat.DATA_MATRIX
        );

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (excluded.contains(result.getBarcodeFormat())) {
                return;
            }

            String res = result.getText(); // Nunca será nulo.
            Util.showToast(Caixa.this, "Código de barras " + res + " lido!");

            Produto produto = produtos.get(res);

            if (produto == null) {
                leitor.setStatusText("Produto " + res + " não encontrado");
                return;
            }

            if (res.equals(lastProduto.getCodDBarras())) {
                return;
            }

            int total = quantidade.getValue();
            quantidade.setMaxValue(produto.getQuantidade());

            String anterior = "";
            if (!lastProduto.getNome().equals(dummy.getNome())) {
                lastProduto.setQuantidade(total);
                carrinho.push(lastProduto);
            } else {
                quantidade.setValue(1);
                quantidade.setMinValue(1);
            }

            if (carrinho.size() >= 2) {
                anterior = total + " " + lastProduto.getNome() + " adicionados ao carrinho e\n";
            }

            quantidade.setValue(1);
            lastProduto = new Produto(produto.getNome(), produto.getValidade(),
                    produto.getValor(), produto.getQuantidade(), produto.getCodDBarras(),
                    produto.getFornecedor());
            lastProduto.setCodDBarras(res);

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
                startActivityForResult(new Intent(Caixa.this, CadastrarProduto.class), REQ_CAIXA);
            }
        });
        erro.show();
    }

    private void finalizarCompra() {
        if (carrinho.isEmpty() && lastProduto.getNome().equals(dummy.getNome())) {
            Util.showToast(this, "Nenhum produto escaneado...");
            return;
        }
        int total = quantidade.getValue();
        lastProduto.setQuantidade(total);
        carrinho.push(lastProduto);
        Intent fim = new Intent(this, PosCaixa.class);
        fim.putExtra("carrinho", carrinho); // Stack é serializavel.
        startActivityForResult(fim, REQ_CAIXA);
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
        quantidade.setValue(0);
        quantidade.setMaxValue(0);
        quantidade.setMinValue(0);

        ImageButton confirma = (ImageButton) findViewById(R.id.caixa_confirma);
        confirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarCompra();
            }
        });

        leitor = (DecoratedBarcodeView) findViewById(R.id.caixa_leitor);
        leitor.setStatusText("Carregando...");
        final Dialog s = Util.dialogoCarregando(this);

        Produto.DBROOT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Produto>> tipo =
                        new GenericTypeIndicator<Map<String, Produto>>() {
                        };
                produtos = dataSnapshot.getValue(tipo);

                if (produtos == null) {
                    nenhumProduto();
                    return;
                }

                leitor.setStatusText("Pronto!");
                if (s != null)
                    if (s.isShowing()) s.dismiss();
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
        if (carrinho.empty()) {
            leitor.setStatusText("Lista de produtos vazia");
            return;
        }
        Produto fora = carrinho.pop();
        leitor.setStatusText(fora.getNome() + " foi removido da lista");
        if (carrinho.empty()) {
            lastProduto = dummy;
            quantidade.setMinValue(0);
            quantidade.setValue(0);
            quantidade.setMaxValue(0);
            return;
        }
        quantidade.setValue(0);
        quantidade.setMaxValue(carrinho.peek().getQuantidade());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_CAIXA) {
                finish();
            }
        }
        //TODO outros codigos de resultado.
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        leitor.onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }
}
