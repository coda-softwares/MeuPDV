package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minotaur on 30/05/17.
 */

public class Usuario {
    private String pdv;
    private String nome;
    private String email;
    private List<Permissao> permissoes;

    public Usuario() {
        this("", "", "", new ArrayList<Permissao>());
    }

    public Usuario(String pdv, String nome, String email, List<Permissao> permissoes) {
        this.pdv = pdv;
        this.nome = nome;
        this.permissoes = permissoes;
        this.email = email;
    }

    static public DatabaseReference getRoot() {
        return FirebaseDatabase.getInstance().getReference().child("user");
    }

    public String getPdv() {
        return pdv;
    }

    public void setPdv(String pdv) {
        this.pdv = pdv;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Permissao> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(List<Permissao> permissoes) {
        this.permissoes = permissoes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        return pdv.equals(usuario.pdv)
                && nome.equals(usuario.nome)
                && email.equals(usuario.email)
                && permissoes.equals(usuario.permissoes);

    }

    @Override
    public int hashCode() {
        int result = pdv.hashCode();
        result = 31 * result + nome.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + permissoes.hashCode();
        return result;
    }
}
