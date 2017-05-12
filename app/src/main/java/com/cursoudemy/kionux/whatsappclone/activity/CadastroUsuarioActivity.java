package com.cursoudemy.kionux.whatsappclone.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.helper.base64Custom;
import com.cursoudemy.kionux.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroUsuarioActivity extends AppCompatActivity{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference usuarioDatabase = database.child("usuario");

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button btnCadastrar;
    private Usuario usuario;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome         = (EditText) findViewById(R.id.editCadastroNome);
        email        = (EditText) findViewById(R.id.editCadastroEmail);
        senha        = (EditText) findViewById(R.id.editCadastroSenha);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setNome(nome.getText().toString());
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                cadastrarUsuario();
            }
        });

    }

    private void cadastrarUsuario() {

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CadastroUsuarioActivity.this, "Usuario cadastrado com sucesso!",Toast.LENGTH_SHORT).show();
                            salvarUsuario();
                            finish();
                        }else {
                            Toast.makeText(CadastroUsuarioActivity.this, "Falha: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void salvarUsuario() {
        FirebaseUser firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        String identificador = base64Custom.converterBase64(usuario.getEmail());
        usuario.setId(identificador);

        usuarioDatabase.child(usuario.getId()).setValue(usuario);
    }
}
