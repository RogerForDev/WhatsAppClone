package com.cursoudemy.kionux.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.helper.Preferencias;
import com.cursoudemy.kionux.whatsappclone.model.Mensagem;

import java.util.ArrayList;

/**
 * Created by kionux on 19/01/17.
 */

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;

    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context=c;
        this.mensagens=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;

        if(mensagens!=null){
            //recuperar mensagem
            Mensagem mensagem = mensagens.get(position);

            //recuperar dados usuario logado
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioLogado = preferencias.getIdentificador();

            //inicializa objeto para montagem do layout
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //monta o view a partir do xml
            if(idUsuarioLogado.equals(mensagem.getIdUsuario())) {
                view = layoutInflater.inflate(R.layout.item_mensagem_direita, parent, false);
            }else {
                view = layoutInflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
            }

            //recuperar elementos para exibição
            TextView textView = (TextView) view.findViewById(R.id.tv_mensagem);
            textView.setText(mensagem.getMensagem());
        }

        return view;
    }
}
