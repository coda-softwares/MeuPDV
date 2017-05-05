package etec.coda_softwares.meupdv;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.google.firebase.crash.FirebaseCrash;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

/**
 * Created by malzahar on 03/05/17.
 */

public class CarregarImagem extends Activity {
    private static final int REQ_FOTO_GALERIA = 2231;
    private static final int REQ_TIRAR_FOTO = 1115;
    private File tempFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_nova_img);
    }

    private void answer(Uri img) {
        Intent response = getIntent();
        response.replaceExtras(new Bundle()); // Limpar
        if (img != null) {
            response.putExtra("imagem", img);
            setResult(RESULT_OK, response);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri img = Uri.EMPTY;
            if (requestCode == REQ_TIRAR_FOTO) {
                img = Uri.fromFile(tempFoto);
            } else if (requestCode == REQ_FOTO_GALERIA) {
                img = data.getData();
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (tempFoto != null) {
                    tempFoto.delete();
                }
                answer(CropImage.getActivityResult(data).getUri());
                return;
            }
            CropImage.activity(img).setMinCropResultSize(256, 256).start(this);
        } else {
            answer(null);
        }
    }

    private void initTempfoto() {
        try {
            tempFoto = File.createTempFile("temporary", ".jpg", getExternalCacheDir());
            tempFoto.delete();
        } catch (IOException e) {
            FirebaseCrash.report(e);
            System.exit(1);
        }
    }

    public void tirarFoto(View v) {
        initTempfoto();
        Uri photoURI = Uri.fromFile(tempFoto);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQ_TIRAR_FOTO);
    }

    public void selecionarGaleria(View v) {
        initTempfoto();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_FOTO_GALERIA);
    }
}
