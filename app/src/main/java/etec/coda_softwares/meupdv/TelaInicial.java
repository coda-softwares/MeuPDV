package etec.coda_softwares.meupdv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import etec.coda_softwares.meupdv.entitites.PDV;
import etec.coda_softwares.meupdv.menuPrincipal.MenuPrincipal;

public class TelaInicial extends AppCompatActivity {
    public static final int REQ_NOVO_PDV = 200;
    private static final int REQ_LOGIN = 742;
    private static PDV CURRENT_PDV;
    // Propriedade de controle pro watcher da databse se essa atividade ainda esta viva
    private static boolean LOADING = true;

    /**
     * Forma principal e global de conseguiur uma instancia do PDV atual.
     *
     * @return o PDV atual.
     */
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
            aui.setTheme(R.style.AppTheme_NoBar);
            aui.setAllowNewEmailAccounts(true);
            aui.setProviders(Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()));
            startActivityForResult(aui.build(), REQ_LOGIN);
        } else {
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
                    Log.wtf(getClass().getName(), "PDV definido leva a lugar nenhum");
                    FirebaseCrash.log("PDV definido leva a lugar nenhum");
                    System.exit(1);
                }
                //TODO: Atualizar ImageView.
                CURRENT_PDV = a;
                if (LOADING) {
                    LOADING = false;
                    a.initId(dataSnapshot.getKey());
                    nextActivity();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
                System.exit(0);
            }
        };

        // acessa o node do usuario na database identificado pelo o seu UID
        ds = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid())
                .child("pdv");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Primeiro carregamos a key do PDV do usuario:
                String local = dataSnapshot.getValue(String.class);
                //Se a key nao existir:
                if (local == null) {
                    Intent i = new Intent(TelaInicial.this, NovoPDV.class);
                    startActivityForResult(i, REQ_NOVO_PDV);
                    return;
                }
                //Se ja existir carrega o PDV e fica de olho por alterações futuras também!
                ds.getRoot().child("pdv").child(local).addValueEventListener(pdvLoader);
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
        if (requestCode == REQ_LOGIN) {
            IdpResponse res = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                populateList();
                return;
            } else if (res != null) {
                if (res.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Não foi possivel se conectar a internet",
                            Toast.LENGTH_LONG).show();
                }
                if (res.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, "Erro desconhecido", Toast.LENGTH_LONG).show();
                }
                FirebaseCrash.log("Erro de login! " + res.getErrorCode());
            } else {
                Toast.makeText(this, "Login cancelado.", Toast.LENGTH_SHORT).show();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(1); // Sai se o login falha.
                }
            }, 2000);
        } else if (requestCode == REQ_NOVO_PDV) { // Retorno da atividade de novo PDV
            PDV res = (PDV) data.getExtras().get("pdv");
            if (res != null) {
                TelaInicial.CURRENT_PDV = res;
                CURRENT_PDV.saveOnDB();
                nextActivity();
            }
        }
    }

//    private static void setPDV(PDV PDV) {
//        TelaInicial.PDV = PDV;
//    }
}
