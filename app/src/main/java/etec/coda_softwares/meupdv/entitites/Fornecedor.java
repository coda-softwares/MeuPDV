package etec.coda_softwares.meupdv.entitites;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

/**
 * Created by dovahkiin on 26/04/17.
 */

public class Fornecedor {
    private static final String TAG = Contato.class.getName();
    private String id = "";
    private String nome;
    private String endereco;
    private Contato contato;

    public Fornecedor() {
    }

    public Fornecedor(String nome, String endereco, Contato contato) {
        this.nome = nome;
        this.endereco = endereco;
        this.contato = contato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Exclude
    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void saveOnDB(PDV pdv) {
        FirebaseUser info = FirebaseAuth.getInstance().getCurrentUser();
        if (info == null) {
            Log.wtf(TAG, "User is null, WTF???");
            return;
        }

        contato.saveOnDB();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference self = firebaseDatabase.getReference(pdv.getId()).child("fornecedor");

        if (id.equals("")) {
            self = self.push();
            id = self.getKey();
        } else {
            self = self.child(id);
        }
        self.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(Fornecedor.this);
                mutableData.child("contato").setValue(contato.getEmail());
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null){
                    Log.d(TAG, "onComplete() returned: " + b + " and id " + dataSnapshot.toString());
                } else {
                    Log.wtf(TAG, "onComplete the error was " + databaseError.toString());
                }
            }
        });
    }
}
