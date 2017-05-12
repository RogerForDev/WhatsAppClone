package com.cursoudemy.kionux.whatsappclone.model;

import com.google.firebase.database.Exclude;

/**
 * Created by kionux on 16/01/17.
 */

public class Usuario {

    private String id;
    private String Nome;
    private String email;
    private String senha;


    public Usuario() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
