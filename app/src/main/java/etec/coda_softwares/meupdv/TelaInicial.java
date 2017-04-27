package etec.coda_softwares.meupdv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import etec.coda_softwares.meupdv.entitites.Contato;
import etec.coda_softwares.meupdv.entitites.Fornecedor;
import etec.coda_softwares.meupdv.entitites.PDV;
import etec.coda_softwares.meupdv.menuPrincipal.MenuPrincipal;

public class TelaInicial extends AppCompatActivity {
    private static final int REQ_CODE = 742;
    private static String PDV_ID = "";
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        tryLogin();
    }

    private void tryLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            AuthUI.SignInIntentBuilder aui = AuthUI.getInstance().createSignInIntentBuilder();
            aui.setLogo(R.drawable.ic_meupdv);
            aui.setTheme(R.style.AppTheme);
            aui.setAllowNewEmailAccounts(true);
            aui.setProviders(Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()));
            startActivityForResult(aui.build(), REQ_CODE);
        } else {
            populateList();
        }
    }

    private void populatePDVView(View v, PDV model) {
        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TelaInicial.this.nextActivity();
            }
        });
        TextView titulo = (TextView) v.findViewById(R.id.pdv_Title);
        titulo.setText(model.getName());
        TextView subT = (TextView) v.findViewById(R.id.pdv_Data);
        subT.setText(model.getLema());
        RoundImageView rdv = (RoundImageView) v.findViewById(R.id.pdv_img);
        rdv.setImageResource(R.drawable.ic_meupdv);
    }

    private void populateList(){

        Contato a = new Contato(new ArrayList<>(Arrays.asList("1112345678", "1111234567")),
                "samosaara@gmail.com");
        Fornecedor awesome = new Fornecedor("Samuel", "http://github.com/coda_softwares", a);
        awesome.saveOnDB();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference(user.getUid())
                .child("pdv").child("own");

        Query query = ds.limitToFirst(3);
        final FirebaseListAdapter<PDV> ownPDVList = new FirebaseListAdapter<PDV>(this, PDV.class,
                R.layout.pdv_item, query) {
            @Override
            protected void populateView(View v, PDV model, int position) {
                populatePDVView(v, model);

            }
        };
        Query qr2 = ds.getParent().child("shared").startAt(0);
        ((ListView) findViewById(R.id.inicio_own_pdv)).setAdapter(ownPDVList);
        FirebaseListAdapter<PDV> sharedPDVAdpater = new FirebaseListAdapter<PDV>(this, PDV.class,
                R.layout.pdv_item, query) {
            @Override
            protected void populateView(View v, PDV model, int position) {
                populatePDVView(v, model);
            }
        };
        ((ListView) findViewById(R.id.inicio_shared_pdv)).setAdapter(sharedPDVAdpater);
        nextActivity();

    }

    private void nextActivity(){
        Intent i = new Intent(TelaInicial.this, MenuPrincipal.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQ_CODE == requestCode) {
            IdpResponse res = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                populateList();
            } else {
                if (res == null) {
                    Toast.makeText(this, "Login cancelado.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (res.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Login cancelado.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (res.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Erro desconhecido", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static String getPdvId() {
        return PDV_ID;
    }

    private static void setPdvId(String pdvId) {
        PDV_ID = pdvId;
    }
}
