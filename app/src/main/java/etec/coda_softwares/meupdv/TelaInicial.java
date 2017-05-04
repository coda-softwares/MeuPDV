package etec.coda_softwares.meupdv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Arrays;

import etec.coda_softwares.meupdv.entitites.PDV;
import etec.coda_softwares.meupdv.menuPrincipal.MenuPrincipal;

public class TelaInicial extends AppCompatActivity {
    public static final int REQ_NOVO_PDV = 200;
    private static final int REQ_LOGIN = 742;
    private static File internalFiles;
    private static PDV CURRENT_PDV;
    // Propriedade de controle pro watcher da databse se essa atividade ainda esta viva

    /**
     * Forma principal e global de conseguiur uma instancia do PDV atual.
     *
     * @return o PDV atual.
     */
    public static PDV getCurrentPdv() {
        if (CURRENT_PDV == null) {
            FirebaseCrash.log("CurrentPDV era nulo");
            System.exit(1);
        }
        return CURRENT_PDV;
    }

    public static void getFile(String firebaseURL, final UriCallback callback) {
        if (firebaseURL != null) {
            if (firebaseURL.equals(""))
                return;
        } else return;
        StorageReference file = FirebaseStorage.getInstance().getReferenceFromUrl(firebaseURL);
        final File locFile =
                new File(internalFiles.getAbsolutePath() + File.separator + file.getName());
        if (locFile.exists()) {
            callback.done(Uri.fromFile(locFile));
        } else {
            file.getFile(locFile).addOnCompleteListener(
                    new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                callback.done(Uri.fromFile(locFile));
                            } else {
                                FirebaseCrash.report(task.getException());
                            }
                        }
                    });
        }
    }

    public static void eraseFile(String firebaseURL) {
        eraseFile(firebaseURL, false);
    }

    public static void eraseFile(String firebaseURL, boolean onlyLocal) {
        StorageReference file = FirebaseStorage.getInstance().getReferenceFromUrl(firebaseURL);
        final File locFile =
                new File(internalFiles.getAbsolutePath() + File.separator + file.getName());
        if (locFile.exists())
            locFile.delete();
        if (!onlyLocal)
            file.delete();
    }

    private static void populateList(final TelaInicial self){
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
                a.initId(dataSnapshot.getKey());
                CURRENT_PDV = a;
                if (self != null) {
                    self.nextActivity();
                    self.finish();
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
                    Intent i = new Intent(self, NovoPDV.class);
                    self.startActivityForResult(i, REQ_NOVO_PDV);
                    return;
                }
                //Se ja existir carrega o PDV e fica de olho por alterações futuras também!
                ds.getRoot().child("pdv").child(local).addListenerForSingleValueEvent(pdvLoader);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
                System.exit(0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        internalFiles = getFilesDir();
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
            populateList(this);
        }
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
                populateList(this);
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
                finish();
            }
        }
    }

    public abstract static class UriCallback {
        abstract void done(Uri u);
    }
}
