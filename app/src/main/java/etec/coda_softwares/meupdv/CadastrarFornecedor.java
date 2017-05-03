package etec.coda_softwares.meupdv;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Collections;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.Fornecedor;

public class CadastrarFornecedor extends AppCompatActivity {
    public static final String NO_IMG = "$NOIMG$";
    private static final int REQUEST_FOTO = 598;
    private boolean newImage = false;

    private EditText etNome;
    private EditText etEmail;
    private EditText etTelefone;
    private Uri image;
    private ImageView ivFoto;
    private Fornecedor old;
    private Dialog loading;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.menu_confirma, menu);
        MenuItem item = menu.findItem(R.id.botao_confirma);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String telefone = etTelefone.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String nome = etNome.getText().toString().trim();
                if (nome.equals("")) {
                    Toast.makeText(CadastrarFornecedor.this, R.string.nome_vazio,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (email.equals("")) {
                    Toast.makeText(CadastrarFornecedor.this, R.string.email_vazio,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (telefone.equals("")) {
                    Toast.makeText(CadastrarFornecedor.this, R.string.telefone_vazio,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
                createFornecedor(nome, email, Collections.singletonList(telefone));
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_fornecedor);


        loading = new AlertDialog.Builder(this).setView(R.layout.layout_loading).create();
        Toolbar tbar = (Toolbar)findViewById(R.id.fornecedor_toolbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNome = (EditText) findViewById(R.id.nfornecedor_nome);
        etEmail = (EditText) findViewById(R.id.nfornecedor_email);
        etTelefone = (EditText) findViewById(R.id.nfornecedor_telefone);
        ivFoto = (ImageView) findViewById(R.id.nfornecedor_img);

        etTelefone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        old = (Fornecedor) getIntent().getSerializableExtra("fornecedor");
        if (old != null) {
            etNome.setText(old.getNome());
            etEmail.setText(old.formatEmail());
            etTelefone.setText(old.getTelefones().get(0));
            if (old.hasImagem()) {
                TelaInicial.getFile(old.getImagem(), new TelaInicial.UriCallback() {
                    @Override
                    void done(Uri u) {
                        ivFoto.setPadding(0, 0, 0, 0);
                        image = u;
                        ivFoto.setImageURI(u);
                    }
                });
            }
        }
    }

    private void saveAndFinish(Fornecedor f, DatabaseReference dbLocation) {
        dbLocation.setValue(f).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loading.dismiss();
                finish();
            }
        });
    }

    private void createFornecedor(String nome, String email, List<String> telefones) {
        loading.show();

        final Fornecedor f = new Fornecedor(nome, email, telefones);

        final DatabaseReference dbLocation;
        dbLocation = old == null ? Fornecedor.DBROOT.push() : Fornecedor.DBROOT.child(old.getId());

        if (image != null) {
            if (old != null) { // If editing.
                if (!newImage) {
                    f.setImagem(old.getImagem());
                    saveAndFinish(f, dbLocation);
                    return;
                }
                if (old.hasImagem())
                    TelaInicial.eraseFile(old.getImagem(), true);
            }

            //Se imagem nao for nula
            //Um objeto task é obtido ao acessar o caminho:
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference("pdv") //pdv
                    .child(TelaInicial.getCurrentPdv().getId()) // pdv/[pdv_id]
                    .child("fornecedores").child(dbLocation.getKey() + ".jpg"); // pdv/[pdv_id]/fornecedores/[forn_id]


            //salvamos a imagem no objeto F antes do upload.
            f.setImagem(storageReference.toString());
            // Iniciamos o upload da foto
            UploadTask uploadTask = storageReference.putFile(image);//quando o upload terminar...
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> t) {
                    saveAndFinish(f, dbLocation);
                }
            });
        } else {
            f.setImagem(CadastrarFornecedor.NO_IMG + "/" + dbLocation.getKey());
            saveAndFinish(f, dbLocation);
        }
    }

    public void addFoto(View v) {
        startActivityForResult(new Intent(this, CarregarImagem.class), REQUEST_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FOTO) {
                newImage = true;
                image = data.getParcelableExtra("imagem");
                ivFoto.setPadding(0, 0, 0, 0);
                ivFoto.setImageURI(image);
            }
        }
    }

}
