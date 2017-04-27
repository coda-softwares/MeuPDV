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

import java.util.ArrayList;

import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Created by dovahkiin on 26/04/17.
 */

public class Contato {
    private static final String TAG = Contato.class.getName();
    private ArrayList<String> telefones = new ArrayList<>();
    private String email = "";

    public Contato() {}

    public Contato(ArrayList<String> telefones, String email) {
        this.telefones = telefones;
        this.email = email;
    }

    public ArrayList<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(ArrayList<String> telefones) {
        this.telefones = telefones;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    public void saveOnDB() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String email = this.email.replaceAll("@", "-").replaceAll("\\.", "_");
        DatabaseReference a = FirebaseDatabase.getInstance().getReference().child("pdv")
                .child(TelaInicial.getCurrentPdv().getId()).child("contato").child(email);
        a.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Contato inPlc = mutableData.getValue(Contato.class);
                if (inPlc != null)
                    if (inPlc.equals(Contato.this)) {
                        return Transaction.abort();
                    }
                mutableData.setValue(Contato.this);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError dbError, boolean ok, DataSnapshot dataSnapshot) {
                if (dbError != null) {
                    Log.e(TAG, "onComplete: couldn't write to DB", dbError.toException());
                }
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contato)) return false;
        Contato other = (Contato) obj;
        return email.equals(other.getEmail()) && telefones.equals(other.getTelefones());
    }
}
