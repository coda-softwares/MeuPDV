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
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Collections;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.Fornecedor;

public class CadastrarFornecedor extends AppCompatActivity {
    private static final int REQUEST_FOTO = 598;
    private Uri image;
    private EditText etNome;
    private EditText etEmail;
    private EditText etTelefone;
    private ImageView ivFoto;

    private void createFornecedor(String nome, String email, List<String> telefones) {
        final Dialog d = new AlertDialog.Builder(this).setView(R.layout.layout_loading).create();
        d.show();

        String key = getIntent().getStringExtra("id");
        final Fornecedor f = new Fornecedor(nome, email, telefones);
        final DatabaseReference dbLocation = key == null ?
                Fornecedor.DBROOT.push() : Fornecedor.DBROOT.child(key);

        if (image != null) { //Se imagem nao for nula
            //Um objeto task é obtido ao acessar o caminho:
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference("pdv") //pdv
                    .child(TelaInicial.getCurrentPdv().getId()) // pdv/[pdv_id]
                    .child("fornecedores") // pdv/[pdv_id]/fornecedores
                    .child(dbLocation.getKey() + ".jpg");//pdv/[pdv_id]/fornecedores/id.jpg
            Fornecedor fornecedor = (Fornecedor) getIntent().getSerializableExtra("fornecedor");
            if (fornecedor != null) if (!fornecedor.getImagem().equals("")) {
                TelaInicial.eraseFile(fornecedor.getImagem(), true);
            }

            //salvamos a imagem no objeto F antes do upload.
            f.setImagem(storageReference.toString());
            // Iniciamos o upload da foto
            UploadTask uploadTask = storageReference.putFile(image);//quando o upload terminar...
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> t) {
                    dbLocation.setValue(f).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            d.dismiss();
                            finish();
                        }
                    });
                }
            });
        } else {
            dbLocation.setValue(f).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    d.dismiss();
                    finish();
                }
            });
        }
    }

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

        Toolbar tbar = (Toolbar)findViewById(R.id.fornecedor_toolbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNome = (EditText) findViewById(R.id.nfornecedor_nome);
        etEmail = (EditText) findViewById(R.id.nfornecedor_email);
        etTelefone = (EditText) findViewById(R.id.nfornecedor_telefone);
        ivFoto = (ImageView) findViewById(R.id.nfornecedor_img);

        etTelefone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        Fornecedor edit = (Fornecedor) getIntent().getSerializableExtra("fornecedor");
        if (edit != null) {
            etNome.setText(edit.getNome());
            etEmail.setText(edit.formatEmail());
            etTelefone.setText(edit.getTelefones().get(0));
            TelaInicial.getFile(edit.getImagem(), new TelaInicial.UriCallback() {
                @Override
                void done(Uri u) {
                    ivFoto.setPadding(0, 0, 0, 0);
                    ivFoto.setImageURI(u);
                }
            });
        }
    }

    public void addFoto(View v) {
        startActivityForResult(CropImage.getPickImageChooserIntent(this), REQUEST_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FOTO) {
                Uri img;
                if (data == null)
                    img = CropImage.getCaptureImageOutputUri(this);
                else
                    img = data.getData();
                assert img != null;
                CropImage.activity(img).setAspectRatio(1, 1).setOutputCompressQuality(70)
                        .setRequestedSize(512, 512).setMinCropResultSize(256, 256).start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                ivFoto.setPadding(0, 0, 0, 0);
                this.image = CropImage.getActivityResult(data).getUri();
                ivFoto.setImageURI(this.image);
            }
        }
    }

}
