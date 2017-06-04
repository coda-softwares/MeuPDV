package etec.coda_softwares.meupdv;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;
import etec.coda_softwares.meupdv.entitites.PDV;
import etec.coda_softwares.meupdv.entitites.Usuario;

public class EditPDV extends AppCompatActivity {
    private static final int REQ_IMG = 2302;
    PDV novoPdv;
    private CircleImageView imagemView;
    private Uri image;
    private EditText lemaView;
    private EditText nomeView;
    private ListView partc;

    private void terminar() {
        String nome = nomeView.getText().toString().trim();
        String lema = lemaView.getText().toString().trim();
        if (Util.temStringVazia(nome, lema)) {
            Util.showToast(this, "Campos vazios nao permitidos.");
            return;
        }
        Dialog loading = Util.dialogoCarregando(this);
        novoPdv.setNome(nome);
        novoPdv.setLema(lema);
        if (image != null) {
            TelaInicial.eraseFile(novoPdv.getImagem());
            novoPdv.setImagem(image);
        }
        PDV.ROOT.child(novoPdv.getId()).setValue(novoPdv);
        TelaInicial.CURRENT_PDV = novoPdv;
        setResult(RESULT_OK);
        loading.dismiss();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater a = new MenuInflater(this);
        a.inflate(R.menu.menu_confirma, menu);
        MenuItem confirma = menu.findItem(R.id.botao_confirma);
        confirma.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                terminar();
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_IMG) {
                this.image = data.getParcelableExtra("imagem");
                imagemView.setImageURI(this.image);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pdv);
        novoPdv = TelaInicial.CURRENT_PDV;

        Toolbar tb = (Toolbar) findViewById(R.id.epdv_toobar);
        setSupportActionBar(tb);

        lemaView = (EditText) findViewById(R.id.epdv_lema);
        nomeView = (EditText) findViewById(R.id.epdv_nome);
        imagemView = (CircleImageView) findViewById(R.id.epdv_img);
        partc = (ListView) findViewById(R.id.epdv_participantes);

        imagemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n = new Intent(EditPDV.this, CarregarImagem.class);
                startActivityForResult(n, REQ_IMG);
            }
        });
        nomeView.setText(novoPdv.getNome());
        lemaView.setText(novoPdv.getLema());
        if (!novoPdv.getImagem().equals("")) {
            TelaInicial.getFile(novoPdv.getImagem(), new TelaInicial.UriCallback() {
                @Override
                public void done(Uri u) {
                    imagemView.setImageURI(u);
                }
            });
        }

        final Iterator<String> userIds = novoPdv.getIntegrantes().iterator();

        if (userIds.hasNext()) {
            final Stack<Usuario> uStack = new Stack<>();
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user");
            ref.child(userIds.next()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario u = dataSnapshot.getValue(Usuario.class);
                    if (u != null) {
                        uStack.push(u);
                    }
                    if (userIds.hasNext()) {
                        ref.child(userIds.next()).addListenerForSingleValueEvent(this);
                    } else {
                        populateParticipantes(uStack);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    FirebaseCrash.report(databaseError.toException());
                    System.exit(1);
                }
            });
        }
    }

    private void populateParticipantes(List<Usuario> usuarios) {
        final ArrayAdapter<Usuario> adap = new ArrayAdapter<Usuario>(this,
                R.layout.participante_item, usuarios) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = EditPDV.this.getLayoutInflater().inflate(
                            R.layout.participante_item, parent, false);
                }

                Usuario usu = this.getItem(position);
                assert usu != null;
                TextView name = (TextView) convertView.findViewById(R.id.pp_nome);
                TextView descr = (TextView) convertView.findViewById(R.id.pp_email);

                name.setText(usu.getNome());
                descr.setText(usu.getEmail());

                return convertView;
            }
        };
        partc.setAdapter(adap);

    }

    public void enviarEnvite(View v) {
        final EditText editText = new EditText(this);
        editText.setHint("E-mail");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText.setMaxLines(1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Adicionar Participante")
                .setView(editText)
                .setCancelable(true);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = editText.getText().toString().trim();
                if (email.isEmpty() || !(email.contains("@") && email.contains("."))) {
                    Util.showToast(EditPDV.this, "Email inválido.");
                    return;
                }
                FirebaseDatabase.getInstance().getReference().child("convites")
                        .child(Util.jsonifyEmail(email)).setValue(novoPdv.getId());

                // Já que nao vamos pagar pra um serviço mandar o email automaticamente...
                // Pensei que o proprio usuario mandasse...
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                i.putExtra(Intent.EXTRA_SUBJECT, "Participe do MeuPDV");
                i.putExtra(Intent.EXTRA_TEXT, "Logue-se com esse email no nosso aplicativo, " +
                        "disponivel em... lalala");
                try {
                    startActivity(Intent.createChooser(i, "Escolha o aplicativo de email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Util.showToast(EditPDV.this, "Nenhum cliente de email disponivel...");
                }
            }
        });
        builder.show();
    }
}
