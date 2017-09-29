package com.example.lucasrezende.igor.controller.adventures;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lucasrezende.igor.R;
import com.example.lucasrezende.igor.api.AdventureServiceImplentation;
import com.example.lucasrezende.igor.api.ResponseBody;
import com.example.lucasrezende.igor.api.UserServiceImplentation;
import com.example.lucasrezende.igor.controller.adventures.adventureinfo.AdventureInfoActivity;
import com.example.lucasrezende.igor.model.Adventure;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vzaffalon on 13/09/17.
 */

public class AdventureListFragment extends Fragment {
    List<Adventure> adventures = new ArrayList<>();
    RecyclerView recyclerView;
    AdventureServiceImplentation client;
    View view;
    private ImageButton new_adventure_button;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_adventure_list, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAdventuresList();
        setUpLayout();
    }

    private void setUpLayout(){
        new_adventure_button = (ImageButton) view.findViewById(R.id.new_adventure_button);
        new_adventure_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),NewAdventureActivity.class);
                startActivity(intent);
            }
        });
    }


    private AdventuresAdapter.OnClickListener onClickEvento(){
        return new AdventuresAdapter.OnClickListener(){
            @Override
            public void onClickEvento(View view, int idx) {
                //aqui entra quando um dos elementos da lista é selecionado
                Intent intent = new Intent(getContext(), AdventureInfoActivity.class);
                startActivity(intent);
            }
        };
    }

    private AdventuresAdapter.OnClickListenerDeleteButton onClickDeleteButton(){
        return new AdventuresAdapter.OnClickListenerDeleteButton(){
            @Override
            public void onClickDeleteButton(View view, int idx) {
               deleteAdventure(idx);
            }
        };
    }

    private void deleteAdventure(int idx){
        client = new AdventureServiceImplentation(getContext());
        Call<Adventure> call = client.getService().delete(adventures.get(idx).getId());

        // Execute the call asynchronously. Get a positive or negative callback.
        call.enqueue(new Callback<Adventure>() {
            @Override
            public void onResponse(Call<Adventure> call, Response<Adventure> response) {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(),"Aventura deletada com sucesso",Toast.LENGTH_SHORT).show();
                    getAdventuresList();
                    setUpLayout();
                }else{
                    Toast.makeText(getContext(),"Falha ao deletar a aventura",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Adventure> call, Throwable t) {
                // the network call was a failure
                // TODO: handle error
                Log.d("Error", t.getMessage());
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAdventuresList(){
        client = new AdventureServiceImplentation(getContext());
        // Fetch a list of the Github repositoriesteste.
        Call<List<Adventure>> call = client.getService().list();

        // Execute the call asynchronously. Get a positive or negative callback.
        call.enqueue(new Callback<List<Adventure>>() {
            @Override
            public void onResponse(Call<List<Adventure>> call, Response<List<Adventure>> response) {
                // The network call was a success and we got a response
                // TODO: use the repository list and display it
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(),"Lista obtida com sucesso",Toast.LENGTH_SHORT).show();
                    adventures = response.body();
                    setUpList();
                }else{
                    Toast.makeText(getContext(),"Falha na obtenção da lista",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Adventure>> call, Throwable t) {
                // the network call was a failure
                // TODO: handle error
                Log.d("Error", t.getMessage());
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpList(){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new AdventuresAdapter(getContext(), adventures, onClickEvento(),onClickDeleteButton()));
    }
}
