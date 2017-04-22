package etec.coda_softwares.meupdv;

/**
 * Created by dovahkiin on 22/04/17.
 */

public class ItemMenuPrincipal {
    private int image;
    private String titulo;
    private String descricao;
    private Runnable action;

    public ItemMenuPrincipal(int image, String titulo, String descricao, Runnable action) {
        this.image = image;
        this.titulo = titulo;
        this.descricao = descricao;
        this.action = action;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
}
