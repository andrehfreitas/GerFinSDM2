package br.edu.ifsp.scl.gerfinsdm2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.activity.Categoria.CategoriaDetalheActivity
import br.edu.ifsp.scl.gerfinsdm2.model.Categoria
import kotlinx.android.synthetic.main.categoria_item.view.*

class CategoriaAdapter(private val context: Context, private var categoriaList: List<Categoria>):
    RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val textViewNome = itemView.tvNomeCategoria

        fun bindView(categoria: Categoria) {
            textViewNome.text = categoria.nome

            // Tratamento do evento de click em um item da lista
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, CategoriaDetalheActivity::class.java)
                intent.putExtra("categoria", categoria)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.categoria_item, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun getItemCount() = categoriaList.size

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bindView(categoriaList[position])
    }
}
