package etec.coda_softwares.meupdv;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import etec.coda_softwares.meupdv.entitites.Produto;

/**
 * Created by samuelh on 07/05/17.
 */

public class Util {
    public static final int REQ_IMG = 1547;
    public static final int REQUEST_FOTO = 598;
    public static final String NO_IMG = "$NOIMG$";
    public static final DateFormat DateFormater = new SimpleDateFormat("dd/MM/yyyy");

    public static String lerString(EditText view) {
        return view.getText().toString();
    }

    public static double lerDouble(EditText t) {
        return Double.parseDouble(t.getText().toString());
    }

    public static boolean temStringVazia(String... valores) {

        for (String s : valores) {
            if (s.trim().equals("")) {
                return true;
            }
        }
        return false;
    }

    public static void showToast(AppCompatActivity ativ, String msg) {
        Toast.makeText(ativ, msg, Toast.LENGTH_SHORT).show();
    }

    public static Dialog dialogoCarregando(Activity a) {
        Dialog res = new AlertDialog.Builder(a).setView(R.layout.layout_loading).create();
        res.setCancelable(false);
        res.show();
        return res;
    }

    /**
     * Necessária a implementação aqui pois é utilizado em mais de 1 lugar
     *
     * Metodo reorganiza a lista de produtos de acordo com o nome a ser pesquisado
     *
     * @param searchString
     * @param adapterProdutos -> Lista a ser Reorganizada
     */
    public static void pesquisarProduto(String searchString, FirebaseListAdapter adapterProdutos){
        HashMap<String, Integer> winHashMap = new HashMap<>();

        // O tipo de pesquisa principal é por nome
        // Mas depois poderá ser viavel adicionar por data de validade
        for(int i=0;i<adapterProdutos.getCount();i++){
            Produto prod = (Produto)adapterProdutos.getItem(i);
            int points = 0;
            for(Character ch : prod.getNome().toCharArray()){
                if(searchString.indexOf(ch)!=-1)
                    points++;

                if(searchString.indexOf(prod.getNome())!=-1)
                    points*=2;
            }
            winHashMap.put(prod.getCodDBarras(), points);
        }

        // Organiza em uma lista
        String[] codDBarrasArrayPerWin = new String[adapterProdutos.getCount()];
        int winPlace = 0;


        String winnerCodDBarras = ((Produto)adapterProdutos.getItem(0)).getCodDBarras();
        int winnerPoint = winHashMap.get(winnerCodDBarras);

        for (int i=0;i<adapterProdutos.getCount();i++){
            Produto prod = (Produto)adapterProdutos.getItem(i);
            int point = winHashMap.get(prod.getCodDBarras());
            if ( point > winnerPoint ){
                winnerCodDBarras = prod.getCodDBarras();
                winnerPoint = winHashMap.get(winnerCodDBarras);
                codDBarrasArrayPerWin[0] = winnerCodDBarras;
                // Winner has changed
                // Rework the list
                String[] newArrayPerWin = new String[codDBarrasArrayPerWin.length];
                newArrayPerWin[0] = winnerCodDBarras;
                for(int index=1;index<codDBarrasArrayPerWin.length;index++)
                    newArrayPerWin[index] = codDBarrasArrayPerWin[index-1];
            } else if ( point == winnerPoint) {
                codDBarrasArrayPerWin[winPlace++] = prod.getCodDBarras();
            }
        }
    }
}
