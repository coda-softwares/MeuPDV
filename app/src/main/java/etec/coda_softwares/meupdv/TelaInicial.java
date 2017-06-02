package etec.coda_softwares.meupdv;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
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
import java.util.ArrayList;
import java.util.Arrays;

import etec.coda_softwares.meupdv.entitites.PDV;
import etec.coda_softwares.meupdv.entitites.Permissao;
import etec.coda_softwares.meupdv.entitites.Usuario;
import etec.coda_softwares.meupdv.menuPrincipal.MenuPrincipal;

public class TelaInicial extends AppCompatActivity {
    public static final int REQ_NOVO_PDV = 200;
    private static final int REQ_LOGIN = 742;
    public static PDV CURRENT_PDV;
    private static File internalFiles;
    // Propriedade de controle pro watcher da databse se essa atividade ainda esta viva

    public static void getFile(String firebaseURL, final UriCallback callback) {
        if (firebaseURL != null) {
            if (firebaseURL.equals(""))
                return;
        } else
            return;
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
        if (firebaseURL.equals("")) return;
        StorageReference file = FirebaseStorage.getInstance().getReferenceFromUrl(firebaseURL);
        final File locFile =
                new File(internalFiles.getAbsolutePath() + File.separator + file.getName());
        if (locFile.exists())
            locFile.delete();
        if (!onlyLocal)
            file.delete();
    }

    private static void populateList(final TelaInicial self){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                a.setId(dataSnapshot.getKey());
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
        ds = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Primeiro carregamos a key do PDV do usuario:
                Usuario u = dataSnapshot.getValue(Usuario.class);
                if (u == null) {
                    u = new Usuario("", user.getDisplayName(), user.getEmail(),
                            new ArrayList<Permissao>());
                    ds.setValue(u);
                }
                String local = u.getPdv();
                //Se a key nao existir:
                if (local.trim().equals("")) {
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
        final ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            retryDialog("Sem conexao com a internet, tentar novamente?", new Runnable() {
                @Override
                public void run() {
                    NetworkInfo activeNetwork1 = cm.getActiveNetworkInfo();
                    boolean isConnected1 = activeNetwork1 != null &&
                            activeNetwork1.isConnectedOrConnecting();
                    if (!isConnected1) {
                        retryDialog("Sem conexao com a internet, tentar novamente?", this);
                    } else {
                        tryLogin();
                    }
                }
            });
        } else {
            tryLogin();
        }
    }

    private void tryLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            AuthUI.SignInIntentBuilder aui = AuthUI.getInstance().createSignInIntentBuilder();
            aui.setLogo(R.drawable.ic_meupdv);
            aui.setTheme(R.style.TemaPadrao);
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
        Intent i = new Intent(TelaInicial.this, MenuPrincipal.class);
        i.putExtra("pdv", PDV.class);
        startActivity(i);
        finish();
    }

    private Dialog retryDialog(final Runnable r) {
        return retryDialog("Erro no login, tentar novamente?", r);
    }

    private Dialog retryDialog(String msg, final Runnable r) {
        AlertDialog.Builder retry = new AlertDialog.Builder(this);
        retry.setCancelable(false);
        retry.setMessage(msg);
        retry.setPositiveButton(android.R.string.yes, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (r != null)
                    r.run();
            }
        });
        retry.setNegativeButton(android.R.string.no, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });
        return retry.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == REQ_LOGIN) {
            if (resultCode == RESULT_OK) {
                populateList(this);
            } else {
                retryDialog(new Runnable() {
                    @Override
                    public void run() {
                        tryLogin();
                    }
                });
            }

        } else if (requestCode == REQ_NOVO_PDV) { // Retorno da atividade de novo PDV
            PDV res = (PDV) data.getExtras().get("pdv");
            if (res != null) {
                TelaInicial.CURRENT_PDV = res;
                CURRENT_PDV.saveOnDB(new Runnable() {
                    @Override
                    public void run() {
                        nextActivity();
                        finish();
                    }
                });
            }
        }
    }

    public abstract static class UriCallback {
        public abstract void done(Uri u);
    }
}
