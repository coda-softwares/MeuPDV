package etec.coda_softwares.meupdv.entitites;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dovahkiin on 26/04/17.
 */
public class PDV implements Serializable {
    public static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private String nome;
    private String lema;
    private String id = "";
    private List<String> integrantes = new ArrayList<>();
    private String imagemURL; //TODO: Configurar FIrebaseStorage e fazer um setter com bitmap

    public PDV() {
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

    @Exclude
    public String getId() {
        return id;
    }

    public void saveOnDB() {
        DatabaseReference userPDV = root
                .child("pdv");
        if (id.equals("")) {
            userPDV = userPDV.push();
            id = userPDV.getKey();
        } else {
            userPDV.child(id);
        }
        userPDV.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData data) {
                PDV saved = data.getValue(PDV.class);
                if (saved != null) {
                    if (saved.equals(PDV.this))
                        return Transaction.abort();
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                saved = PDV.this;
                data.setValue(saved);
                root.child("user").child(user.getUid()).child("pdv").setValue(saved.getId());
                return Transaction.success(data);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    FirebaseCrash.report(databaseError.toException());
                }
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PDV pdv = (PDV) o;

        if (!nome.equals(pdv.nome)) return false;
        if (lema != null ? !lema.equals(pdv.lema) : pdv.lema != null) return false;
        if (!id.equals(pdv.id)) return false;
        if (!integrantes.equals(pdv.integrantes)) return false;
        return imagemURL != null ? imagemURL.equals(pdv.imagemURL) : pdv.imagemURL == null;
    }
}
