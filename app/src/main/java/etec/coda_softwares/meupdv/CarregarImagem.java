package etec.coda_softwares.meupdv;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.theartofdev.edmodo.cropper.CropImage;

/**
 * Created by malzahar on 03/05/17.
 */

public class CarregarImagem extends Activity {
    private static final int REQUEST_FOTO = 2231;
    private long b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = System.currentTimeMillis();
        Log.i(getClass().getName(), "FIRED");
        startActivityForResult(CropImage.getPickImageChooserIntent(this), REQUEST_FOTO);
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
        b -= System.currentTimeMillis();
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
                answer(CropImage.getActivityResult(data).getUri());
            }
        } else {
            answer(null);
        }
    }
}
