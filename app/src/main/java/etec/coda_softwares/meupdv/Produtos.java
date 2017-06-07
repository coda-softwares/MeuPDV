package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

import etec.coda_softwares.meupdv.entitites.Produto;

public class Produtos extends AppCompatActivity {

    public EditText searchInput;
    public ListView listaProdutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.prods_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listaProdutos = (ListView) findViewById(R.id.lista_produtos);
        searchInput = (EditText) findViewById(R.id.search_input);

        updateListViewAdapter(Produto.DBROOT.orderByKey());
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

    public void pesquisar(View v) {
        String searchString = searchInput.getText().toString().trim().toLowerCase();

        Query query;
        if (searchString.equals("")) {
            query = Produto.DBROOT.orderByKey();
        } else {
            query = Produto.DBROOT.orderByChild("nome").startAt(searchString)
                    .endAt(searchString+"\uf8ff");
        }
        updateListViewAdapter(query);
    }

    /**
     *
     * @param query Define quais produtos serão listados
     */
    private void updateListViewAdapter(Query query){
        FirebaseListAdapter<Produto> produtos = new FirebaseListAdapter<Produto>(this,
                Produto.class, R.layout.produtos_item, query) {
            @Override
            protected void populateView(View v, final Produto model, int position) {
                TextView nome = (TextView) v.findViewById(R.id.prod_nome);
                nome.setText(model.getNome());

                TextView valor = (TextView) v.findViewById(R.id.prod_valor);
                valor.setText(model.getValor());

                TextView quantidade = (TextView) v.findViewById(R.id.prod_quant);
                quantidade.setText(model.getQuantidade() + "");

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showOptions(model);
                    }
                });
            }
        };

        listaProdutos.setAdapter(produtos);

    }
    void showOptions(final Produto item) {
        AlertDialog.Builder fabrica = new AlertDialog.Builder(this);
        fabrica.setTitle(item.getNome().split(" ")[0]);
        fabrica.setCancelable(true);

        final ArrayAdapter<String> opcoes =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        opcoes.add("Editar");
        opcoes.add("Apagar");
        fabrica.setAdapter(opcoes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String opcao = opcoes.getItem(which);
                assert opcao != null;

                if (opcao.equalsIgnoreCase("editar")) {
                    Intent i = new Intent(Produtos.this, CadastrarProduto.class);
                    i.putExtra("produto", item);
                    startActivity(i);
                } else if (opcao.equalsIgnoreCase("apagar")) {
                    TelaInicial.eraseFile(item.getImagem());
                    Produto.DBROOT.child(item.getCodDBarras()).setValue(null);
                }
            }
        });
        fabrica.show();
    }
}
