package etec.coda_softwares.meupdv.Produtos;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.widget.Button;

import etec.coda_softwares.meupdv.Produto;
import etec.coda_softwares.meupdv.R;
import etec.coda_softwares.meupdv.Produtos.ItemProdutos;

/**
 * Created by jeffbustercase on 28/04/17.
 */

public class ListaProdutosAdapter extends ArrayAdapter<ItemProdutos> {
    private AlphaAnimation an = new AlphaAnimation(.5f, 1);

    public ListaProdutosAdapter(@NonNull Context context, @LayoutRes int resource,
                                @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
        an.setDuration(500);
        an.setFillBefore(true);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ItemProdutos ie = (ItemProdutos) getItem(position);
        if (ie == null) {
            Log.wtf(this.getClass().getName(), "Indentificador da lista é nulo!");
            System.exit(1);
        }
        View v;
        if (convertView == null) {
            v = inflater.inflate(R.layout.produtos_item, parent, false);
        } else {
            v = convertView;
        }

        ImageView imageView = (ImageView) v.findViewById(R.id.prod_icon);
        imageView.setImageResource(ie.getImage());

        TextView title = (TextView) v.findViewById(R.id.prod_titulo);
        title.setText(ie.getTitulo());

        TextView preco = (TextView) v.findViewById(R.id.prod_preco);
        preco.setText(ie.getPreco()+"");

        Button button = (Button) v.findViewById(R.id.prod_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Produto.class);
                // Se for necessário utilizar um id para carregar a imagem do produto
                intent.getExtras().putString("title", ie.getTitulo());
                intent.getExtras().putDouble("preco", ie.getPreco());
                intent.getExtras().putString("firebase_id", "Kh8H6BOdgN9nDY3nd");

                view.getContext().startActivity(intent);
            }
        });

        //Não Sei Se vou precisar disso...

        //v.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //        public void onClick(View v) {
        //            v.startAnimation(an);
        //            ie.getAction().run();
        //        }
        //    });

        return v;
    }
}
