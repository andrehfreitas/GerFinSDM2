package br.edu.ifsp.scl.gerfinsdm2.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.activity.Conta.ContaDetalheActivity
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Conta
import kotlinx.android.synthetic.main.conta_item.view.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class ContaAdapter(private val context: Context, private var contaList: List<Conta>):
    RecyclerView.Adapter<ContaAdapter.ContaViewHolder>() {

    class ContaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        //Criação de variável para formatação dos dados em moeda local
        val f = NumberFormat.getCurrencyInstance(Locale("pt", "br"))

        val textViewNome = itemView.tvNomeConta
        val textViewSaldo = itemView.tvSaldoConta

        fun bindView(conta: Conta) {
            textViewSaldo.text = f.format(conta.saldoinicial.toDouble())
            textViewNome.text = conta.nome

            // Tratamento do evento de click em um item da lista
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, ContaDetalheActivity::class.java)
                intent.putExtra("conta", conta)
                itemView.context.startActivity(intent)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.conta_item, parent, false)
        return ContaViewHolder(view)
    }


    override fun getItemCount() = contaList.size

    override fun onBindViewHolder(holder: ContaViewHolder, position: Int) {
        holder.bindView(contaList[position])
    }
}
