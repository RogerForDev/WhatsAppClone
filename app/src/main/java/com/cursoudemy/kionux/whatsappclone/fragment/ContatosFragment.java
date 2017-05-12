package com.cursoudemy.kionux.whatsappclone.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.activity.ConversaActivity;
import com.cursoudemy.kionux.whatsappclone.adapter.ContatoAdapter;
import com.cursoudemy.kionux.whatsappclone.helper.Preferencias;
import com.cursoudemy.kionux.whatsappclone.model.Contato;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;

    final DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener valueEventListenerContato;

    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        /***** Montar ListView e adapter *****/

        listView = (ListView) view.findViewById(R.id.lv_contatos);

    /*    adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contatos,
                contatos
        );*/

        adapter = new ContatoAdapter(getActivity(), contatos);
        listView.setAdapter(adapter);

        /*****  Recuperar contatos do firebase *****/
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();

        //listener para recuperar usuario logado

        valueEventListenerContato = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //limpar lista
                contatos.clear();

                //listar contatos para o usuario
                for(DataSnapshot dados : dataSnapshot.getChildren()){

                    Contato contato = dados.getValue(Contato.class);
                    Log.i("CONTATO", "NOME: "+contato.getNome());
                    contatos.add(contato);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Adicionar evento click na lista

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ConversaActivity.class);

                //recupera dados a serem passados
                Contato contato = contatos.get(position);

                //Envia dados para ConversaActivity
                i.putExtra("nome", contato.getNome());
                i.putExtra("email", contato.getEmail());

                startActivity(i);
            }
        });

          databaseReferencia.child("contato").child(identificadorUsuarioLogado)
                .addValueEventListener(valueEventListenerContato);

        return view;
    }
}
