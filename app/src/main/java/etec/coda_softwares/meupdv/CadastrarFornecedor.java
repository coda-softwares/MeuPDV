package etec.coda_softwares.meupdv;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import etec.coda_softwares.meupdv.entitites.Contato;
import etec.coda_softwares.meupdv.entitites.Fornecedor;

public class CadastrarFornecedor extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.menu_confirma, menu);
        menu.findItem(R.id.botao_confirma)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String telefone = ((EditText) findViewById(R.id.fornecedor_telefone))
                                .getText().toString().trim();
                        String email = ((EditText) findViewById(R.id.fornecedor_email)).getText()
                                .toString().trim();
                        String nome = ((EditText) findViewById(R.id.fornecedor_nome)).getText()
                                .toString().trim();
                        if (telefone.equals("")) {
                            Toast.makeText(CadastrarFornecedor.this, "Telefone nao pode ser vazio",
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (email.equals("")) {
                            Toast.makeText(CadastrarFornecedor.this, "Email nao pode ser vazio",
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (nome.equals("")) {
                            Toast.makeText(CadastrarFornecedor.this, "Nome nao pode ser vazio",
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        final AlertDialog.Builder ab = new AlertDialog.Builder(CadastrarFornecedor.this);
                        ab.setView(R.layout.layout_loading);
                        final Dialog d = ab.create();
                        d.show();
                        Contato c = new Contato(telefone, email);
                        Fornecedor f = new Fornecedor(nome, c);
                        TelaInicial.getCurrentPdv().addFornecedor(f, new Runnable() {
                            @Override
                            public void run() {
                                d.dismiss();
                                finish();
                                Toast.makeText(getBaseContext(), "Cadastrado com sucesso",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        return true;
                    }
                });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_fornecedor);

        Toolbar tbar = (Toolbar)findViewById(R.id.fornecedor_toolbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
