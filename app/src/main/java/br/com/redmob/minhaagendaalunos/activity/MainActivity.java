package br.com.redmob.minhaagendaalunos.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import br.com.redmob.minhaagendaalunos.Model.Aluno;
import br.com.redmob.minhaagendaalunos.R;
import br.com.redmob.minhaagendaalunos.adapter.AlunoAdapter;
import br.com.redmob.minhaagendaalunos.dto.AlunoSync;
import br.com.redmob.minhaagendaalunos.helper.AlunoDAO;
import br.com.redmob.minhaagendaalunos.helper.DBHelper;
import br.com.redmob.minhaagendaalunos.helper.RecyclerItemClickListener;
import br.com.redmob.minhaagendaalunos.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //Atributos
    private RecyclerView rvAlunos;
    private AlunoAdapter alunoAdapter;
    private List<Aluno> listaAlunos = new ArrayList<>();
    private SwipeRefreshLayout swipeListaAlunos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FindViewById
        rvAlunos = findViewById(R.id.rvAlunos);
        swipeListaAlunos = findViewById(R.id.swipe_lista_aluno);

        //Swipe em Acao
        swipeListaAlunos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                buscaAlunos();
            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddAlunoActivity.class);
                startActivity(intent);
            }
        });



        //Evento de clique na RV
        rvAlunos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(), rvAlunos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int i) {

                                //Atualização
                                Aluno alunoClicado = listaAlunos.get(i);
                                Intent intent = new Intent(MainActivity.this, AddAlunoActivity.class);
                                intent.putExtra("alunoClicado", alunoClicado);
                                startActivity(intent);

                            }

                            @Override
                            public void onLongItemClick(View view, int i) {

                                final Aluno alunoClicado = listaAlunos.get(i);


                                //Exclusão Dao
                                AlertDialog.Builder ad =
                                        new AlertDialog.Builder(MainActivity.this);

                                ad.setTitle("Confirma a exclusao");
                                ad.setMessage("Deseja excluir o aluno " + alunoClicado.getNome() + " ?" );

                                ad.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        //Call Retrofit
                                        Call<Void> call = new RetrofitInicializador().getAlunoService().deleta(alunoClicado.getId());
                                        call.enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {

                                                AlunoDAO alunoDAO = new AlunoDAO(MainActivity.this);
                                                alunoDAO.deletar(alunoClicado);
                                                Toast.makeText(MainActivity.this,
                                                        "O aluno foi apagado com sucesso", Toast.LENGTH_LONG).show();
                                                carregaListaAlunos();


                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {

                                                Log.e("Falha na exclusao", t.getMessage() );
                                                Toast.makeText(MainActivity.this,
                                                        "Falha na exclusão", Toast.LENGTH_LONG).show();

                                            }
                                        });

                                    }
                                });

                                ad.setNegativeButton("Não", null);
                                ad.create();
                                ad.show();




                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        buscaAlunos();


    }

    private void buscaAlunos() {
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().lista();

        call.enqueue(new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                AlunoSync alunoSync = response.body();

                DBHelper db = new DBHelper(MainActivity.this);
                db.sincroniza(alunoSync.getAlunos(), MainActivity.this);
                db.close();
                carregaListaAlunos();
                swipeListaAlunos.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                swipeListaAlunos.setRefreshing(false);

            }


        });
    }


    public void carregaListaAlunos(){


        //Listar os alunos
        AlunoDAO alunoDAO = new AlunoDAO(getApplicationContext());
        listaAlunos = alunoDAO.listar();

        //Verificando o id de cada aluno
        List<Aluno> alunos = alunoDAO.listar();
        for (Aluno aluno:
             alunos) {
            Log.i("id do aluno", String.valueOf(aluno.getId()));
        }


        //Configurar o adapter
        alunoAdapter = new AlunoAdapter(listaAlunos);

        //Configurar o RV
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvAlunos.setLayoutManager(layoutManager);
        rvAlunos.setHasFixedSize(true);
        rvAlunos.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        rvAlunos.setAdapter(alunoAdapter);

    }

    @Override
    protected void onStart() {


        carregaListaAlunos();


        super.onStart();


    }
}
