package etec.coda_softwares.meupdv.entitites;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Classe para represntar o fornecedor no bando de dados. Para salvar realmente, utilize o metodo
 * {@link PDV#addFornecedor(Fornecedor, Runnable)}
 */

public class Fornecedor implements Serializable {
    private String id = "";
    private List<String> telefones = new ArrayList<>();
    private String email = "";
    private String nome = "";
    public Fornecedor() {
    }

    public Fornecedor(String nome, String email, List<String> telefones) {
        this.telefones = telefones;
        this.email = email;
        this.nome = nome;
    }

    public List<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(ArrayList<String> telefones) {
        this.telefones = telefones;
    }

    private String formatEmail() {
        return this.email;
    }

    /**
     * Por causa do firebase, o getter normal retorna o email sem caracteres especiais;
     * Para o email normal use {@link Fornecedor#formatEmail()}
     *
     * @return O email sem caracteres especiais
     */
    public String getEmail() {
        return email.toLowerCase().replaceAll("@", "_A").replaceAll("\\.", "_P");
    }

    public void setEmail(String email) {
        if (email.contains("_A")) {
            email = email.replaceAll("_A", "@").replaceAll("_P", ".");
        }
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
        DatabaseReference self = FirebaseDatabase.getInstance().getReference("pdv")
                .child(TelaInicial.getCurrentPdv().getId()).child("fornecedores");
        if (id.equals("")) {
            self = self.push();
            id = self.push().getKey();
            self.setValue(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    callback.run();
                }
            });
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fornecedor that = (Fornecedor) o;

        if (!id.equals(that.id)) return false;
        if (telefones != null ? !telefones.equals(that.telefones) : that.telefones != null)
            return false;
        if (!email.equals(that.email)) return false;
        return nome.equals(that.nome);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (telefones != null ? telefones.hashCode() : 0);
        result = 31 * result + email.hashCode();
        result = 31 * result + nome.hashCode();
        return result;
    }
}
