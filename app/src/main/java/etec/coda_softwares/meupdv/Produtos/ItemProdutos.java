package etec.coda_softwares.meupdv.Produtos;

import android.view.View;

/**
 * Created by jeffbustercase on 28/04/17.
 */

public class ItemProdutos {
    private int image;
    private String titulo;
    private double preco;

    public ItemProdutos(int image, String titulo, double preco) {
        this.image = image;
        this.titulo = titulo;
        this.preco = preco;
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

    public double getPreco() {return preco;}

    public void setPreco(Double preco) {this.preco = preco;}
}
