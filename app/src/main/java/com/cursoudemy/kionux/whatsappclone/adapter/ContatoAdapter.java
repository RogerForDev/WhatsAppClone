package com.cursoudemy.kionux.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.model.Contato;

import java.util.ArrayList;

/**
 * Created by kionux on 18/01/17.
 */

public class ContatoAdapter extends ArrayAdapter<Contato> {

    private Context context;
    private ArrayList<Contato> contatos;

    public ContatoAdapter(Context c, ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.context = c;
        this.contatos = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        //verifica se a lista esta preenchida
        if(contatos != null){
            //inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //monta a view a partir do xml
            view = inflater.inflate(R.layout.lista_contatos, parent, false);

            //recuperar elementos para exibicao
            TextView textView = (TextView) view.findViewById(R.id.tv_nome);

            Contato contato = contatos.get(position);
            textView.setText(contato.getNome());
        }

        return view;
    }
}
