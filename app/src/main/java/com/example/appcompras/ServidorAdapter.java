package com.example.appcompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServidorAdapter extends RecyclerView.Adapter<ServidorAdapter.ViewHolder> {

    private List<Servidor> servidores;

    public ServidorAdapter(List<Servidor> servidores) {
        this.servidores = servidores;
    }

    public void setServidores(List<Servidor> servidores) {
        this.servidores = servidores;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_servidor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servidor servidor = servidores.get(position);
        holder.tvIpServidor.setText(servidor.getDireccionIp());
        holder.tvTipoServidor.setText(servidor.getTipoServidor());

        String recursos = "CPU: " + servidor.getProcesador() +
                " | RAM: " + servidor.getRam() +
                " | Disco: " + servidor.getAlmacenamiento();
        holder.tvRecursos.setText(recursos);

        holder.tvArea.setText("Área: " + servidor.getArea());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditarServidorActivity.class);
            intent.putExtra("idDocumento", servidor.getIdDocumento());
            intent.putExtra("tipoServidor", servidor.getTipoServidor());
            intent.putExtra("procesador", servidor.getProcesador());
            intent.putExtra("ram", servidor.getRam());
            intent.putExtra("almacenamiento", servidor.getAlmacenamiento());
            intent.putExtra("direccionIp", servidor.getDireccionIp());
            intent.putExtra("area", servidor.getArea());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return servidores != null ? servidores.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIpServidor;
        TextView tvTipoServidor;
        TextView tvRecursos;
        TextView tvArea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIpServidor = itemView.findViewById(R.id.tvIpServidor);
            tvTipoServidor = itemView.findViewById(R.id.tvTipoServidor);
            tvRecursos = itemView.findViewById(R.id.tvRecursos);
            tvArea = itemView.findViewById(R.id.tvArea);
        }
    }
}
