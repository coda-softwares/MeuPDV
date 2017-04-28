package etec.coda_softwares.meupdv.Produtos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.rey.material.widget.ListView;

import etec.coda_softwares.meupdv.CadastrarFornecedor;
import etec.coda_softwares.meupdv.CadastrarProduto;
import etec.coda_softwares.meupdv.Produto;
import etec.coda_softwares.meupdv.R;
import etec.coda_softwares.meupdv.menuPrincipal.ItemMenuPrincipal;
import etec.coda_softwares.meupdv.menuPrincipal.MenuPrincipal;
import etec.coda_softwares.meupdv.menuPrincipal.MenuPrincipalAdapter;

public class Produtos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.prods_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void pesquisar(View v){
        Toast.makeText(v.getContext(), "We Found! Nothing!", Toast.LENGTH_LONG).show();
    }
    private void populateList(){
        ListView l = (ListView) findViewById(R.id.lista_produtos);
        ListaProdutosAdapter adapter = new ListaProdutosAdapter(this, R.layout.menu_principal_item,
                R.id.item_Title);
        // TODO: Trocar ic_produtos, pelo icone do produto, provavelmente estara no aparelho
        adapter.add(new ItemProdutos(R.drawable.ic_produtos, "Dolly", 1.0));
        l.setAdapter(adapter);
    }
}
