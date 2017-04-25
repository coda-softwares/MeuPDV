package etec.coda_softwares.meupdv.menuPrincipal;

import android.content.Context;
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

import etec.coda_softwares.meupdv.R;

/**
 * Created by dovahkiin on 22/04/17.
 */

public class MenuPrincipalAdapter extends ArrayAdapter<ItemMenuPrincipal> {
    private AlphaAnimation an = new AlphaAnimation(.5f, 1);

    public MenuPrincipalAdapter(@NonNull Context context, @LayoutRes int resource,
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
        final ItemMenuPrincipal ie = getItem(position);
        if (ie == null) {
            Log.wtf(this.getClass().getName(), "Indentificator of list is null");
            System.exit(1);
        }
        View v;
        if (convertView == null) {
            v = inflater.inflate(R.layout.menu_principal_item, parent, false);
        } else {
            v = convertView;
        }

        ImageView imageView = (ImageView) v.findViewById(R.id.item_img);
        imageView.setImageResource(ie.getImage());

        TextView title = (TextView) v.findViewById(R.id.item_Title);
        title.setText(ie.getTitulo());

        TextView decr = (TextView) v.findViewById(R.id.item_Descr);
        decr.setText(ie.getDescricao());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(an);
                ie.getAction().run();
            }
        });

        return v;
    }
}
