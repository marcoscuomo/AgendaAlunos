package br.com.redmob.minhaagendaalunos.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import br.com.redmob.minhaagendaalunos.Model.Aluno;
import br.com.redmob.minhaagendaalunos.R;
import br.com.redmob.minhaagendaalunos.helper.AlunoDAO;
import br.com.redmob.minhaagendaalunos.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAlunoActivity extends AppCompatActivity {

    //Atributos
    private EditText editNome;
    private EditText editEndereco;
    private EditText editTelefone;
    private EditText editSite;
    private EditText editNota;
    private Aluno alunoClicado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_aluno);

        //FindViewByIds
        editNome     = findViewById(R.id.addAluno_editNome);
        editEndereco = findViewById(R.id.addAluno_editEndereco);
        editTelefone = findViewById(R.id.addAluno_editTelefone);
        editSite     = findViewById(R.id.addAluno_editSite);
        editNota     = findViewById(R.id.addAluno_editNota);

        //Recuperando o aluno clicado para atualizacao
        alunoClicado = (Aluno) getIntent().getSerializableExtra("alunoClicado");

        //Setando os campos caso seja para atualizcao
        if(alunoClicado != null){
            editNome.setText(alunoClicado.getNome());
            editEndereco.setText(alunoClicado.getEndereco());
            editTelefone.setText(alunoClicado.getTelefone());
            editSite.setText(alunoClicado.getSite());
            editNota.setText(String.valueOf(alunoClicado.getNota()));
        }

    }


    //Criando menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_aluno, menu);

        return super.onCreateOptionsMenu(menu);

    }

    //Capturando o item clicado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menu_add_aluno_itemSalvar:

                //Instanciando um AlunoDAO
                AlunoDAO alunoDAO = new AlunoDAO(getApplicationContext());

                if(alunoClicado != null ){
                    atualizaAluno(alunoDAO);
                }else{
                    salvaAluno(alunoDAO);
                }

                Aluno aluno = pegaAluno();

                Call<Void> call = new RetrofitInicializador().getAlunoService().insere(aluno);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.i("OnResponse", "Requisicao com sucesso ");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("OnResponse", t.getMessage());
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void salvaAluno(AlunoDAO alunoDAO) {

        //Pegando os valores dos Texts
        String nomeAluno = editNome.getText().toString();
        String enderecoAluno = editEndereco.getText().toString();
        String telefoneAluno = editTelefone.getText().toString();
        String siteAluno = editSite.getText().toString();
        int notaAluno = Integer.parseInt(editNota.getText().toString());

        //Instacioando um Objeto Aluno
        Aluno aluno = new Aluno();

        //Setando valores no objeto aluno
        aluno.setNome(nomeAluno);
        aluno.setEndereco(enderecoAluno);
        aluno.setTelefone(telefoneAluno);
        aluno.setSite(siteAluno);
        aluno.setNota(notaAluno);

        //Chamando o metodo salvar
        alunoDAO.salvar(aluno);
        finish();
        Toast.makeText(this, "O Aluno " + aluno.getNome() +
                " foi salvo com sucesso " + aluno.getId(), Toast.LENGTH_LONG).show();
    }

    private void atualizaAluno(AlunoDAO alunoDAO) {
        /*ATUALIZANDO*/
        Aluno aluno = getAluno();


        //Chamando o metodo para atualiza
        alunoDAO.atualizar(aluno);
        Toast.makeText(AddAlunoActivity.this, "Aluno " + aluno.getNome()
                + " atualizado com sucesso", Toast.LENGTH_LONG).show();
        finish();
    }

    @NonNull
    private Aluno getAluno() {
        //Pegando os valores dos Texts
        String nomeAluno = editNome.getText().toString();
        String enderecoAluno = editEndereco.getText().toString();
        String telefoneAluno = editTelefone.getText().toString();
        String siteAluno = editSite.getText().toString();
        int notaAluno = Integer.parseInt(editNota.getText().toString());

        //Instanciando um novo Aluno
        Aluno aluno = new Aluno();

        //Setando os valores no objeto aluno
        aluno.setId(alunoClicado.getId());
        aluno.setNome(nomeAluno);
        aluno.setEndereco(enderecoAluno);
        aluno.setTelefone(telefoneAluno);
        aluno.setSite(siteAluno);
        aluno.setNota(notaAluno);
        return aluno;
    }

    public Aluno pegaAluno() {
        Aluno aluno = new Aluno();
        aluno.setNome(editNome.getText().toString());
        aluno.setEndereco(editEndereco.getText().toString());
        aluno.setTelefone(editTelefone.getText().toString());
        aluno.setSite(editSite.getText().toString());
        aluno.setNota(Integer.parseInt(editNota.getText().toString()));
        //aluno.setCaminhoFoto((String) campoFoto.getTag());
        return aluno;
    }


}
