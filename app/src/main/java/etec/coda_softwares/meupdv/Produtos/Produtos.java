package etec.coda_softwares.meupdv.Produtos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.rey.material.widget.ListView;

import etec.coda_softwares.meupdv.R;

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

    public void pesquisar(View v){
        Toast.makeText(v.getContext(), "We Found! Nothing!", Toast.LENGTH_LONG).show();
    }
    private void populateList(){
        ListView l = (ListView) findViewById(R.id.lista_produtos);
        ListaProdutosAdapter adapter = new ListaProdutosAdapter(this, R.layout.menu_principal_item,
                R.id.menu_item_Title);
        // TODO: Trocar ic_produtos, pelo icone do produto, provavelmente estara no aparelho
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Dolly", 1));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Coca-Cola", 2.5));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Fanta", 6.3));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Cheetos", 5));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Biribinha", 2.5));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "bala de mel", .10));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Caneta", 1.5));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Lapin", 1.0));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Borracha", 3.5));
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Caderno", 19.99));
        l.setAdapter(adapter);
    }
}
