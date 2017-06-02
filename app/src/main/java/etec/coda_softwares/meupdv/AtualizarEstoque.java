package etec.coda_softwares.meupdv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import etec.coda_softwares.meupdv.entitites.Produto;

public class AtualizarEstoque extends AppCompatActivity
{

    public EditText searchInput;
    public ListView listaProdutos;

    @Override
    protected boolean onCreateOptionsMenu(Bundle savedInstanceState){
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_produtos, menu);
        // TODO: Criar novo (XML CONFIRMA ICON)
        MenuItem item = menu.findItem(R.id.menu_atualizar_confirma);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO: Salvar modificações
                // ~ for produtos from listaProdutos
                // - Usar updateQuantidade(Produto)
                return true;
            }
        });
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_estoque_dialog);
        searchInput = (EditText) findViewById(R.id.search_input);
        listaProdutos = (ListView) findViewById(R.id.lista_produtos);

        populateList();
    }

    public void pesquisar(View v){
        Util.pesquisarProduto(searchInput.getText().toString().trim(), listaProdutos);
        Toast.makeText(AtualizarEstoque.this, "Esta opção esta sendo desenvolvida! ;)", Toast.LENGTH_LONG).show();
    }
    public void populateList(){
        DatabaseReference reference = Produto.DBROOT;
        FirebaseListAdapter<Produto> produtos = new FirebaseListAdapter<Produto>(this,
                Produto.class, R.layout.produtos_item, reference.orderByKey()) {

            @Override
            protected void populateView(View v, final Produto model, int position) {
                TextView nome = (TextView) v.findViewById(R.id.prod_nome);
                nome.setText(model.getNome());

                TextView valor = (TextView) v.findViewById(R.id.prod_valor);
                valor.setText(model.getValor());

                TextView quantidade = (TextView) v.findViewById(R.id.prod_quant);
                quantidade.setText(model.getQuantidade() + "");

                // TODO: Colocar Icones de "Menos" e de "Mais".
                ImageButton buttonMinus = (ImageButton) v.findViewById(R.id.prod_icon);
                ImageButton buttonPlus = (ImageButton) v.findViewById(R.id.prod_button);

                /**
                 * As modificações só serão salvas depois da confirmação
                 */
                buttonMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // É preciso fazer um check se isso realmente funciona
                        // Acredito que sim
                        model.setQuantidade(model.getQuantidade() - 1);

                    }
                });
                buttonPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.setQuantidade(model.getQuantidade() + 1);
                    }
                });
            }
        };
        listaProdutos.setAdapter(produtos);
    }
    public void updateQuantidade(Produto prod){
        DatabaseReference ref = Produto.DBROOT;
        ref.child(prod.getCodDBarras()).child("quantidade").setValue(prod.getQuantidade())
    }

}
