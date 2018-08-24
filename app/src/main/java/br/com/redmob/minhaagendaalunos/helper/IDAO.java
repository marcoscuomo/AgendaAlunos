package br.com.redmob.minhaagendaalunos.helper;

import java.util.List;
import br.com.redmob.minhaagendaalunos.Model.Aluno;

public interface IDAO {

    public boolean salvar(Aluno aluno);
    public boolean atualizar(Aluno aluno);
    public boolean deletar(Aluno aluno);
    public List<Aluno> listar();

}
