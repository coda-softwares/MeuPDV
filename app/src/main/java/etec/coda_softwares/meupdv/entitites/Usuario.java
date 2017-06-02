package etec.coda_softwares.meupdv.entitites;

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
}
