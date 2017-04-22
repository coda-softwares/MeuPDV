package etec.coda_softwares.meupdv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TelaInicial extends Activity {
    final Animation outAnim = new ScaleAnimation(1, 0, 1, 0);
    final Animation inAnim = new ScaleAnimation(0, 1, 0, 1);
    private EditText tbUsername;
    private EditText tbSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        final TextView lbUsername = (TextView) findViewById(R.id.log_lb_username);
        tbUsername = (EditText) findViewById(R.id.log_tb_username);
        final TextView lbSenha = (TextView) findViewById(R.id.log_lb_senha);
        tbSenha = (EditText) findViewById(R.id.log_tb_senha);

        inAnim.setDuration(700);
        outAnim.setDuration(700);

        tbUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    lbUsername.startAnimation(outAnim);
                    lbUsername.setVisibility(View.INVISIBLE);
                } else if (start == 0) {
                    lbUsername.setVisibility(View.VISIBLE);
                    lbUsername.startAnimation(inAnim);
                }
            }
        });
        tbSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    lbSenha.startAnimation(outAnim);
                    lbSenha.setVisibility(View.INVISIBLE);
                } else if (start == 0) {
                    lbSenha.setVisibility(View.VISIBLE);
                    lbSenha.startAnimation(inAnim);
                }
            }
        });
    }

    private void setEditable(boolean editable) {
        tbUsername.setClickable(editable);
        tbUsername.setFocusable(editable);
        tbUsername.setEnabled(editable);

        tbSenha.setClickable(editable);
        tbSenha.setFocusable(editable);
        tbSenha.setEnabled(editable);
    }

    public void logar(final View v) {
        final String username = tbUsername.getText().toString();
        final String senha = tbSenha.getText().toString();
        if (username.length() < 3) {
            Toast.makeText(this, "Usuario muito curto", Toast.LENGTH_LONG).show();
        } else if (senha.length() < 6) {
            Toast.makeText(this, "Senha muito curta", Toast.LENGTH_LONG).show();
        }
        v.setEnabled(false);
        final ProgressBar load =
                (ProgressBar) TelaInicial.this.findViewById(R.id.log_loading);
        load.setVisibility(View.VISIBLE);
        load.startAnimation(inAnim);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TelaInicial.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TelaInicial.this,
                                "Usuario é: " + username, Toast.LENGTH_LONG).show();
                        setEditable(true);
                        //FIXME These are supposed when the login fails.
                        //load.startAnimation(outAnim);
                        //load.setVisibility(View.GONE);
                        //v.setEnabled(true);
                        Intent i = new Intent(TelaInicial.this, MenuPrincipal.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }).start();
        setEditable(false);
        //Stub
    }

}
