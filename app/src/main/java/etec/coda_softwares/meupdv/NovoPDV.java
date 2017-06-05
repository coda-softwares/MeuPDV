package etec.coda_softwares.meupdv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

import etec.coda_softwares.meupdv.entitites.PDV;

public class NovoPDV extends AppCompatActivity {
    private static final String TAG = NovoPDV.class.getName();
    private final int REQUEST_FOTO = 444;
    LinearLayout frag;
    Uri image;
    private MenuItem btn_confirma;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_confirma, menu);
        btn_confirma = menu.findItem(R.id.botao_confirma);
        btn_confirma.setVisible(true);
        btn_confirma.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                EditText titulo = (EditText) frag.findViewById(R.id.npdv_nome);
                if (titulo == null) return true;
                String nomePDV = titulo.getText().toString().trim();
                String lemaPDV = ((EditText) frag.findViewById(R.id.npdv_lema)).getText()
                        .toString().trim();

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
                PDV pdv = new PDV(nomePDV, lemaPDV, "", Collections.singletonList(user.getUid()));

                pdv.initId();

                if (image != null) {
                    if (!image.equals(Uri.EMPTY))
                        pdv.setImagem(image);

                }

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
        frag = (LinearLayout) findViewById(R.id.npdv_frag);

        if (frag != null && savedInstanceState == null) {
            FragmentNovoPDV fnpdv = new FragmentNovoPDV();
            getSupportFragmentManager().beginTransaction().add(R.id.npdv_frag, fnpdv).commit();
        }

        Toolbar tb = (Toolbar) findViewById(R.id.npdv_toolbar);
        tb.setTitle(R.string.title_activity_novo_pdv);
        setSupportActionBar(tb);
    }

    public void addFotoNovoPDV(View v) {
        startActivityForResult(new Intent(this, CarregarImagem.class), REQUEST_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FOTO) {
                ImageView image = (ImageView) frag.findViewById(R.id.npdv_img);
                image.setPadding(0, 0, 0, 0);
                this.image = data.getParcelableExtra("imagem");
                image.setImageURI(this.image);
            }
        }
    }
}
