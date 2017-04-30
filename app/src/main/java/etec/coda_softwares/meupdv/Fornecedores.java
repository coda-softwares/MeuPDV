package etec.coda_softwares.meupdv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import etec.coda_softwares.meupdv.entitites.Fornecedor;

public class Fornecedores extends AppCompatActivity {
    private static final int REQ_NOVO_FORNECEDOR = 120;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_fornecedores, menu);
        MenuItem item = menu.findItem(R.id.menu_fornecedor_add);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Fornecedores.this, CadastrarFornecedor.class);
                startActivity(intent);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fornecedores);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fornecedores_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        populateList();
    }

    private void populateList() {
        ListView list = (ListView) findViewById(R.id.fornecedores_lista);
        DatabaseReference fornecedores = FirebaseDatabase.getInstance().getReference("pdv")
                .child(TelaInicial.getCurrentPdv().getId()).child("fornecedores");

        FirebaseListAdapter<Fornecedor> adapter = new FirebaseListAdapter<Fornecedor>(this,
                Fornecedor.class, R.layout.fornecedor_item, fornecedores.orderByKey()) {
            @Override
            protected void populateView(View v, Fornecedor model, int position) {
                TextView titulo = (TextView) v.findViewById(R.id.fornecedor_item_nome),
                        telefone = (TextView) v.findViewById(R.id.fornecedor_item_telefone);
                //TODO: Carregar imagem, nescessita rework
                CircleImageView imagem = (CircleImageView) v.findViewById(R.id.fornecedor_img);
                imagem.setImageResource(R.drawable.ic_def_fornecedor);
                titulo.setText(model.getNome());
                telefone.setText(model.getTelefones().get(0));
            }
        };

        list.setAdapter(adapter);
    }
}
