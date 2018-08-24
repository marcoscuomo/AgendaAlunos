package br.com.redmob.minhaagendaalunos.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.redmob.minhaagendaalunos.Model.Aluno;
import br.com.redmob.minhaagendaalunos.activity.MainActivity;

public class DBHelper extends SQLiteOpenHelper {

    public static int VERSION = 4;
    public static String NOME_BD = "bdTarefas";
    public static String TABELA_ALUNOS = "tabelaAlunos";

    public DBHelper(Context context) {
            super(context, NOME_BD, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Query de criacao da tabela alunos
        String sql = "CREATE TABLE IF NOT EXISTS " + TABELA_ALUNOS +
                "(id CHAR(36) PRIMARY KEY, " +
                "nome TEXT NOT NULL, " +
                "endereco TEXT, " +
                "telefone TEXT, " +
                "site TEXT, " +
                "nota INT);";
        
        try{

            db.execSQL(sql);
            Log.i("INFO DB", "Sucesso ao criar a tabela " );
            
        }catch (Exception e){
            Log.i("INFO DB", "Erro ao criar a tabela " );
        }
        
        

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion){
            case 1:
            case 2:
                //Alterando o tipo de dados da coluna id
                //Criando uma tabela nova
                String criandoNovaTable = "CREATE TABLE alunos_novos " +
                        "(id CHAR(36) PRIMARY KEY, " +
                        "nome TEXT NOT NULL, " +
                        "endereco TEXT, " +
                        "telefone TEXT, " +
                        "site TEXT, " +
                        "nota INT); ";
                db.execSQL(criandoNovaTable);


                //Passando os dados da tabela antiga para a nova
                String inserindoAlunosNaTabelaNova = "INSERT INTO alunos_novos " +
                        "(id, nome, endereco, telefone, nota) " +
                        "SELECT id, nome, endereco, telefone, nota FROM " + TABELA_ALUNOS;
                db.execSQL(inserindoAlunosNaTabelaNova);

                //Removendo a abela antiga
                String removendoTabelaAntiga = "DROP TABLE " + TABELA_ALUNOS;
                db.execSQL(removendoTabelaAntiga);

                //Alterando o nome da tabela nova
                String alterandoNomeTabelaNova = "ALTER TABLE alunos_novos RENAME TO " + TABELA_ALUNOS;
                db.execSQL(alterandoNomeTabelaNova);

            case 3:
                String buscaAlunos = "SELECT * FROM " + TABELA_ALUNOS;
                Cursor c = db.rawQuery(buscaAlunos, null);
                List<Aluno> alunos = populaAlunos(c);
                String atualizaIdAluno = "UPDATE " + TABELA_ALUNOS + " SET id=? WHERE id=?";

                for(Aluno aluno: alunos){
                    db.execSQL(atualizaIdAluno, new String[] {geraUUID(), aluno.getId()});
                }


        }

    }

    public static String geraUUID() {

        return UUID.randomUUID().toString();

    }


    private ContentValues pegaDadosDoAluno(Aluno aluno) {
        ContentValues cv = new ContentValues();
        cv.put("id", aluno.getId());
        cv.put("nome", aluno.getNome());
        cv.put("endereco", aluno.getEndereco());
        cv.put("telefone", aluno.getTelefone());
        cv.put("site", aluno.getSite());
        cv.put("nota", aluno.getNota());
        return cv;
    }

    private List<Aluno> populaAlunos(Cursor c) {
        List<Aluno> alunos = new ArrayList<Aluno>();
        while (c.moveToNext()) {
            Aluno aluno = new Aluno();
            aluno.setId(c.getString(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getInt(c.getColumnIndex("nota")));

            alunos.add(aluno);
        }
        return alunos;
    }


    public void sincroniza(List<Aluno> alunos, Context context) {

        AlunoDAO dao = new AlunoDAO(context);

        for (Aluno aluno :
                alunos) {
            if (existe(aluno) ) {
                dao.atualizar(aluno);
            }else{
                dao.salvar(aluno);
            }
        }

    }

    private boolean existe(Aluno aluno) {

        SQLiteDatabase read = getWritableDatabase();

        String sql = "SELECT id FROM " + DBHelper.TABELA_ALUNOS + " WHERE id=? LIMIT 1";
        Cursor c = read.rawQuery(sql, new String[] {aluno.getId()});
        int qtd = c.getCount();

        if(read.isOpen()){
            read.close();
        }

        if(!c.isClosed()){
            c.close();
        }

        return qtd > 0;
    }
}
