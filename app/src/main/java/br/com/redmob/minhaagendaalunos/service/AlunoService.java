package br.com.redmob.minhaagendaalunos.service;

import br.com.redmob.minhaagendaalunos.Model.Aluno;
import br.com.redmob.minhaagendaalunos.dto.AlunoSync;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AlunoService {

    @POST("aluno")
    Call<Void> insere(@Body Aluno aluno);

    @GET("aluno")
    Call<AlunoSync> lista();

    @DELETE("aluno/{id}")
    Call<Void> deleta(@Path("id") String id);


}
