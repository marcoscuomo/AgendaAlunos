package br.com.redmob.minhaagendaalunos.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import br.com.redmob.minhaagendaalunos.Model.Aluno;
import br.com.redmob.minhaagendaalunos.R;

public class AlunoAdapter extends RecyclerView.Adapter<AlunoAdapter.MyViewHolder> {

    private List<Aluno> listaAlunos;

    public AlunoAdapter(List<Aluno> aluno) {

        this.listaAlunos = aluno;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemLista = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lista_aluno_adapter, viewGroup, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Aluno aluno = listaAlunos.get(i);
        holder.textNome.setText(aluno.getNome());
        holder.textTelefone.setText(aluno.getTelefone());

    }

    @Override
    public int getItemCount() {
        return this.listaAlunos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textNome;
        TextView textTelefone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textNome = itemView.findViewById(R.id.lista_aluno_textNome);
            textTelefone = itemView.findViewById(R.id.lista_aluno_textTelefone);

        }
    }
}
