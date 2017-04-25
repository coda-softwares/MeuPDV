package etec.coda_softwares.meupdv;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import java.net.SocketTimeoutException;
import java.util.HashMap;

import etec.coda_softwares.meupdv.menuPrincipal.MenuPrincipal;

public class TelaInicial extends AppCompatActivity {
    private EditText tbUsername;
    private EditText tbSenha;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        //final TextView lbUsername = (TextView) findViewById(R.id.log_lb_username);
        tbUsername = (EditText) findViewById(R.id.log_tb_username);
        //final TextView lbSenha = (TextView) findViewById(R.id.log_lb_senha);
        tbSenha = (EditText) findViewById(R.id.log_tb_senha);

    }

    public void logar(final View v) {
        final String username = tbUsername.getText().toString().trim();
        final String senha = tbSenha.getText().toString().trim();
        if (username.length() < 3) {
            Toast.makeText(this, "Usuario muito curto", Toast.LENGTH_LONG).show();
            return;
        } else if (senha.length() < 6) {
            Toast.makeText(this, "Senha muito curta", Toast.LENGTH_LONG).show();
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaInicial.this);
        builder.setCancelable(false)
                .setTitle("Logando")
                .setView(R.layout.layout_loading);
        final Dialog d = builder.create();
        final Usuario u = new Usuario("", username, senha, "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int code = 0;
                HashMap<String, String> postForm = new HashMap<>();
                postForm.put("body", gson.toJson(u));
                try {
                    HttpRequest req = HttpRequest.post("http://192.168.0.150/login", postForm,
                            false).connectTimeout(2000).readTimeout(5000);
                    code = req.code();
                } catch (HttpRequest.HttpRequestException e) {
                    if (e.getCause() instanceof SocketTimeoutException) {
                        code = 408;
                    } else {
                        e.printStackTrace();
                    }
                }
                final int fcode = code;
                TelaInicial.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        networkAnswer(d, fcode);
                    }
                });
            }
        }).start();
        d.show();
    }

    private void networkAnswer(Dialog d, int code) {
        code = 200; //FIXME: DEBUG ONLY!!!
        d.dismiss();
        if (code == 200) {
            Intent i = new Intent(TelaInicial.this, MenuPrincipal.class);
            startActivity(i);
            finish();
        }
        Toast.makeText(this, "Login falhou, código do erro: " + code, Toast.LENGTH_LONG).show();
    }

}
