package br.edu.ifsp.scl.gerfinsdm2.activity.Transacao

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.adapter.TransacaoAdapter
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Transacao
import kotlinx.android.synthetic.main.activity_listatransacao.*
import kotlinx.android.synthetic.main.consulta_transacao.*
import kotlinx.android.synthetic.main.consulta_transacao.view.*
import kotlinx.android.synthetic.main.content_activity_listatransacao.*
import java.util.*

class TransacaoListaActivity : AppCompatActivity() {

    companion object{
        lateinit var transacaoAdapter: TransacaoAdapter
    }

    private lateinit var dao: TransacaoSQLite
    private lateinit var daoConta: ContaSQLite
    private lateinit var daoCategoria: CategoriaSQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listatransacao)
        setSupportActionBar(toolbar3)

        dao = TransacaoSQLite(this)

        // Evento de clique no Floating Action Button
        fabtransacoes.setOnClickListener { view ->
            val i = Intent(applicationContext, TransacaoCadastroActivity::class.java)
            startActivityForResult(i, 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_consulta, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_consultar -> {
                janelaBusca()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()

        //Criar a lista de transações na activity
        val listaTransacoes = dao.leiaTransacao()
        transacaoAdapter = TransacaoAdapter(this, listaTransacoes)
        recyclerViewTransacoes.adapter = transacaoAdapter
        recyclerViewTransacoes.layoutManager = LinearLayoutManager(this)
        recyclerViewTransacoes.smoothScrollToPosition(listaTransacoes.size)

    }


    fun janelaBusca() {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.consulta_transacao, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        val c = Calendar.getInstance()

        daoConta = ContaSQLite(this)
        daoCategoria = CategoriaSQLite(this)

        val listaNomeConta = daoConta.leiaNomeConta()
        listaNomeConta.add(0, "Selecione a conta")
        val listaNomeCategoria = daoCategoria.leiaNomeCategoria()
        listaNomeCategoria.add(0, "Selecione a categoria")

        //Carrega os spinner com as informações do banco
        mAlertDialog.spnAlertaConta.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaNomeConta)
        mAlertDialog.spnAlertaCategoria.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaNomeCategoria)

        calendario(c, mAlertDialog.etAlertaDataInicio)
        calendario(c, mAlertDialog.etAlertaDataFim)

        mDialogView.btnCancelar.setOnClickListener {
            //Fecha a janela
            mAlertDialog.dismiss()
        }

        mDialogView.btnBuscar.setOnClickListener {
            //Realiza a busca

            var tipoConta = mAlertDialog.spnAlertaConta.selectedItem.toString()
            var dataInicio = mAlertDialog.etAlertaDataInicio.text.toString()
            var dataFim = mAlertDialog.etAlertaDataFim.text.toString()
            var opTransacao = ""
            var tipoCategoria = mAlertDialog.spnAlertaCategoria.selectedItem.toString()

            if (mAlertDialog.rbCredito.isChecked) {
                opTransacao = mAlertDialog.rbCredito.text.toString()
            } else if (mAlertDialog.rbDebito.isChecked) {
                opTransacao = mAlertDialog.rbDebito.text.toString()
            } else if (mAlertDialog.rbCreditoDebito.isChecked) {
                opTransacao = mAlertDialog.rbCreditoDebito.text.toString()
            }
            //Recupero o id de cada campo
            var idConta = daoConta.leiaContaNome(tipoConta)
            var idCategoria = daoCategoria.leiaCategoriaNome(tipoCategoria)

            //Crio um objeto com novo valores
            var transacao =
                Transacao(0, opTransacao, idConta.id_, idCategoria.id_, dataInicio, "", "", dataFim)

            //Carrega a view com os valores da busca
            val listaTransacoes = dao.buscaTransacao(transacao)
            transacaoAdapter = TransacaoAdapter(this, listaTransacoes)
            recyclerViewTransacoes.adapter = transacaoAdapter
            recyclerViewTransacoes.layoutManager = LinearLayoutManager(this)
            recyclerViewTransacoes.smoothScrollToPosition(listaTransacoes.size)

            Log.d(
                "Resultado", "Conta: " + tipoConta + ", Início: " + dataInicio
                        + ", Fim: " + dataFim + ", Tipo transacao: " + opTransacao + ", Categoria: " + tipoCategoria
            )
            mAlertDialog.dismiss()
        }
    }


    // Mostra um calendário na tela
    fun calendario(c: Calendar, inputData: TextView) {

        val ano = c.get(Calendar.YEAR)
        val mes = c.get(Calendar.MONTH)
        val dia = c.get(Calendar.DAY_OF_MONTH)

        inputData.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { datePicker, mAno, mMes, mDia ->
                    inputData.setText(mDia.toString() + "/" + (mMes + 1).toString() + "/" + mAno.toString())
                }, ano, mes, dia
            )
            dpd.show()
        }
    }
}







