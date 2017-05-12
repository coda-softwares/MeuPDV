package etec.coda_softwares.meupdv.Produtos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import java.text.NumberFormat;

import etec.coda_softwares.meupdv.CadastrarProduto;
import etec.coda_softwares.meupdv.DetalhesProduto;
import etec.coda_softwares.meupdv.R;
import etec.coda_softwares.meupdv.entitites.Produto;

public class Produtos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.prods_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_produtos, menu);
        MenuItem item = menu.findItem(R.id.menu_produtos_add);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Produtos.this, CadastrarProduto.class);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    public void pesquisar(View v){
        Toast.makeText(Produtos.this, "Sorry did not get that.", Toast.LENGTH_LONG).show();
    }

    public void add_produto(View view) {
        view.getContext().startActivity(new Intent(Produtos.this, CadastrarProduto.class));
    }
    private void populateList(){
        ListView lista_produtos = (ListView) findViewById(R.id.lista_produtos);

        /**
         * Yet Simple
         */
        DatabaseReference reference = Produto.DBROOT;
        FirebaseListAdapter<Produto> produtos = new FirebaseListAdapter<Produto>(this,
                Produto.class, R.layout.produtos_item, reference.orderByKey()) {

            private final NumberFormat formatter = NumberFormat.getCurrencyInstance();

            @Override
            protected void populateView(View v, final Produto model, int position) {
                TextView nome = (TextView) v.findViewById(R.id.prod_nome);
                nome.setText(model.getNome());

                TextView valor = (TextView) v.findViewById(R.id.prod_valor);
                valor.setText(model.getValor());

                ImageButton button = (ImageButton) v.findViewById(R.id.prod_button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Produtos.this, DetalhesProduto.class);

                        intent.putExtra("nome", model.getNome());
                        intent.putExtra("valor", model.getValor());
                        intent.putExtra("cdDBarras", model.getCodDBarras());

                        view.getContext().startActivity(intent);
                    }
                });
            }
        };

        lista_produtos.setAdapter(produtos);
    }
}
