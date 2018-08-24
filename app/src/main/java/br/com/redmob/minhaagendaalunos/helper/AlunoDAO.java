package br.com.redmob.minhaagendaalunos.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.redmob.minhaagendaalunos.Model.Aluno;
import br.com.redmob.minhaagendaalunos.dto.AlunoSync;

public class AlunoDAO {

    private SQLiteDatabase write;
    private SQLiteDatabase read;

    public AlunoDAO(Context context) {

        DBHelper dbHelper = new DBHelper(context);
        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    public static String geraUUID() {

        return UUID.randomUUID().toString();
    }


    public boolean salvar(Aluno aluno) {
        insereIdSeNecessario(aluno);
        ContentValues cv = pegaDadosAluno(aluno);
        write.insert(DBHelper.TABELA_ALUNOS, null, cv);

        return true;
    }

    private void insereIdSeNecessario(Aluno aluno) {
        if( aluno.getId() == null ){
            aluno.setId( geraUUID() );
        }
    }


    @NonNull
    private ContentValues pegaDadosAluno(Aluno aluno) {
        ContentValues cv = new ContentValues();
        cv.put("id", aluno.getId());
        cv.put("nome", aluno.getNome() );
        cv.put("endereco", aluno.getEndereco() );
        cv.put("telefone", aluno.getTelefone() );
        cv.put("site", aluno.getSite() );
        cv.put("nota", aluno.getNota() );
        return cv;
    }


    public boolean atualizar(Aluno aluno) {

        ContentValues cv = pegaDadosAluno(aluno);
        String[] args = {aluno.getId()};

        try{
            write.update(DBHelper.TABELA_ALUNOS, cv, "id=?", args);
        }catch (Exception e){

        }

        return true;
    }


    public boolean deletar(Aluno aluno) {

        String[] args = {aluno.getId().toString()};

        try {
            write.delete(DBHelper.TABELA_ALUNOS, "id=?", args);
        }catch (Exception e){
            return false;
        }

        return true;
    }


    public List<Aluno> listar() {

        List<Aluno> listaAlunos = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABELA_ALUNOS + " ;";
        Cursor c = read.rawQuery(sql, null);

        while( c.moveToNext() ){
            Aluno aluno = new Aluno();

            aluno.setId(c.getString(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getInt(c.getColumnIndex("nota")));

            listaAlunos.add(aluno);
        }

        return listaAlunos;
    }

}
