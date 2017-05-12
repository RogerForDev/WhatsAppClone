package com.cursoudemy.kionux.whatsappclone.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cursoudemy.kionux.whatsappclone.R;
import com.cursoudemy.kionux.whatsappclone.activity.ConversaActivity;
import com.cursoudemy.kionux.whatsappclone.adapter.ConversaAdapter;
import com.cursoudemy.kionux.whatsappclone.helper.Preferencias;
import com.cursoudemy.kionux.whatsappclone.helper.base64Custom;
import com.cursoudemy.kionux.whatsappclone.model.Conversa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ConversasFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Conversa> arrayAdapter;
    private ArrayList<Conversa> conversas;
    private ValueEventListener valueEventListenerConversas;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public ConversasFragment() {    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_conversas, container, false);

        //Montar listview e adapter
        conversas = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.lv_conversas);
        arrayAdapter = new ConversaAdapter(getActivity(), conversas);
        listView.setAdapter(arrayAdapter);

        /*Recuperar conversas firebase*/

        Preferencias preferencias = new Preferencias(getActivity());
        String idUsuariologado = preferencias.getIdentificador();

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversas.clear();
                for(DataSnapshot dados : dataSnapshot.getChildren()){
                    Conversa conversa = dados.getValue(Conversa.class);
                    conversas.add(conversa);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //instancia do firebase
        databaseReference.child("conversa")
                .child(idUsuariologado)
                .addValueEventListener(valueEventListenerConversas);

        //Adicionar evento clique na lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //recuperar conversa para a posição
                Conversa conversa = conversas.get(position);

                //criar intent para conversaactivity
                Intent i = new Intent(getActivity(), ConversaActivity.class);
                String email = base64Custom.decodificarBase64(conversa.getIdUsuario());
                i.putExtra("email", email);
                i.putExtra("nome", conversa.getNome());
                startActivity(i);

            }
        });
      return view;
    }

}
