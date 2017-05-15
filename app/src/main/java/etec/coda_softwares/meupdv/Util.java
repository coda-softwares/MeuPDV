package etec.coda_softwares.meupdv;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by samuelh on 07/05/17.
 */

public class Util {

    public static final int REQ_IMG = 1547;
    public static final int REQUEST_FOTO = 598;
    public static final String NO_IMG = "$NOIMG$";

    public static String lerString(EditText view) {
        return view.getText().toString();
    }

    public static double lerDouble(EditText t) {
        return Double.parseDouble(t.getText().toString());
    }

    public static boolean verificarStringsVazias(String... valores) {
        for (String s : valores) {
            if (s.trim().equals("")) {
                return false;
            }
        }
        return true;
    }

    public static void showToast(AppCompatActivity ativ, String msg) {
        Toast.makeText(ativ, msg, Toast.LENGTH_SHORT).show();
    }

    public static Dialog dialogoCarregando(Activity a) {
        Dialog res = new AlertDialog.Builder(a).setView(R.layout.layout_loading).create();
        res.show();
        return res;
    }
}
