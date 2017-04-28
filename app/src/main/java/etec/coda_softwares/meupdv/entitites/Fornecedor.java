package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;

import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Created by dovahkiin on 26/04/17.
 */

public class Fornecedor {
    private static final String TAG = Fornecedor.class.getName();
    private String id = "";
    private String nome;
    private Contato contato;

    public Fornecedor() {
    }

    public Fornecedor(String nome, Contato contato) {
        this.nome = nome;
        this.contato = contato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Exclude
    public Contato getContato() {
        return contato;
    }

    @Exclude
    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    void saveOnDB(final Runnable callback) {
        FirebaseUser info = FirebaseAuth.getInstance().getCurrentUser();
        assert info != null;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference self = firebaseDatabase.getReference().child("pdv")
                .child(TelaInicial.getCurrentPdv().getId()).child("fornecedores");
        if (id.equals("")) {
            id = self.push().getKey();
        }
        self.runTransaction(new HandlerPadrao() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                GenericTypeIndicator<Map<String, Fornecedor>> tipo =
                        new GenericTypeIndicator<Map<String, Fornecedor>>() {
                        };
                Map<String, Fornecedor> curr = mutableData.getValue(tipo);
                Fornecedor esse = Fornecedor.this;

                if (curr != null) {
                    esse = curr.get(esse.getId());
                    if (esse != null) {
                        return Transaction.abort();
                    } else esse = Fornecedor.this;
                } else curr = new HashMap<>();

                curr.put(id, esse);
                mutableData.setValue(curr);
                if (esse.contato != null) {
                    contato.saveOnDB();
                    mutableData.child(id).child("contato").setValue(esse.contato.getId());
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError dbError, boolean b, DataSnapshot dataSnapshot) {
                super.onComplete(dbError, b, dataSnapshot);
                if (callback != null) {
                    callback.run();
                }
            }
        });


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fornecedor that = (Fornecedor) o;

        if (!id.equals(that.id)) return false;
        if (nome != null ? !nome.equals(that.nome) : that.nome != null) return false;
        return contato != null ? contato.equals(that.contato) : that.contato == null;

    }
}
