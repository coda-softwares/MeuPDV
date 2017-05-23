package etec.coda_softwares.meupdv;

import android.app.Dialog;
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

import de.hdodenhof.circleimageview.CircleImageView;
import etec.coda_softwares.meupdv.entitites.PDV;

public class EditPDV extends AppCompatActivity {
    private static final int REQ_IMG = 2302;
    PDV novoPdv;
    private CircleImageView imagemView;
    private Uri image;
    private EditText lemaView;
    private EditText nomeView;

    private void terminar() {
        String nome = nomeView.getText().toString().trim();
        String lema = lemaView.getText().toString().trim();
        if (Util.temStringVazia(nome, lema)) {
            Util.showToast(this, "Campos vazios nao permitidos.");
            return;
        }
        Dialog loading = Util.dialogoCarregando(this);
        novoPdv.setNome(nome);
        novoPdv.setLema(lema);
        if (image != null) {
            TelaInicial.eraseFile(novoPdv.getImagem());
            novoPdv.setImagem(image);
        }
        PDV.ROOT.child(novoPdv.getId()).setValue(novoPdv);
        TelaInicial.CURRENT_PDV = novoPdv;
        setResult(RESULT_OK);
        loading.dismiss();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater a = new MenuInflater(this);
        a.inflate(R.menu.menu_confirma, menu);
        MenuItem confirma = menu.findItem(R.id.botao_confirma);
        confirma.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                terminar();
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_IMG) {
                this.image = data.getParcelableExtra("imagem");
                imagemView.setImageURI(this.image);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pdv);
        novoPdv = TelaInicial.CURRENT_PDV;

        Toolbar tb = (Toolbar) findViewById(R.id.epdv_toobar);
        setSupportActionBar(tb);

        lemaView = (EditText) findViewById(R.id.epdv_lema);
        nomeView = (EditText) findViewById(R.id.epdv_nome);
        imagemView = (CircleImageView) findViewById(R.id.epdv_img);

        imagemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n = new Intent(EditPDV.this, CarregarImagem.class);
                startActivityForResult(n, REQ_IMG);
            }
        });
        nomeView.setText(novoPdv.getNome());
        lemaView.setText(novoPdv.getLema());
        if (!novoPdv.getImagem().equals("")) {
            TelaInicial.getFile(novoPdv.getImagem(), new TelaInicial.UriCallback() {
                @Override
                public void done(Uri u) {
                    imagemView.setImageURI(u);
                }
            });
        }

    }
}
