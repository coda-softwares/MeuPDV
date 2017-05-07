package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Created by jeffbustercase on 06/05/17.
 */

public class Produto implements Serializable {
    public static final DatabaseReference DBROOT = FirebaseDatabase.getInstance()
            .getReference("pdv").child(TelaInicial.getCurrentPdv().getId()).child("produtos");

    // Não utilizar imagem ainda
    private String nome = "";
    private double valor = 0;
    private int quantidade = 0;
    private int codDBarras = 0;

    public Produto(){}
    public Produto(String nome, double valor, int quantidade, int codDBarras){
        this.nome = nome;
        this.valor = valor;
        this.quantidade = quantidade;
        this.codDBarras = codDBarras;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Produto produto = (Produto) o;

        if (Double.compare(produto.valor, valor) != 0) return false;
        if (quantidade != produto.quantidade) return false;
        if (codDBarras != produto.codDBarras) return false;
        return nome != null ? nome.equals(produto.nome) : produto.nome == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = nome != null ? nome.hashCode() : 0;
        temp = Double.doubleToLongBits(valor);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + quantidade;
        result = 31 * result + codDBarras;
        return result;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getCodDBarras() {
        return codDBarras;
    }

    public void setCodDBarras(int codDBarras) {
        this.codDBarras = codDBarras;
    }
}
