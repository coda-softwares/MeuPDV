package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private Fornecedor fornecedor;
    private String codDBarras = "";

    public Produto() {
        this("", 0.0, 1, "", new Fornecedor());
    }

    public Produto(String nome, double valor, int quantidade, String codDBarras, Fornecedor f) {
        this.nome = nome;
        this.valor = new BigDecimal(valor);
        this.valor = this.valor.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.quantidade = quantidade;
        this.fornecedor = f;
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

    @Exclude
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setValor(String valor) {
        this.valor = new BigDecimal(valor);
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

    @Exclude
    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    @Exclude
    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getFornecedorId() {
        if (fornecedor == null) return "";
        return fornecedor.getId();
    }

    public void setFornecedorId(String id) {
        Fornecedor.DBROOT.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fornecedor = dataSnapshot.getValue(Fornecedor.class);
                if (fornecedor == null) {
                    FirebaseCrash.log("Fornecedor carregado nulo");
                    System.exit(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseCrash.report(databaseError.toException());
                System.exit(1);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Produto produto = (Produto) o;

        if (quantidade != produto.quantidade) return false;
        if (!nome.equals(produto.nome)) return false;
        if (valor != null ? !valor.equals(produto.valor) : produto.valor != null) return false;
        if (fornecedor != null ? !fornecedor.equals(produto.fornecedor) : produto.fornecedor != null)
            return false;
        return codDBarras.equals(produto.codDBarras);

    }

    @Override
    public int hashCode() {
        int result = nome.hashCode();
        result = 31 * result + (valor != null ? valor.hashCode() : 0);
        result = 31 * result + quantidade;
        result = 31 * result + (fornecedor != null ? fornecedor.hashCode() : 0);
        result = 31 * result + codDBarras.hashCode();
        return result;
    }
}
