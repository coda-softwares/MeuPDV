package etec.coda_softwares.meupdv;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

// Esta atividade servira para mostrar informações especificas do produto
public class DetalhesProduto extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.produto_toolbar);
        setSupportActionBar(toolbar);

        // Terminar o XML em base aos extras
        String nome = savedInstanceState.getString("nome");
        String valor = savedInstanceState.getString("valor");
        String cdDBarras = savedInstanceState.getString("cdDBarras");

        toolbar.setTitle(nome);
    }

}
