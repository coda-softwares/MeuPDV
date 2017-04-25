package etec.coda_softwares.meupdv;

/**
 * Created by dovahkiin on 24/04/17.
 */

public class Usuario {
    private transient String nome;
    private transient String macToken;
    private String username;
    private String password;

    public Usuario() {
    }

    public Usuario(String nome, String username, String password, String macToken) {
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.macToken = macToken;
    }
}
