package etec.coda_softwares.meupdv.menuPrincipal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.Arrays;
import java.util.List;

import etec.coda_softwares.meupdv.CadastrarProduto;
import etec.coda_softwares.meupdv.Fornecedores;
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
        FlowLayout g = (FlowLayout) findViewById(R.id.mp_gridItems);
        LayoutInflater factory = LayoutInflater.from(this);
        ImageView v;
        TextView titulo;
        //Essa lista contem todos os items do menu inicial. adicione mais um ItemMenuPrincipal
        //no metodo asList, pra adicionar mais itens.
        List<ItemMenuPrincipal> itens = Arrays.asList(
                new ItemMenuPrincipal(R.drawable.ic_entrega, "Registrar Produto", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MenuPrincipal.this, CadastrarProduto.class));
                    }
                }),
                new ItemMenuPrincipal(R.drawable.ic_def_fornecedor, "Fornecedores", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MenuPrincipal.this, Fornecedores.class));
                    }
                }),
                new ItemMenuPrincipal(R.drawable.ic_produtos, "Ver Produtos", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MenuPrincipal.this, Produtos.class));
                    }
                })
        );

        //Carrega e coloca no menu os items
        for (final ItemMenuPrincipal m : itens) {
            View menuItem = factory.inflate(R.layout.menu_principal_item, g, false);
            titulo = (TextView) menuItem.findViewById(R.id.menu_item_Title);
            titulo.setText(m.getTitulo());
            v = (ImageView) menuItem.findViewById(R.id.menu_item_img);
            v.setImageResource(m.getImage());
            menuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m.getAction().run();
                }
            });
            g.addView(menuItem);
        }
    }
}

