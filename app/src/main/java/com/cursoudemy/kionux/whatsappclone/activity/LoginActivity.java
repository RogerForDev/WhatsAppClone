package com.cursoudemy.kionux.whatsappclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.helper.Preferencias;
import com.cursoudemy.kionux.whatsappclone.helper.base64Custom;
import com.cursoudemy.kionux.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseauth;
    private DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
    //private DatabaseReference mensagemreferencia = databaseReferencia.child("mensagens");
    private EditText email;
    private EditText senha;
    private Button btnLogar;
    private Usuario usuario;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseauth = FirebaseAuth.getInstance();

        verificarUsuarioLogado();

        email = (EditText) findViewById(R.id.editEmail);
        senha = (EditText) findViewById(R.id.editSenha);
        btnLogar = (Button) findViewById(R.id.btn_login);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                validarLogin();
            }
        });
    }

    private void validarLogin() {

        firebaseauth.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //recuperar dados do usuario
                    idUsuarioLogado = base64Custom.converterBase64(usuario.getEmail());

                    databaseReferencia.child("usuario")
                            .child(idUsuarioLogado)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Usuario usuario = dataSnapshot.getValue(Usuario.class);

                                    //Salvar email nas preferencias
                                    String identificadorUsuarioLogado = base64Custom.converterBase64(usuario.getEmail());

                                    Preferencias preferencias = new Preferencias(LoginActivity.this);
                                    preferencias.salvarDados(identificadorUsuarioLogado, usuario.getNome());

                                    abrirTelaPrincipal();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }else{
                    Toast.makeText(LoginActivity.this, "Falha: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void abrirCadastroUsuario(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }
    private void verificarUsuarioLogado(){
        if(firebaseauth.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }
}
