package br.edu.ifsp.scl.gerfinsdm2.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.activity.Categoria.CategoriaListaActivity
import br.edu.ifsp.scl.gerfinsdm2.activity.Conta.ContaListaActivity
import br.edu.ifsp.scl.gerfinsdm2.activity.Transacao.TransacaoListaActivity
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Conta
import br.edu.ifsp.scl.gerfinsdm2.model.Transacao
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var saldototal: TextView
    private lateinit var contas: MutableList<Conta>
    private lateinit var transacoes: MutableList<Transacao>
    private lateinit var daoContas: ContaSQLite
    private lateinit var daoTransacao: TransacaoSQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        daoContas = ContaSQLite(this)
        daoTransacao = TransacaoSQLite(this)
        saldototal = findViewById(R.id.tvTotalContas)

        var btnContas = findViewById<Button>(R.id.btnContas)
        var btnCategorias = findViewById<Button>(R.id.btnCategorias)
        var btnTransacoes = findViewById<Button>(R.id.btnTransacao)

        btnContas.setOnClickListener(this)
        btnCategorias.setOnClickListener(this)
        btnTransacoes.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        val f = NumberFormat.getCurrencyInstance(Locale("pt", "br"))

        // Cálculo do somatório de saldos das contas e exibição na TextView
        contas = daoContas.leiaConta()
        val saldototal = contas.sumByDouble { it.saldoinicial.toDouble() }
        tvTotalContas.text = f.format(saldototal)



        // Cálculo do saldo total das contas sem considerar transações futuras
        val dataFormatada = SimpleDateFormat("dd/MM/yyy")
        val hoje = Calendar.getInstance().time
        transacoes = daoTransacao.leiaTransacao()
        var saldo = 0.0
        for (t in transacoes){
            var dataTransacao: Date = dataFormatada.parse(t.data)
            if (dataTransacao > hoje){
                when (t.tipo) {
                    "Crédito" -> saldo += t.valor.toDouble()
                    "Débito"  -> saldo -= t.valor.toDouble()
                }
            }
        }
        val total = saldototal - saldo
        tvSaldoReal.text = f.format(total)
    }


    // Tratamento de clique nos botões da MainActivity
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContas -> startActivity(Intent(this, ContaListaActivity::class.java))
            R.id.btnCategorias -> startActivity(Intent(this, CategoriaListaActivity::class.java))
            R.id.btnTransacao -> startActivity(Intent(this, TransacaoListaActivity::class.java))
        }
    }

}
