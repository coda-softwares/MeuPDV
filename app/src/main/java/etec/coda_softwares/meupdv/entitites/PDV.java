package etec.coda_softwares.meupdv.entitites;

import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dovahkiin on 26/04/17.
 */
public class PDV implements Serializable {
    public static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private String nome = "";
    private String lema = "";
    private String id = "";
    private List<String> integrantes = new ArrayList<>();
    private Map<String, Contato> contatos = new HashMap<>();
    private Map<String, Fornecedor> fornecedores = new HashMap<>();
    private String imagemURL = ""; //TODO: Configurar FIrebaseStorage e fazer um setter com bitmap

    public PDV() {
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                if (contatos != null) {
                    for (String id : contatos.keySet()) {
                        Contato c = contatos.get(id);
                        c.setId(id);
                    }
                }
                if (fornecedores != null) {
                    for (String id : fornecedores.keySet()) {
                        Fornecedor c = fornecedores.get(id);
                        c.setId(id);
                    }
                }
            }
        }, 100);

    }

    public PDV(String nome, List<String> integrantes, String imagemURL, String lema) {
        this.nome = nome;
        this.lema = lema;
        this.integrantes = integrantes;
        this.imagemURL = imagemURL;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImagemURL() {
        return imagemURL;
    }

    public void setImagemURL(String imagemURL) {
        this.imagemURL = imagemURL;
    }

    public String getLema() {
        return lema;
    }

    public void setLema(String lema) {
        this.lema = lema;
    }

    public List<String> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<String> integrantes) {
        this.integrantes = integrantes;
    }

    Map<String, Contato> getContatos() {
        return contatos;
    }

    Map<String, Fornecedor> getFornecedores() {
        return fornecedores;
    }

    public void addFornecedor(Fornecedor c, Runnable r) {
        Fornecedor backup = fornecedores.put(c.getId(), c);
        if (backup == null) {
            c.saveOnDB(r);
        } else {
            FirebaseCrash.log("Adicionando fornecedor já presente");
            fornecedores.put(backup.getId(), backup);
        }
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void initId(String id) {
        if (this.id.equals("")) this.id = id;
    }

    public void saveOnDB() {
        final DatabaseReference userPDV = root.child("pdv");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        final HandlerPadrao save = new HandlerPadrao() {
            @Override
            public Transaction.Result doTransaction(MutableData data) {
                PDV saved = data.getValue(PDV.class);
                if (saved != null) {
                    if (saved.equals(PDV.this))
                        return Transaction.abort();
                }
                saved = PDV.this;
                data.setValue(saved);
                root.child("user").child(user.getUid()).child("pdv").setValue(saved.getId());
                return Transaction.success(data);
            }
        };

        if (id.equals("")) {
            root.child("user").child(user.getUid()).child("pdv").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String usrid = (String) dataSnapshot.getValue();
                            if (usrid != null) {
                                if (!usrid.equals(""))
                                    return;
                            }
                            DatabaseReference dfr = userPDV.push();
                            initId(dfr.getKey());
                            dfr.runTransaction(save);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            FirebaseCrash.report(databaseError.toException());
                            System.exit(1);
                        }
                    });
        } else {
            userPDV.child(id).runTransaction(save);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PDV pdv = (PDV) o;

        if (!nome.equals(pdv.nome)) return false;
        if (!lema.equals(pdv.lema)) return false;
        if (!id.equals(pdv.id)) return false;
        if (integrantes != null ? !integrantes.equals(pdv.integrantes) : pdv.integrantes != null)
            return false;
        if (contatos != null ? !contatos.equals(pdv.contatos) : pdv.contatos != null) return false;
        if (fornecedores != null ? !fornecedores.equals(pdv.fornecedores) : pdv.fornecedores != null)
            return false;
        return imagemURL.equals(pdv.imagemURL);

    }

    @Override
    public int hashCode() {
        int result = nome.hashCode();
        result = 31 * result + lema.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (integrantes != null ? integrantes.hashCode() : 0);
        result = 31 * result + (contatos != null ? contatos.hashCode() : 0);
        result = 31 * result + (fornecedores != null ? fornecedores.hashCode() : 0);
        result = 31 * result + imagemURL.hashCode();
        return result;
    }
}
