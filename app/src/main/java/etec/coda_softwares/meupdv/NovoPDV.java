package etec.coda_softwares.meupdv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import etec.coda_softwares.meupdv.entitites.PDV;

public class NovoPDV extends AppCompatActivity {
    private static final String TAG = NovoPDV.class.getName();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_confirma, menu);
        menu.findItem(R.id.botao_confirma)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String imgPDV = "";
                        String nomePDV = ((EditText) findViewById(R.id.npdv_nome)).getText().toString().trim();
                        String lemaPDV = ((EditText) findViewById(R.id.npdv_lema)).getText().toString().trim();

                        if (nomePDV.equals("")) {
                            Toast.makeText(NovoPDV.this, "Nome não pode ser vazio",
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        if (lemaPDV.equals("")) {
                            Toast.makeText(NovoPDV.this, "Lema não pode ser vazio",
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        PDV pdv = new PDV(nomePDV, Arrays.asList(user.getUid()), imgPDV, lemaPDV);
                        pdv.saveOnDB();
                        Intent i = getIntent();
                        i.putExtra("pdv", pdv);
                        setResult(RESULT_OK, i);
                        finish();
                        return true;
                    }
                });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_pdv);
        Toolbar tb = (Toolbar) findViewById(R.id.npdv_toolbar);
        setSupportActionBar(tb);
    }
}
