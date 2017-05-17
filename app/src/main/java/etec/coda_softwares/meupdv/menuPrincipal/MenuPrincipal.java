package etec.coda_softwares.meupdv.menuPrincipal;

import android.content.Intent;
import android.net.Uri;
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

import de.hdodenhof.circleimageview.CircleImageView;
import etec.coda_softwares.meupdv.Caixa;
import etec.coda_softwares.meupdv.Fornecedores;
import etec.coda_softwares.meupdv.Produtos;
import etec.coda_softwares.meupdv.R;
import etec.coda_softwares.meupdv.TelaInicial;
import etec.coda_softwares.meupdv.entitites.PDV;

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
        TextView titulo = (TextView) findViewById(R.id.mp_pdv_nome);
        TextView descr = (TextView) findViewById(R.id.mp_pdv_desc);
        PDV esse = TelaInicial.getCurrentPdv();
        titulo.setText(esse.getNome());
        descr.setText(esse.getLema());
        TelaInicial.getFile(esse.getImagem(), new TelaInicial.UriCallback() {
            @Override
            public void done(Uri u) {
                CircleImageView imagem = (CircleImageView) findViewById(R.id.mp_pdv_img);
                if (u != null)
                    imagem.setImageURI(u);
            }
        });

    }

    private void populateGrid(){
        FlowLayout g = (FlowLayout) findViewById(R.id.mp_gridItems);
        LayoutInflater factory = LayoutInflater.from(this);
        ImageView v;
        TextView titulo;
        //Essa lista contem todos os items do menu inicial. adicione mais um ItemMenuPrincipal
        //no metodo asList, pra adicionar mais itens.
        List<ItemMenuPrincipal> itens = Arrays.asList(

                new ItemMenuPrincipal(R.drawable.ic_produtos, "Produtos", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MenuPrincipal.this, Produtos.class));
                    }
                }),
                new ItemMenuPrincipal(R.drawable.ic_def_fornecedor, "Fornecedores", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MenuPrincipal.this, Fornecedores.class));
                    }
                }),
                new ItemMenuPrincipal(R.drawable.ic_caixa, "Caixa", new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MenuPrincipal.this, Caixa.class));
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

