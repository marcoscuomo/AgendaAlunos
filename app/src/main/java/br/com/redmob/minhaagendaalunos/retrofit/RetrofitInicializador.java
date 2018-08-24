package br.com.redmob.minhaagendaalunos.retrofit;

import br.com.redmob.minhaagendaalunos.service.AlunoService;
import br.com.redmob.minhaagendaalunos.service.DispositivoService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitInicializador {

    private final Retrofit retrofit;

    public static final String BASE_URL = "http://192.168.0.28:8080/api/";

    public RetrofitInicializador() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(interceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();
    }

    public AlunoService getAlunoService() {

        return retrofit.create(AlunoService.class);
    }

    public DispositivoService getDispositivoService() {

        return retrofit.create(DispositivoService.class);
    }
}
