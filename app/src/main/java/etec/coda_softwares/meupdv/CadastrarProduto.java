package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CadastrarProduto extends AppCompatActivity {
    public static final int REQ_IMG = 1547;
    Date validade;
    EditText tb_barras;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_confirma, menu);
        //TODO: Botão confirma.
        return true;
    }

    // id_Prod, id_Forn, nome_Prod, validade_Prod, cdgBarras_Prod, val_Prod, quant_Prod
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produto);
        tb_barras = (EditText) findViewById(R.id.prod_barras);
        Toolbar tbar = (Toolbar) findViewById(R.id.prod_toolbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupValidade();
    }

    private void setupValidade() {
        final EditText quant = (EditText) findViewById(R.id.prod_quant);
        final EditText data = (EditText) findViewById(R.id.prod_validade);
        data.setKeyListener(null);

        final DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        data.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != MotionEvent.ACTION_UP) return false;
                final DatePicker dpicker = new DatePicker(v.getContext());
                AlertDialog.Builder builder = new DatePickerDialog.Builder(CadastrarProduto.this)
                        .setTitle("Data de vencimento")
                        .setView(dpicker);
                Dialog d = builder.create();
                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Calendar c = Calendar.getInstance();
                        c.set(dpicker.getYear(), dpicker.getMonth(), dpicker.getDayOfMonth());
                        if (c.getTime().after(new Date())) {
                            validade = c.getTime();
                            data.setText(dateFormat.format(validade));
                            quant.requestFocusFromTouch();
                        } else {
                            Toast.makeText(dpicker.getContext(), "Data precisa ser depois de hoje",
                                    Toast.LENGTH_LONG).show();
                            data.requestFocus(View.FOCUS_UP);
                        }
                    }
                });
                d.show();
                return true;
            }
        });
        data.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) quant.requestFocus();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_IMG) {
                ImageView img = (ImageView) findViewById(R.id.prod_image);
                img.setImageURI((Uri) data.getParcelableExtra("imagem"));
            }
            IntentResult barcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (barcode == null) return;
            tb_barras.setText(barcode.getContents());
        }
    }

    public void novaImgProduto(View v) {
        startActivityForResult(new Intent(this, CarregarImagem.class), REQ_IMG);
    }

    public void carregarCodBarras(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
    }

}