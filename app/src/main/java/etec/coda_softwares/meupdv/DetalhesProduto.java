package etec.coda_softwares.meupdv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

// Esta atividade servira para mostrar informações especificas do produto
public class DetalhesProduto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.produto_toolbar);
        setSupportActionBar(toolbar);

        // Terminar o XML em base aos extras
        String nome = getIntent().getStringExtra("nome");
        String valor = getIntent().getStringExtra("valor");
        String cdDBarras = getIntent().getStringExtra("cdDBarras");

        toolbar.setTitle(nome);
    }

}
