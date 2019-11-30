package br.edu.ifsp.scl.gerfinsdm2.activity

/*
    Trabalho final da disciplina Programação para Dispositivos Android 1

    Alunos:
            André Henrique de Freitas - SC3009408
            Diego Maroldi Barreiros - SC3009262


   OBSERVAÇÃO: O app poderia ser melhorado através de refatoração do código,
   eliminando respetições e reduzindo código.
 */


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.gerfinsdm2.Funcoes.FormataMeuValor
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.activity.Categoria.CategoriaListaActivity
import br.edu.ifsp.scl.gerfinsdm2.activity.Conta.ContaListaActivity
import br.edu.ifsp.scl.gerfinsdm2.activity.Transacao.TransacaoListaActivity
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Conta
import br.edu.ifsp.scl.gerfinsdm2.model.Transacao
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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

    //atributos do Gráfico
    private lateinit var barChat: BarChart
    private lateinit var tvMes: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        daoContas = ContaSQLite(this)
        daoTransacao = TransacaoSQLite(this)
        saldototal = findViewById(R.id.tvTotalContas)

        var btnContas = findViewById<Button>(R.id.btnContas)
        var btnCategorias = findViewById<Button>(R.id.btnCategorias)
        var btnTransacoes = findViewById<Button>(R.id.btnTransacao)

        //findViewById Gráfico
        barChat = findViewById(R.id.grafico)
        tvMes = findViewById(R.id.tvMes)

        btnContas.setOnClickListener(this)
        btnCategorias.setOnClickListener(this)
        btnTransacoes.setOnClickListener(this)
    }


    override fun onResume() {
        super.onResume()

        //Chama função para criar gráfico
        criaGrafico()


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


        // Formata cor do texto verificando se o saldo é positivo ou negativo
        if (total < 0.0) tvSaldoReal.setTextColor(Color.RED)
        else tvSaldoReal.setTextColor(Color.BLUE)

        // Formata cor do texto verificando se o saldo é positivo ou negativo
        if (total < 0.0) tvTotalContas.setTextColor(Color.RED)
        else tvTotalContas.setTextColor(Color.BLUE)
    }


    // Tratamento de clique nos botões da MainActivity
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContas -> startActivity(Intent(this, ContaListaActivity::class.java))
            R.id.btnCategorias -> startActivity(Intent(this, CategoriaListaActivity::class.java))
            R.id.btnTransacao -> startActivity(Intent(this, TransacaoListaActivity::class.java))
        }
    }


    //Função para criar o gráfico
    fun criaGrafico(){

        // Formata a data para mes
        var formataParaMes = SimpleDateFormat("MM")
        var data = Date()
        var formatoMes = formataParaMes.format(data)

        //Meses do ano
        val mes = mapOf<String, String>(
            "01" to "Janeiro",
            "02" to "Fevereiro",
            "03" to "Março",
            "04" to "Abril",
            "05" to "Maio",
            "06" to "Junho",
            "07" to "Julho",
            "08" to "Agosto",
            "09" to "Setembro",
            "10" to "Outubro",
            "11" to "Novembro",
            "12" to "Dezembro"
        )

        tvMes.text = "${mes.get(formatoMes)}: crédito e débito"

        var valorCredito = daoTransacao.buscaValorNoMes("Crédito", formatoMes)
        var valorDebito = daoTransacao.buscaValorNoMes("Débito", formatoMes)


        // Valor do Saldocrédito
        var barValorCredito = java.util.ArrayList<BarEntry>()
        barValorCredito.add(BarEntry(0f, valorCredito))


        // Valor do débito
        var barValorDebito = java.util.ArrayList<BarEntry>()
        barValorDebito.add(BarEntry(1f, valorDebito))


        // Define os valores no gráfico com legenda
        var barDataSetValorCredito = BarDataSet(barValorCredito, "Crédito")
        var barDataSetValorDebito = BarDataSet(barValorDebito, "Débito")

        // Define cor
        barDataSetValorCredito.setColor(Color.BLUE)
        barDataSetValorDebito.setColor(Color.RED)


        var barData = BarData(barDataSetValorCredito, barDataSetValorDebito)
        barData.setValueFormatter(FormataMeuValor())

        // Configuração de formatação e exibição
        barChat.axisLeft.valueFormatter = FormataMeuValor()
        barChat.axisLeft.axisMinimum = 0f
        barChat.xAxis.isEnabled = false
        barChat.setDrawGridBackground(false)
        barChat.axisRight.isEnabled = false
        barChat.description.isEnabled = false
        barChat.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChat.data = barData
        barChat.invalidate()
    }
}
