package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import etec.coda_softwares.meupdv.entitites.Fornecedor;
import etec.coda_softwares.meupdv.entitites.Produto;

import static etec.coda_softwares.meupdv.Util.DateFormater;
import static etec.coda_softwares.meupdv.Util.REQ_IMG;

public class CadastrarProduto extends AppCompatActivity {
    EditText campoNome, campoQuantidade, campoValor, campoCdDBarras;
    MaterialSpinner spinnerFornecedores;
    ImageView ivFoto;
    Date validade;
    Produto old;


    private void finalizar() {
        // Checar campos
        String nomeProd = Util.lerString(campoNome).trim();
        int quantidade = (int) Util.lerDouble(campoQuantidade);
        Fornecedor f = getSelectedFornecedor();
        double preco = Util.lerDouble(campoValor);
        final String cdgBarras = Util.lerString(campoCdDBarras).trim();
        if (Util.temStringVazia(nomeProd, cdgBarras)) {
            Util.showToast(CadastrarProduto.this, "Codigo de barras ou nome nao inseridos");
            return;
        }
        if (cdgBarras.length() < 8) {
            Util.showToast(CadastrarProduto.this, "Codigo de barras muito curto");
            return;
        }
        if (preco <= 0) {
            Util.showToast(CadastrarProduto.this, "Preco tem que ser maior que zero.");
            return;
        }
        if (quantidade <= 0) {
            Util.showToast(CadastrarProduto.this, "Quantidade tem que ser maior que zero");
            return;
        }

        final Produto prod = new Produto(nomeProd.toLowerCase(), validade, preco + "", quantidade, cdgBarras, f);

        // Utilizado para verificar a existência de duplicatas
        final DatabaseReference reference = Produto.DBROOT.child(cdgBarras);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /**
                 * Tenha cuidado na hora que for mexer aqui
                 * O Hack seguinte é necessário para que na hora que o usuário estiver criando
                 * um novo produto, ele de um 'avoid' na hora de checar se foram feitas mudanças
                 * no codigo de barras(pois ele não existia).
                 */
                String comparableOld;
                if(old!=null)
                    comparableOld = old.getCodDBarras();
                else
                    comparableOld = "";

                if (dataSnapshot.exists() && !(comparableOld.equals(prod.getCodDBarras()))) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(CadastrarProduto.this);
                    builder.setMessage("Ja existe um produto com este mesmo codigo de barras, tem" +
                            " certeza que quer substitui-lo?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Substituir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reference.setValue(prod);
                            CadastrarProduto.this.endActivity();
                        }
                    });
                    /**
                     * Caso tivesse só uma opção 'substituir', seria peso demais para o usuário
                     * Mesmo não fazendo nada, significa muito para o usuário
                     */
                    builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    reference.setValue(prod);
                    CadastrarProduto.this.endActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.showToast(CadastrarProduto.this, "salvamento cancelado");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_confirma, menu);
        MenuItem item = menu.findItem(R.id.botao_confirma);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finalizar();
                return true;
            }
        });
        return true;
    }

    public void endActivity(){
        if(old!=null && !(old.getCodDBarras().equals(campoCdDBarras.getText().toString().trim())))
            Produto.DBROOT.child(old.getCodDBarras()).removeValue();
        CadastrarProduto.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produto);
        Toolbar tbar = (Toolbar) findViewById(R.id.prod_toolbar);
        setSupportActionBar(tbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        campoNome = (EditText) findViewById(R.id.prod_nome);
        campoQuantidade = (EditText) findViewById(R.id.prod_quant);
        campoValor = (EditText) findViewById(R.id.prod_valor);
        campoCdDBarras = (EditText) findViewById(R.id.prod_barras);
        spinnerFornecedores = (MaterialSpinner) findViewById(R.id.lista_fornecedores_spineer);

        // Instanciado só aqui pois é o unico lugar onde é utilizado
        EditText campoValidade = (EditText) findViewById(R.id.prod_validade);

        old = (Produto) getIntent().getSerializableExtra("produto");
        if (old != null) {
            campoNome.setText(old.getNome());
            campoValidade.setText(DateFormater.format(old.getValidade()));
            validade = old.getValidade();
            campoQuantidade.setText(old.getQuantidade() + "");
            campoValor.setText(old.getValor() + "");
            campoCdDBarras.setText(old.getCodDBarras() + "");
        }

        beautifySpinner();

        populateFornecedoresSpinner();

        setupValidade();
    }

    private void beautifySpinner() {
        spinnerFornecedores.setBackgroundColor(Color.alpha(0));
        spinnerFornecedores.getPopupWindow().getContentView().setBackgroundColor(Color.WHITE);
    }

    private void populateFornecedoresSpinner() {
        final DatabaseReference fornecedoresRef = Fornecedor.DBROOT;

        fornecedoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final List<Fornecedor> fornecedores = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Fornecedor fornecedor = snapshot.getValue(Fornecedor.class);
                    fornecedor.setImagem(snapshot.getKey());
                    fornecedores.add(fornecedor);
                }
                if (fornecedores.size() <= 0) {
                    AlertDialog.Builder erro = new AlertDialog.Builder(CadastrarProduto.this);
                    erro.setMessage("Nenhum fornecedor cadastrado!!");
                    erro.setCancelable(false);
                    erro.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(CadastrarProduto.this, CadastrarFornecedor.class);
                            startActivity(i);
                        }
                    });
                    erro.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    erro.show();
                    return;
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
     * Getter da proriedade fornecedor.
     *
     * @return Retorna o fornecedor selecionado da drop down list(Spinner)
     */
    private Fornecedor getSelectedFornecedor(){
        return  (Fornecedor)spinnerFornecedores.getItems().get(spinnerFornecedores.getSelectedIndex());
    }

    /**
     * Adiciona utilidade para selecionar a validade facilmente
     */
    private void setupValidade() {
        final EditText quant = (EditText) findViewById(R.id.prod_quant);
        final EditText data = (EditText) findViewById(R.id.prod_validade);
        data.setKeyListener(null);

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
                            data.setText(DateFormater.format(validade));
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

    public void addFoto(View v) {
        startActivityForResult(new Intent(this, CarregarImagem.class), REQ_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            IntentResult barcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (barcode == null) return;
            campoCdDBarras.setText(barcode.getContents());
        }
    }

    public void carregarCodBarras(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setBarcodeImageEnabled(true);
        integrator.setPrompt("Escaneie o código de barras do produto");
        integrator.setBeepEnabled(true);
        integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
    }

}