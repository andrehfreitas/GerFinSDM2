package br.edu.ifsp.scl.gerfinsdm2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.activity.Transacao.TransacaoDetalheActivity
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Transacao
import kotlinx.android.synthetic.main.transacao_item.view.*
import java.text.NumberFormat
import java.util.*

class TransacaoAdapter(private val context: Context,
                       private var transacaoList: List<Transacao>):
    RecyclerView.Adapter<TransacaoAdapter.TransacaoViewHolder>(){
    companion object {
        var transacoes: ArrayList<Transacao> = ArrayList()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            TransacaoViewHolder{
        //Inflando view onde serão setados as informações das Transacões cadastradas
        val view = LayoutInflater.from(context).inflate(R.layout.transacao_item, parent, false)
        return TransacaoViewHolder(view)
    }

    override fun getItemCount() = transacaoList.size


    fun notifyAdapter() {
        transacoes.sortBy { it.data }
        this.notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: TransacaoViewHolder, position: Int){
        holder.bindView(transacaoList[position])
    }

    class TransacaoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val f = NumberFormat.getCurrencyInstance(Locale("pt", "br"))

        val imageViewTransacaoTipo: ImageView = itemView.transacao_imagem
        val textViewTransacaoConta = itemView.transacao_conta
        val textViewTransacaoCategoria = itemView.transacao_categoria
        val textViewTransacaoData = itemView.transacao_data
        val textViewTransacaoValor = itemView.transacao_valor

        fun bindView(transacao: Transacao){

            val daoConta = ContaSQLite(itemView.context)
            val nomeConta = daoConta.leiaContaId(transacao.conta).nome

            val daoCategoria = CategoriaSQLite(itemView.context)
            val nomeCategoria = daoCategoria.leiaCategoriaId(transacao.categoria).nome

            textViewTransacaoConta.text = "$nomeConta"
            textViewTransacaoCategoria.text = "$nomeCategoria"
            textViewTransacaoData.text = transacao.data
            textViewTransacaoValor.text = f.format(transacao.valor.toDouble())

            val tipo = transacao.tipo

            if (tipo == "Débito"){
                imageViewTransacaoTipo.setImageResource(R.drawable.icone_transacao_item_despesa)
            } else{
                imageViewTransacaoTipo.setImageResource(R.drawable.icone_transacao_item_receita)
            }

            // Tratamento do evento de click em um item da lista
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, TransacaoDetalheActivity::class.java)
                intent.putExtra("transacao", transacao)
                itemView.context.startActivity(intent)
            }
        }
    }
}