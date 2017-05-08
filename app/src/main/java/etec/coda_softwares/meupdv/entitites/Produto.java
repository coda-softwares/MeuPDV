package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.math.BigDecimal;

import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Created by jeffbustercase on 06/05/17.
 */

public class Produto implements Serializable {
    public static final DatabaseReference DBROOT = FirebaseDatabase.getInstance()
            .getReference("pdv").child(TelaInicial.getCurrentPdv().getId()).child("produtos");

    // Não utilizar imagem ainda
    private String nome = "";
    private BigDecimal valor = new BigDecimal(0);
    private int quantidade = 0;
    private String codDBarras = "";

    public Produto() {
        this("", 0.0, 1, "");
    }

    public Produto(String nome, double valor, int quantidade, String codDBarras) {
        this.nome = nome;
        this.valor = new BigDecimal(valor);
        this.valor = this.valor.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.quantidade = quantidade;
        this.codDBarras = codDBarras;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getValor() {
        return valor.toPlainString();
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Exclude
    public BigDecimal getValorReal() {
        return valor;
    }

    public String getCodDBarras() {
        return codDBarras;
    }

    public void setCodDBarras(String codDBarras) {
        this.codDBarras = codDBarras;
    }
}
