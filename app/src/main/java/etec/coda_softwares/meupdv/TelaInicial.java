package etec.coda_softwares.meupdv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import etec.coda_softwares.meupdv.entitites.PDV;
import etec.coda_softwares.meupdv.menuPrincipal.MenuPrincipal;

public class TelaInicial extends AppCompatActivity {
    private static final int REQ_CODE = 742;
    private static PDV CURRENT_PDV;
    private final AtomicReference<PDV> databasePDV = new AtomicReference<>();

    public static PDV getCurrentPdv() {
        return CURRENT_PDV;
    }

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
            //Muito muito provavelmente já faz parte de um PDV
            populateList();
        }
    }

    private void populateList(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final DatabaseReference ds;

        final ValueEventListener pdvLoader = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PDV a = dataSnapshot.getValue(PDV.class);
                if (a == null) {
                    Log.wtf(getClass().getName(), "PDV is set and leads to nothing!");
                    FirebaseCrash.log("PDV is set and leads to nothing!");
                    System.exit(1);
                }
                //TODO: Atualizar ImageView.
                a.initId(dataSnapshot.getKey());
                CURRENT_PDV = a;
                nextActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
                System.exit(0);
            }
        };

        ds = FirebaseDatabase.getInstance().getReference()
                .child("user").child(user.getUid()).child("pdv");
        ds.addListenerForSingleValueEvent(new ValueEventListener() { //Async databases be like
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String local = dataSnapshot.getValue(String.class);
                if (local == null) {
                    Intent i = new Intent(TelaInicial.this, NovoPDV.class);
                    startActivityForResult(i, 200);
                    return;
                }
                ds.getRoot().child("pdv").child(local).addListenerForSingleValueEvent(pdvLoader);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
                System.exit(0);
            }
        });
    }

    private void nextActivity(){
        Intent i = new Intent(TelaInicial.this, MenuPrincipal.class); //FIXME: CHANGE BACK!
        i.putExtra("pdv", PDV.class);
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
                    Toast.makeText(this, "Sem internet disponivel.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (res.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Erro desconhecido", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == 200) { // Retorno da atividade de novo PDV TODO: constante int
            PDV res = (PDV) data.getExtras().get("pdv");
            if (res != null) {
                TelaInicial.CURRENT_PDV = res;
                nextActivity();
            }
        }
    }

//    private static void setPDV(PDV PDV) {
//        TelaInicial.PDV = PDV;
//    }
}
