package etec.coda_softwares.meupdv.entitites;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dovahkiin on 26/04/17.
 */
public class PDV {
    private String name;
    private String lema;
    private List<String> integrantes = new ArrayList<>();
    private Bitmap imagem;

    public PDV() {
    }

    public PDV(String name, List<String> integrantes, Bitmap imagem, String lema) {
        this.name = name;
        this.lema = lema;
        this.integrantes = integrantes;
        this.imagem = imagem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImagem() {
        return imagem;
    }

    public String getLema() {
        return lema;
    }

    public void setLema(String lema) {
        this.lema = lema;
    }

    public void setIntegrantes(List<String> integrantes) {
        this.integrantes = integrantes;
    }

    public void setImagem(Bitmap imagem) {
        this.imagem = imagem;
    }

    public List<String> getIntegrantes() {
        return integrantes;
    }
}
