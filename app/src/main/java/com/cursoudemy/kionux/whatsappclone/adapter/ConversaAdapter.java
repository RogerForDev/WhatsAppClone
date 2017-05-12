package com.cursoudemy.kionux.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.model.Conversa;

import java.util.ArrayList;

/**
 * Created by kionux on 19/01/17.
 */
public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private Context context;
    private ArrayList<Conversa> conversas;
    private Conversa conversa;

    public ConversaAdapter(Context c, ArrayList<Conversa> objects) {
        super(c,0, objects);
        this.context=c;
        this.conversas=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        //verifica lista esta preenchida
        if(conversas != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //monta a view a partir do xml
            view = inflater.inflate(R.layout.lista_conversas, parent, false);

            //recupera elementos da tela
            TextView nome = (TextView) view.findViewById(R.id.text_nome);
            TextView ultimaMensagem = (TextView) view.findViewById(R.id.text_conversa);

            //setar valores nos compónentes da tela
            conversa = conversas.get(position);
            nome.setText(conversa.getNome());
            ultimaMensagem.setText(conversa.getMensagem());

        }

        return view;
    }
}
