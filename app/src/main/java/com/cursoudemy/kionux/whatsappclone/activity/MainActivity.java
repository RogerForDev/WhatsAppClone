package com.cursoudemy.kionux.whatsappclone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.adapter.TabAdapter;
import com.cursoudemy.kionux.whatsappclone.helper.Preferencias;
import com.cursoudemy.kionux.whatsappclone.helper.SlidingTabLayout;
import com.cursoudemy.kionux.whatsappclone.helper.base64Custom;
import com.cursoudemy.kionux.whatsappclone.model.Contato;
import com.cursoudemy.kionux.whatsappclone.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;

    final DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
    final DatabaseReference contatoReferencia = databaseReferencia.child("contato");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.textColorPrimary));


        //Configurar adapter

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_configuracoes:
                return true;
            case R.id.item_adicionar:
                abrirCadastroContato();
                return true;
            case R.id.item_pesquisa:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void abrirCadastroContato() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Novo contato\n");
       // alertDialog.setMessage("Email do usuário");
        alertDialog.setCancelable(false);

        //criar campo texto
        final EditText editText = new EditText(this);
        editText.setHint("Digite o email do usuário");
        editText.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS );
        alertDialog.setView(editText);

        //definir botao positivo
        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailContato = editText.getText().toString();
                if(emailContato.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Preencha o email",Toast.LENGTH_SHORT).show();
                }else{
                    //verificar se usuario esta cadastrado no app
                    identificadorContato = base64Custom.converterBase64(emailContato);

                    Log.i("IDENTIFICADOR", "ID:"+identificadorContato);

                    databaseReferencia.child("usuario").child(identificadorContato)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //verifica se foi retornado algum usuario
                            if(dataSnapshot.getValue() != null){
                                Log.i("DATASNAPSHOT", "DATA:"+dataSnapshot);

                                //recuperar dados do contato a ser adicionado
                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);

                                //recuperar dados usuarioContato logado
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado =  preferencias.getIdentificador();

                                //Salvar dados firebase
                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identificadorContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());

                                Log.i("USUARIO", "EMAIL:"+usuarioContato.getEmail());
                                Log.i("USUARIO", "NOME:"+usuarioContato.getNome());

                                //salvando no firebase
                                contatoReferencia.child(identificadorUsuarioLogado)
                                        .child(identificadorContato)
                                        .setValue(contato);

                            }else{
                                Toast.makeText(getApplicationContext(), "Usuario não possui cadastro no app",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
        //definir botao negativo
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    private void deslogarUsuario() {
        firebaseAuth.signOut();
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
