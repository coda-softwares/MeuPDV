package etec.coda_softwares.meupdv.entitites;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Created by jeffbustercase on 06/05/17.
 */

public class Venda implements Serializable {
    public static final DatabaseReference DBROOT = FirebaseDatabase.getInstance()
            .getReference("pdv").child(TelaInicial.CURRENT_PDV.getId()).child("vendas");

    private long data;
    private String funcionario, total;
    private ArrayList<Produto> produtos;

    /**
     * NOTA: Não editar uma venda já comfirmada
     */
    public Venda(long data, String funcionario, ArrayList<Produto> produtos, String total) {
        this.data = data;
        this.funcionario = funcionario;
        this.produtos = produtos;
        this.total = total;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(String funcionario) {
        this.funcionario = funcionario;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(ArrayList<Produto> produtos) {
        this.produtos = produtos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Venda venda = (Venda) o;

        if (data != venda.data) return false;
        if (funcionario != null ? !funcionario.equals(venda.funcionario) : venda.funcionario != null)
            return false;
        if (total != null ? !total.equals(venda.total) : venda.total != null) return false;
        return produtos != null ? produtos.equals(venda.produtos) : venda.produtos == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (data ^ (data >>> 32));
        result = 31 * result + (funcionario != null ? funcionario.hashCode() : 0);
        result = 31 * result + (total != null ? total.hashCode() : 0);
        result = 31 * result + (produtos != null ? produtos.hashCode() : 0);
        return result;
    }
}
