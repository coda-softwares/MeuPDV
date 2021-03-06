package etec.coda_softwares.meupdv.entitites;

import android.net.Uri;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import etec.coda_softwares.meupdv.TelaInicial;
import etec.coda_softwares.meupdv.Util;

/**
 * Created by jeffbustercase on 06/05/17.
 */

public class Produto implements Serializable {
    public static final DatabaseReference DBROOT = FirebaseDatabase.getInstance()
            .getReference("pdv").child(TelaInicial.CURRENT_PDV.getId()).child("produtos");
    public static final DatabaseReference NOSTOQ_DBROOT = FirebaseDatabase.getInstance()
            .getReference("pdv").child(TelaInicial.CURRENT_PDV.getId()).child("produtos_zero");

    private String nome = "";
    private Date validade = new Date();
    private BigDecimal valor = new BigDecimal(0);
    private int quantidade = 0;
    private Fornecedor fornecedor = new Fornecedor();
    private String codDBarras = "";
    private String imagem = "";
    private long ultimaModificacao;

    public Produto() {
        ultimaModificacao = System.currentTimeMillis();
    }

    public Produto(String nome, Date validade, String valor, int quantidade, String codDBarras, Fornecedor f) {
        this.nome = nome;
        this.validade = validade;
        if (valor.equals(""))
            valor = "0";
        this.valor = new BigDecimal(valor);
        this.valor = this.valor.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.quantidade = quantidade;
        this.fornecedor = f;
        this.codDBarras = codDBarras;
        ultimaModificacao = System.currentTimeMillis();
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean hasImagem(){
        return !imagem.contains(Util.NO_IMG);
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

    @Exclude
    public void setImagem(Uri e) {

    }

    public Date getValidade() { return validade; }

    public void setValidade(Date validade) { this.validade = validade; }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getValor() {
        return valor.toPlainString();
    }

    public void setValor(String valor) {
        this.valor = new BigDecimal(valor);
    }

    @Exclude
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
