package com.david.fixittecnic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AvisosAdapter extends RecyclerView.Adapter<AvisosAdapter.AvisoViewHolder> {

    private List<Aviso> listaAvisos;

    public AvisosAdapter(List<Aviso> listaAvisos) {
        this.listaAvisos = listaAvisos;
    }

    @NonNull
    @Override
    public AvisoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aviso, parent, false);
        return new AvisoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvisoViewHolder holder, int position) {
        Aviso aviso = listaAvisos.get(position);

        // 1. Extraer datos anidados con cuidado por si vienen nulos
        String nombreCliente = (aviso.getCliente() != null && aviso.getCliente().getNombre() != null)
                ? aviso.getCliente().getNombre() : "Cliente Desconocido";

        String direccionCliente = (aviso.getCliente() != null && aviso.getCliente().getDireccion() != null)
                ? aviso.getCliente().getDireccion() : "Dirección no disponible";

        String nombreCategoria = (aviso.getCategoria() != null && aviso.getCategoria().getNombre() != null)
                ? aviso.getCategoria().getNombre().toUpperCase() : "GENERAL";

        // 2. 🔥 LÓGICA DE FECHA Y HORA MEJORADA 🔥
        String textoFechaHora = "--:--";
        if (aviso.getFechaCreacion() != null && aviso.getFechaCreacion().contains("T")) {
            try {
                String[] partes = aviso.getFechaCreacion().split("T");
                String fechaBD = partes[0];
                String horaBD = partes[1].substring(0, 5);

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                String fechaHoy = sdf.format(new java.util.Date());

                if (fechaBD.equals(fechaHoy)) {
                    textoFechaHora = "Hoy " + horaBD;
                } else {
                    String[] fechaPartes = fechaBD.split("-");
                    String diaMes = fechaPartes[2] + "/" + fechaPartes[1];
                    textoFechaHora = diaMes + " " + horaBD;
                }
            } catch (Exception e) {
                textoFechaHora = "00:00";
            }
        }

        // 3. Rellenar los textos
        holder.txtCliente.setText(nombreCliente);
        holder.txtDescripcion.setText(aviso.getDescripcion());
        holder.txtDireccion.setText(direccionCliente);
        holder.txtHora.setText(textoFechaHora);
        holder.txtNombreCategoria.setText(nombreCategoria);

        // 4. Magia de Colores: Categorías Blindadas
        String catNormalizada = nombreCategoria.toUpperCase();

        if (catNormalizada.contains("FONTANER")) {
            holder.contenedorIcono.setBackgroundResource(R.drawable.bg_icono_fontaneria);
            holder.imgCategoria.setImageResource(R.drawable.ic_gota);

        } else if (catNormalizada.contains("ELECTRIC")) {
            holder.contenedorIcono.setBackgroundResource(R.drawable.bg_icono_electricidad);
            holder.imgCategoria.setImageResource(R.drawable.ic_rayo);

        } else if (catNormalizada.contains("ASCENSOR")) {
            holder.contenedorIcono.setBackgroundResource(R.drawable.bg_icono_ascensores);
            holder.imgCategoria.setImageResource(R.drawable.ic_ajustes);

        } else {
            holder.contenedorIcono.setBackgroundResource(R.drawable.bg_icono_fontaneria);
            holder.imgCategoria.setImageResource(R.drawable.ic_portapapeles);
        }

        // 5. Magia de Colores: Prioridad (SOLO UNA VEZ)
        String prioridad = aviso.getPrioridad() != null ? aviso.getPrioridad().toUpperCase() : "MEDIA";
        holder.txtPrioridad.setText(prioridad);

        if (prioridad.equals("ALTA")) {
            holder.txtPrioridad.setBackgroundResource(R.drawable.bg_badge_alta);
        } else if (prioridad.equals("MEDIA")) {
            holder.txtPrioridad.setBackgroundResource(R.drawable.bg_badge_media);
        } else {
            holder.txtPrioridad.setBackgroundResource(R.drawable.bg_badge_baja);
        }

        // 🔥 EL TRUCO NINJA: Congelamos la variable para que Java no llore en el clic 🔥
        final String fechaFinalParaIntent = textoFechaHora;

        // 🔥 AQUÍ VA EL CLIC 🔥
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), AvisoDetalleActivity.class);

            intent.putExtra("CATEGORIA", nombreCategoria);
            intent.putExtra("CLIENTE", nombreCliente);
            intent.putExtra("PRIORIDAD", prioridad);
            intent.putExtra("DESCRIPCION", aviso.getDescripcion());
            intent.putExtra("DIRECCION", direccionCliente);

            String telefono = (aviso.getCliente() != null && aviso.getCliente().getTelefono() != null)
                    ? aviso.getCliente().getTelefono() : "+34 000 000 000";
            intent.putExtra("TELEFONO", telefono);

            // Usamos la variable congelada
            intent.putExtra("FECHA", fechaFinalParaIntent);
            intent.putExtra("ESTADO", aviso.getEstado() != null ? aviso.getEstado().toUpperCase() : "PENDIENTE");

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaAvisos.size();
    }

    public static class AvisoViewHolder extends RecyclerView.ViewHolder {
        TextView txtCliente, txtDescripcion, txtPrioridad, txtDireccion, txtHora, txtNombreCategoria;
        FrameLayout contenedorIcono;
        ImageView imgCategoria;

        public AvisoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtPrioridad = itemView.findViewById(R.id.txtPrioridad);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtHora = itemView.findViewById(R.id.txtHora);
            txtNombreCategoria = itemView.findViewById(R.id.txtNombreCategoria);
            contenedorIcono = itemView.findViewById(R.id.contenedorIcono);
            imgCategoria = itemView.findViewById(R.id.imgCategoria);
        }
    }
}