package etec.coda_softwares.meupdv;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.HandlerPadrao;
import etec.coda_softwares.meupdv.entitites.Produto;

public class PosCaixa extends AppCompatActivity {
    BigDecimal total = new BigDecimal(0);
    private TextView precoTotal;
    private ArrayList<Produto> produtos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_caixa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.poscaixa_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        precoTotal = (TextView) findViewById(R.id.poscaixa_preco);

        //noinspection unchecked
        produtos = (ArrayList<Produto>) getIntent().getSerializableExtra("carrinho");

        ListView lista = (ListView) findViewById(R.id.poscaixa_lista);
        PrecoAdapter precos = new PrecoAdapter(this, android.R.layout.two_line_list_item, produtos);
        lista.setAdapter(precos);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_confirma, menu);
        MenuItem confirma = menu.findItem(R.id.botao_confirma);
        confirma.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                atualizarDatabase();
                setResult(RESULT_OK);
                finish();
                return true;
            }
        });
        return true;
    }

    private void atualizarDatabase() {
        for (final Produto p : produtos) {
            DatabaseReference ref = Produto.DBROOT.child(p.getCodDBarras());
            ref.runTransaction(new HandlerPadrao(null) {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Produto produtoDB = mutableData.getValue(Produto.class);
                    produtoDB.setQuantidade(produtoDB.getQuantidade() - p.getQuantidade());
                    if (produtoDB.getQuantidade() <= 0)
                        produtoDB = null;
                    mutableData.setValue(produtoDB);
                    return Transaction.success(mutableData);
                }
            });
        }

        DatabaseReference log = Produto.DBROOT.getParent().child("vendas").push();
        log.child("produtos").setValue(produtos);
        log.child("data").setValue(System.currentTimeMillis());
        log.child("total").setValue(total.toPlainString());
        log.child("funcionario").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    private class PrecoAdapter extends ArrayAdapter<Produto> {

        public PrecoAdapter(@NonNull Context context, @LayoutRes int resource,
                            @NonNull List<Produto> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int pos, @Nullable View antiga, @NonNull ViewGroup parent) {
            if (antiga == null) {
                antiga = LayoutInflater.from(PosCaixa.this)
                        .inflate(android.R.layout.two_line_list_item, parent, false);
            }
            Produto atual = getItem(pos);

            TextView nomeProduto = (TextView) antiga.findViewById(android.R.id.text1);
            nomeProduto.setText(atual.getNome());
            nomeProduto.setTextColor(0xFF0e0e0e);

            BigDecimal precoUnidade = atual.getValorReal();
            BigDecimal precoTtl = precoUnidade.multiply(new BigDecimal(atual.getQuantidade()));
            total = total.add(precoTtl);
            precoTotal.setText(total.toPlainString());

            TextView valProduto = (TextView) antiga.findViewById(android.R.id.text2);
            valProduto.setText(precoUnidade.toPlainString() + " x " + atual.getQuantidade() +
                    " = " + precoTtl.toPlainString());
            valProduto.setTextColor(getResources().getColor(R.color.cor_preco));

            return antiga;
        }

        //TODO REMOÇÃO E EDIÇÃO DE ITENS DA LISTA!
    }

}
