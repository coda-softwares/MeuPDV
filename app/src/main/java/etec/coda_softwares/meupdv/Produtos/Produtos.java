package etec.coda_softwares.meupdv.Produtos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import etec.coda_softwares.meupdv.CadastrarProduto;
import etec.coda_softwares.meupdv.R;
import etec.coda_softwares.meupdv.TelaInicial;
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
        // Test
        DatabaseReference refProdutos = FirebaseDatabase.getInstance()
                .getReference("pdv").child(TelaInicial.getCurrentPdv().getId()).child("produtos");

        List testValues = new ArrayList();

        testValues.add(new Produto("Maçã", 1.5, 25, "334254245"));
        testValues.add(new Produto("Pera", .5, 35, "735287580"));

        refProdutos.setValue(testValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Produtos.this, "Success!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void add_produto(View view) {
        //Toast.makeText(view.getContext(), "Ohppa gangnam style!", Toast.LENGTH_SHORT).show();
        view.getContext().startActivity(new Intent(Produtos.this, CadastrarProduto.class));
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
