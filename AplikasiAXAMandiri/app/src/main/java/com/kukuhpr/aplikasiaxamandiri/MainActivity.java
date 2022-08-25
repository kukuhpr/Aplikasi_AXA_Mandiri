package com.kukuhpr.aplikasiaxamandiri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kukuhpr.aplikasiaxamandiri.Adapter.PostAdapter;
import com.kukuhpr.aplikasiaxamandiri.Interface.JSONPlaceHolder;
import com.kukuhpr.aplikasiaxamandiri.Model.Post;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv_JSONPH;
    private TextInputEditText et_cariJSONPH;
    SearchView searchView_tx;

    PostAdapter postAdapter;
    ArrayList<Post> postList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();


    }

    private void initViews() {

        //et_cariJSONPH = findViewById(R.id.edt_cariJSONPlaceHolder);

        //postAdapter = new PostAdapter(this,postList);

        rv_JSONPH = findViewById(R.id.rv_JSONPlaceHolder);
        rv_JSONPH.setHasFixedSize(true);
        //rv_JSONPH.setAdapter(postAdapter);
        rv_JSONPH.setLayoutManager(new LinearLayoutManager(this));



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JSONPlaceHolder jsonPlaceHolder = retrofit.create(JSONPlaceHolder.class);
        Call<List<Post>> listCall = jsonPlaceHolder.getPost();
        listCall.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (!response.isSuccessful()){
                    Toast.makeText(MainActivity.this, ""+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<Post> postList = (ArrayList<Post>) response.body();
                postAdapter = new PostAdapter(MainActivity.this, postList);
                rv_JSONPH.setAdapter(postAdapter);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        /*searchView_tx.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {



                return false;
            }
        });*/



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item,menu);
        MenuItem menuItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Masukan kata-kata");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                postAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}