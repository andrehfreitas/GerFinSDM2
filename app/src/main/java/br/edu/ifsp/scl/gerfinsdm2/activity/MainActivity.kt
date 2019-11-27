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
import br.edu.ifsp.scl.gerfinsdm2.model.Conta
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var saldototal: TextView
    private lateinit var contas: MutableList<Conta>
    private lateinit var dao: ContaSQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dao = ContaSQLite(this)
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

        //Criação de variável para formatação dos dados em moeda local
        val f = NumberFormat.getCurrencyInstance(Locale("pt", "br"))

        // Cálculo do somatório de saldos das contas e exibição na TextView
        contas = dao.leiaConta()
        val saldototal = contas.sumByDouble { it.saldoinicial.toDouble() }
        tvTotalContas.text = f.format(saldototal)
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
