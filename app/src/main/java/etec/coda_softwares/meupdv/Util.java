package etec.coda_softwares.meupdv;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import etec.coda_softwares.meupdv.entitites.Usuario;

/**
 * Created by samuelh on 07/05/17.
 */

public class Util {
    public static final int REQ_IMG = 1547;
    public static final int REQUEST_FOTO = 598;
    public static final String NO_IMG = "$NOIMG$";
    public static final DateFormat DateFormater = new SimpleDateFormat("dd/MM/yyyy");
    static Usuario usuario;

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
     * Simples metodo que formata emails antes de serem guardados no banco de dados,
     * verdadeiro objetivo é manter a consistencia dos emails.
     *
     * @param email email em formato normal para ser convertido
     * @return email pronto para ser guardado no banco de dados
     */
    public static String jsonifyEmail(String email) {
        return email.toLowerCase().replace("@", ";A").replace(".", ";P");
    }

    public static String unjsonifyEmail(String email) {
        return email.replace("@", ";A").replace(";P", ".");
    }

    public static Calendar getCalendar(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        return c;
    }
}
