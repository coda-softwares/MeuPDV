package etec.coda_softwares.meupdv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Collections;

import etec.coda_softwares.meupdv.entitites.PDV;

public class NovoPDV extends AppCompatActivity {
    private static final String TAG = NovoPDV.class.getName();
    private final int REQUEST_FOTO = 444;
    private final int CORTAR_FOTO = 888;
    Uri image;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_confirma, menu);
        MenuItem confirma = menu.findItem(R.id.botao_confirma);
        confirma.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String nomePDV = ((EditText) findViewById(R.id.npdv_nome)).getText()
                        .toString().trim();
                String lemaPDV = ((EditText) findViewById(R.id.npdv_lema)).getText()
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
                final PDV pdv = new PDV(nomePDV, Collections.singletonList(user.getUid()),
                        lemaPDV);

                if (image != null) {
                    final Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        Handler h = handler;
                        Uri image = NovoPDV.this.image;

                        @Override
                        public void run() {
                            PDV currentPdv = TelaInicial.getCurrentPdv();
                            if (currentPdv != null)
                                if (!currentPdv.getId().equals("")) {
                                    StorageReference s = FirebaseStorage.getInstance()
                                            .getReference("pdvLogo")
                                            .child(currentPdv.getId() + ".jpg");
                                    s.putFile(image);
                                    return;
                                }
                            h.postDelayed(this, 300);
                        }
                    };
                    handler.postDelayed(r, 1000);
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
        Toolbar tb = (Toolbar) findViewById(R.id.npdv_toolbar);
        tb.setTitle(R.string.title_activity_novo_pdv);
        setSupportActionBar(tb);
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
                CropImage.activity(img).setAspectRatio(1, 1).setOutputCompressQuality(70)
                        .setRequestedSize(512, 512).setMinCropResultSize(256, 256).start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                ImageView image = (ImageView) findViewById(R.id.npdv_img);
                image.setPadding(0, 0, 0, 0);
                this.image = CropImage.getActivityResult(data).getUri();
                image.setImageURI(this.image);
            }
        }
    }
}
