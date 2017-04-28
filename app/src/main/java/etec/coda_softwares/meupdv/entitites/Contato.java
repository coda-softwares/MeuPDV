package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Created by dovahkiin on 26/04/17.
 */

public class Contato {
    private static final String TAG = Contato.class.getName();
    private List<String> telefones = new ArrayList<>();
    private String email = "";
    private String id = "";

    public Contato() {}

    public Contato(String telefone, String email) {
        this(Collections.singletonList(telefone), email);
    }

    public Contato(List<String> telefones, String email) {
        this.telefones.addAll(telefones);
        this.email = email;
    }

    public List<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(ArrayList<String> telefones) {
        this.telefones = telefones;
    }

    /**
     * Por causa do firebase, o getter normal retorna o email sem caracteres especiais;
     * Para o email normal use {@link Contato#formatEmail()}
     *
     * @return O email sem caracteres especiais
     */
    public String getEmail() {
        return email.toLowerCase().replaceAll("@", "_A").replaceAll("\\.", "_P");
    }

    public void setEmail(String email) {
        if (email.contains("_A")) {
            email = email.replaceAll("_A", "@")
                    .replaceAll("_P", ".");
        }
        this.email = email;
    }

    private String formatEmail() {
        return this.email;
    }

    @Exclude
    public String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    void saveOnDB() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference a = FirebaseDatabase.getInstance().getReference().child("pdv")
                .child(TelaInicial.getCurrentPdv().getId()).child("contatos");
        if (id.equals("")) {
            id = a.push().getKey();
        }
        a.runTransaction(new HandlerPadrao() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                GenericTypeIndicator<Map<String, Contato>> tipo =
                        new GenericTypeIndicator<Map<String, Contato>>() {
                        };
                Map<String, Contato> curr = mutableData.getValue(tipo);
                Contato esse = Contato.this;

                if (curr != null) {
                    esse = curr.get(id);
                    if (esse != null) {
                        return Transaction.abort();
                    } else esse = Contato.this;
                } else curr = new HashMap<>();

                curr.put(esse.getId(), esse);
                mutableData.setValue(curr);
                TelaInicial.getCurrentPdv().getContatos().put(id, esse);
                return Transaction.success(mutableData);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contato contato = (Contato) o;

        if (telefones != null ? !telefones.equals(contato.telefones) : contato.telefones != null)
            return false;
        if (!email.equals(contato.email)) return false;
        return id.equals(contato.id);

    }

    @Override
    public int hashCode() {
        int result = telefones != null ? telefones.hashCode() : 0;
        result = 31 * result + email.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
