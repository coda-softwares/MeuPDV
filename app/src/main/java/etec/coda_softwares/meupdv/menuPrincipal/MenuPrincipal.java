package etec.coda_softwares.meupdv.menuPrincipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import etec.coda_softwares.meupdv.CadastrarFornecedor;
import etec.coda_softwares.meupdv.CadastrarProduto;
import etec.coda_softwares.meupdv.Produtos.Produtos;
import etec.coda_softwares.meupdv.R;

public class MenuPrincipal extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_principal_menu, menu);
        menu.findItem(R.id.mp_sair)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        System.exit(0);
                        return false;
                    }
                });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        Toolbar ab = (Toolbar) findViewById(R.id.mp_toolbar);
        setSupportActionBar(ab);
        populateGrid();
    }
    private void populateGrid(){
        GridView g = (GridView) findViewById(R.id.mp_gridItems);
        MenuPrincipalAdapter adapter = new MenuPrincipalAdapter(this, R.layout.menu_principal_item,
                R.id.item_Title);
        adapter.add(new ItemMenuPrincipal(R.drawable.ic_entrega, "Registrar Estoque",
                new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MenuPrincipal.this, CadastrarProduto.class);
                startActivity(i);
            }
        }));

        adapter.add(new ItemMenuPrincipal(R.drawable.ic_fornecedor, "Cadastrar Fornecedor",
                new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MenuPrincipal.this, CadastrarFornecedor.class));
            }
        }));
        adapter.add(new ItemMenuPrincipal(R.drawable.ic_produtos, "Produtos", new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MenuPrincipal.this, Produtos.class));
            }
        }));
        g.setAdapter(adapter);
    }
}

