package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import etec.coda_softwares.meupdv.CadastrarFornecedor;
import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Classe para represntar o fornecedor no bando de dados.
 */

public class Fornecedor implements Serializable {
    public static final DatabaseReference DBROOT = FirebaseDatabase.getInstance()
            .getReference("pdv").child(TelaInicial.getCurrentPdv().getId()).child("fornecedores");
    private List<String> telefones = new ArrayList<>();
    private String email = "";
    private String nome = "";
    private String imagem = "";
    private long ultimaModificacao;

    public Fornecedor() {
        ultimaModificacao = System.currentTimeMillis();
    }

    public Fornecedor(String nome, String email, List<String> telefones) {
        ultimaModificacao = System.currentTimeMillis();
        setTelefones(telefones);
        this.email = email;
        this.nome = nome;
    }

    @Exclude
    public String getId() {
        String[] path = imagem.split("/");
        return path[path.length - 1].replace(".jpg", "");
    }

    @Exclude
    public void setId(String id) {
        if (!getId().equals("") && !imagem.equals(""))
            return;
        imagem = CadastrarFornecedor.NO_IMG + "/" + id;
    }

    /**
     * Metodo utilizado para abilitar o acesso ao nome atraves de uma lista
     * @return Retorna o nome
     */
    @Override
    public String toString() {
        return this.nome;
    }

    public List<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<String> telefones) {
        ArrayList<String> formatted = new ArrayList<>(telefones.size());
        for (String tel : telefones) {
            formatted.add(tel.replaceAll("\\(|-|\\)| |\t|\\+", ""));
        }
        this.telefones = formatted;
    }

    public String formatEmail() {
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

    public boolean hasImagem() {
        return !imagem.contains(CadastrarFornecedor.NO_IMG) && !imagem.equals("");
    }

    public String getImagem() {
        if (!hasImagem())
            return "";
        else
            return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public long getUltimaModificacao() {
        return ultimaModificacao;
    }

    public void setUltimaModificacao(long ultimaModificacao) {
        this.ultimaModificacao = ultimaModificacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fornecedor that = (Fornecedor) o;

        if (telefones != null ? !telefones.equals(that.telefones) : that.telefones != null)
            return false;
        if (!email.equals(that.email)) return false;
        if (!nome.equals(that.nome)) return false;
        return imagem.equals(that.imagem);

    }

    @Override
    public int hashCode() {
        int result = (telefones != null ? telefones.hashCode() : 0);
        result = 31 * result + email.hashCode();
        result = 31 * result + nome.hashCode();
        result = 31 * result + imagem.hashCode();
        return result;
    }
}
