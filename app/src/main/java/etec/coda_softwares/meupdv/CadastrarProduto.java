package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.Fornecedor;
import etec.coda_softwares.meupdv.entitites.Produto;

public class CadastrarProduto extends AppCompatActivity {
    public static final int REQ_IMG = 1547;
    Date validade;
    MaterialSpinner spinnerFornecedores;
    EditText campoNome, campoQuantidade, campoValor, campoCdDBarras;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_confirma, menu);
        MenuItem item = menu.findItem(R.id.botao_confirma);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Checar campos
                String nomeProd = Util.lerString(campoNome).trim();
                int quantidade = (int) Util.lerDouble(campoQuantidade);
                Fornecedor f = getSelectedFornecedor();
                double preco = Util.lerDouble(campoValor);
                String cdgBarras = Util.lerString(campoCdDBarras).trim();
                Util.verificarStringsVazias(nomeProd, cdgBarras);

                if (cdgBarras.length() < 8) {
                    Util.showToast(CadastrarProduto.this, "Codigo de barras muito curto");
                    return true;
                }
                if (preco <= 0) {
                    return true;
                }
                if (quantidade <= 0) {
                    Util.showToast(CadastrarProduto.this, "Quantidade tem que ser maior que zero");
                    return true;
                }

                Produto prod = new Produto(nomeProd, preco, quantidade, cdgBarras);
                DatabaseReference referencia = Produto.DBROOT.child(cdgBarras + "");

                referencia.setValue(prod);
                // salvar no banco
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produto);
        Toolbar tbar = (Toolbar) findViewById(R.id.prod_toolbar);
        setSupportActionBar(tbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campoNome = (EditText) findViewById(R.id.prod_nome);
        campoQuantidade = (EditText) findViewById(R.id.prod_quant);
        campoValor = (EditText) findViewById(R.id.prod_preco);
        campoCdDBarras = (EditText) findViewById(R.id.prod_barras);
        spinnerFornecedores = (MaterialSpinner) findViewById(R.id.lista_fornecedores_spineer);

        beautifySpinner();

        populateFornecedoresSpinner();

        setupValidade();
    }

    private void beautifySpinner() {
        spinnerFornecedores.setBackgroundColor(Color.alpha(0));
        spinnerFornecedores.getPopupWindow().getContentView().setBackgroundColor(Color.WHITE);
    }

    private void populateFornecedoresSpinner() {

        DatabaseReference fornecedoresRef = Fornecedor.DBROOT;

        /**
         *  That's the trick! ;)
         */
        fornecedoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<Fornecedor> fornecedores = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Fornecedor fornecedor = snapshot.getValue(Fornecedor.class);
                    fornecedor.setImagem(snapshot.getKey());
                    fornecedores.add(fornecedor);
                }

                spinnerFornecedores.setItems(fornecedores);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
    }
    /**
     *
     * @return Retorna o fornecedor selecionado da drop down list(Spinner)
     */
    private Fornecedor getSelectedFornecedor(){
        return  (Fornecedor)spinnerFornecedores.getItems().get(spinnerFornecedores.getSelectedIndex());
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
            campoCdDBarras.setText(barcode.getContents());
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