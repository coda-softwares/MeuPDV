package etec.coda_softwares.meupdv.entitites;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import etec.coda_softwares.meupdv.TelaInicial;

/**
 * Classe que representa o PDV do usuario, contendo todos os dados, o logo nome, produtos, etc.
 * Teoricamente somente uma instancia dessa classe por programa, instancia que fica salva em
 * {@link TelaInicial#CURRENT_PDV}
 *
 */
public class PDV implements Serializable {
    public static final DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference()
            .child("pdv");
    private String nome = "";
    private String lema = "";
    private String imagem = "";
    private String id = "";
    private List<String> integrantes;

    public PDV() {
        this("", "", "", Collections.singletonList(""));
    }

    public PDV(String nome, String lema, String imagem, List<String> integrantes) {
        this.nome = nome;
        this.lema = lema;
        this.imagem = imagem;
        this.integrantes = integrantes;
    }

    public PDV(String nome, String lema, Uri imagem, List<String> integrantes) {
        initId();
        setImagem(imagem);
        this.nome = nome;
        this.lema = lema;
        this.integrantes = integrantes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLema() {
        return lema;
    }

    public void setLema(String lema) {
        this.lema = lema;
    }

    public List<String> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<String> integrantes) {
        this.integrantes = integrantes;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (this.id.equals("") && !id.equals("")) {
            this.id = id;
        }
    }

    public DatabaseReference initId() {
        if (!id.equals(""))
            return null;
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null)
            return null;
        DatabaseReference ref = ROOT.push();
        this.id = ref.getKey();
        return ref;
    }

    /**
     * Atualiza ou salva a instancia desse PDV no banco de dados atual e assimila com
     * {@link FirebaseAuth#getCurrentUser()} até agora todas as atividades editoras
     * dos conteudos do PDV como fornecedor, atualizão o editado automaticamente
     * quando modificadas. (29/04/17)
     */
    public void saveOnDB(final Runnable callback) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        final HandlerPadrao save = new HandlerPadrao(null) {
            @Override
            public Transaction.Result doTransaction(MutableData data) {
                PDV saved = data.getValue(PDV.class);
                if (saved != null) {
                    if (saved.equals(PDV.this))
                        return Transaction.abort();
                }
                saved = PDV.this;
                data.setValue(saved);
                DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                root.child("user").child(user.getUid()).child("pdv").setValue(saved.getId());
                if (callback != null)
                    callback.run();
                return Transaction.success(data);
            }
        };

        if (id.equals("")) {
            initId().runTransaction(save);
        } else {
            ROOT.child(id).runTransaction(save);
        }
    }

    public void pushToDB() {
        ROOT.child(getId()).setValue(this);
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagemUrl) {
        this.imagem = imagemUrl;
    }

    @Exclude
    public void setImagem(@NonNull Uri image) {
        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("pdv").child(getId()).child("logo.jpg");
        ref.putFile(image);
        setImagem(ref.toString());
    }
}
