package com.cursoudemy.kionux.whatsappclone.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.adapter.MensagemAdapter;
import com.cursoudemy.kionux.whatsappclone.helper.Preferencias;
import com.cursoudemy.kionux.whatsappclone.helper.base64Custom;
import com.cursoudemy.kionux.whatsappclone.model.Conversa;
import com.cursoudemy.kionux.whatsappclone.model.Mensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btEnviar;
    private ListView listView;
    private ArrayAdapter<Mensagem> arrayAdapter;
    private ArrayList<Mensagem> mensagens;
    private Conversa conversa;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener valueEventListenerMensagem;
    //Destinatario
    private String nomeUsuarioDestinatario;
    private String nomeUsuarioLogado;
    private String idUsuarioDestinatario;

    //remetente
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btEnviar = (ImageButton) findViewById(R.id.bt_enviar);
        listView = (ListView) findViewById(R.id.lv_mensagens);

        //recuperar dados do usuario logado
        Preferencias preferencia = new Preferencias(ConversaActivity.this);
        idUsuarioLogado = preferencia.getIdentificador();
        nomeUsuarioLogado = preferencia.getNome();

        //recuperar dados enviados na intent
        Bundle extra = getIntent().getExtras();

        if(extra != null){
            //recuperar dados do contato(destinatario)
            nomeUsuarioDestinatario = extra.getString("nome");
            idUsuarioDestinatario = base64Custom.converterBase64(extra.getString("email"));
        }

        /***** Montagem listview e adapter *****/
        mensagens = new ArrayList<>();

     /*   arrayAdapter = new ArrayAdapter<String>(
                ConversaActivity.this,
                R.layout.lista_contatos,
                mensagens
        );*/

        arrayAdapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        listView.setAdapter(arrayAdapter);

        /* Criar Listener para mensagem*/
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //limpar array list de mensages
                mensagens.clear();

                //recuperar mensagens firebase
                for(DataSnapshot dados : dataSnapshot.getChildren()){

                    //recuperar mensagem individual
                    Mensagem mensagem = dados.getValue(Mensagem.class);

                    //adicionar na lista mensagens
                    mensagens.add(mensagem);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        /* Recuperar mensagem firebase*/
        databaseReference.child("mensagem")
                .child(idUsuarioLogado)
                .child(idUsuarioDestinatario)
                .addValueEventListener(valueEventListenerMensagem);

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //enviar mensagem
        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();
                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite a mensagem para enviar", Toast.LENGTH_SHORT).show();
                }else{

                    /*SALVA MENSAGEM NO FIREBASE*/
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioLogado);
                    mensagem.setMensagem(textoMensagem);

                    //salva mensagem para o remetente
                    Boolean retornoRemetente = salvarMensagemFirebase(idUsuarioDestinatario,idUsuarioLogado,mensagem);
                    if(!retornoRemetente){
                        Toast.makeText(ConversaActivity.this, "Problema ao enviar mensagem, tente novamente!", Toast.LENGTH_LONG).show();
                    }
                    //salva mensagem para o destinatario
                    Boolean retornoDestinatario = salvarMensagemFirebase(idUsuarioLogado,idUsuarioDestinatario,mensagem);
                    if(!retornoDestinatario){
                        Toast.makeText(ConversaActivity.this, "Problema ao enviar mensagem, tente novamente!", Toast.LENGTH_LONG).show();
                    }

                    //salva conversa no firebase para o remetente
                    conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);

                    Boolean retornoConversaRem = salvarConversaFirebase(idUsuarioLogado,idUsuarioDestinatario,conversa);
                    if(!retornoConversaRem){
                        Toast.makeText(ConversaActivity.this, "Problema ao salvar conversa, tente novamente!", Toast.LENGTH_LONG).show();
                    }

                    /*salvar conversa no firebase para o destinatario*/
                    conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioLogado);
                    conversa.setNome(nomeUsuarioLogado);
                    conversa.setMensagem(textoMensagem);

                    Boolean retornoConversaDes = salvarConversaFirebase(idUsuarioDestinatario, idUsuarioLogado, conversa);
                    if(!retornoConversaDes){
                        Toast.makeText(ConversaActivity.this, "Problema ao salvar conversa, tente novamente!", Toast.LENGTH_LONG).show();
                    }

                    editMensagem.setText("");
                }
            }
        });
    }

    private Boolean salvarMensagemFirebase(String idRemetente, String idDestinatario, Mensagem mensagem) {
        try{
            databaseReference.child("mensagem")
                    .child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private Boolean salvarConversaFirebase(String idRemetente, String idDestinatario, Conversa conversa) {
        try{
            databaseReference.child("conversas")
                    .child(idRemetente)
                    .child(idDestinatario)
                    .setValue(conversa);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_conversa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_configuracoes) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
