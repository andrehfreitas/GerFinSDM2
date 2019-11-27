package br.edu.ifsp.scl.gerfinsdm2.activity.Transacao

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Categoria
import br.edu.ifsp.scl.gerfinsdm2.model.Conta
import br.edu.ifsp.scl.gerfinsdm2.model.Transacao
import kotlinx.android.synthetic.main.activity_cadtransacoes.*
import kotlinx.android.synthetic.main.activity_detalhe_conta.*
import kotlinx.android.synthetic.main.activity_detalhe_transacao.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class TransacaoDetalheActivity: AppCompatActivity() {

    private lateinit var listaNomeContas: ArrayList<String>
    private lateinit var listaNomeCategorias: ArrayList<String>
    private lateinit var listaContas: ArrayList<Conta>
    private lateinit var listaCategorias: ArrayList<Categoria>
    private lateinit var transacao: Transacao
    private lateinit var daoTransacao: TransacaoSQLite
    private lateinit var daoConta: ContaSQLite
    private lateinit var daoCategorias: CategoriaSQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_transacao)

        daoTransacao = TransacaoSQLite(this)
        daoConta = ContaSQLite(this)
        daoCategorias = CategoriaSQLite(this)

        listaContas = daoConta.leiaConta()
        listaCategorias = daoCategorias.leiaCategoria()

        listaNomeContas = daoConta.leiaNomeConta()
        listaNomeCategorias = daoCategorias.leiaNomeCategoria()

        // Preenchendo Spinner com o nome das Contas
        val spinnerConta = spnDetalheContaTransacao
        val arrayAdapterConta =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaNomeContas)
        spinnerConta.adapter = arrayAdapterConta

        // Preenchendo Spinner com o nome das Categorias
        val spinnerCategoria = spnDetalheCategoriaTransacao
        val arrayAdapterCategoria =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaNomeCategorias)
        spinnerCategoria.adapter = arrayAdapterCategoria


        // Chamada da activity de atualização e preenchimento das view com dados do banco
        if (intent.hasExtra("transacao")) {
            this.transacao = intent.getParcelableExtra("transacao") as Transacao

            val f = NumberFormat.getCurrencyInstance(Locale("pt", "br"))

            val tipo = this.findViewById<Spinner>(R.id.spnDetalheTipoTransacao)
            val conta = this.findViewById<Spinner>(R.id.spnDetalheContaTransacao)
            val categoria = this.findViewById<Spinner>(R.id.spnDetalheCategoriaTransacao)
            val data = this.findViewById<EditText>(R.id.etDetalheDataTransacao)
            val valor = this.findViewById<EditText>(R.id.etDetalheValorTransacao)
            val descricao = this.findViewById<EditText>(R.id.etDetalheDescricaoTransacao)

            tipo.setSelection(resources.getStringArray(R.array.tipo).indexOf(transacao.tipo))
            conta.setSelection(arrayAdapterConta.getPosition(daoConta.leiaContaId(transacao.conta).toString()))
            categoria.setSelection(arrayAdapterCategoria.getPosition(daoCategorias.leiaCategoriaId(transacao.categoria).toString()))
            data.setText(transacao.data)
            valor.setText(transacao.valor)
            descricao.setText(transacao.descricao)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detalhe, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Clique no ícone para salvar os dados
            R.id.action_salvar -> {
                //Capturando os dados informados pelo usuário
                val tipo = spnDetalheTipoTransacao.selectedItem.toString()
                val nomeConta = spnDetalheContaTransacao.selectedItem.toString()
                val nomeCategoria = spnDetalheCategoriaTransacao.selectedItem.toString()
                val data = etDetalheDataTransacao.text.toString()
                val valor = etDetalheValorTransacao.text.toString()
                val descricao = etDetalheDescricaoTransacao.text.toString()


                if (etDetalheValorTransacao.text.isNotEmpty() && etDetalheDataTransacao.text.isNotEmpty()) {

                    var categoria = 0
                    for (cat in listaCategorias) {
                        if (cat.nome == nomeCategoria) {
                            // Recebe Id da categoria para armazenar na tabela de transação
                            categoria = cat.id_
                        }
                    }

                    val t = Transacao()
                    t.id_ = transacao.id_
                    t.tipo = tipo
                    t.categoria = categoria
                    t.data = data
                    t.valor = valor
                    t.descricao = descricao


                    for (c in listaContas) {
                        if (c.nome == nomeConta) {
                            t.conta = c.id_
                            var saldo = c.saldoinicial.toDouble()
                            if (t.tipo == transacao.tipo) {
                                when (t.tipo) {
                                    "Crédito" -> {
                                        saldo += t.valor.toDouble()
                                        saldo -= transacao.valor.toDouble()
                                    }
                                    "Débito" -> {
                                        saldo -= t.valor.toDouble()
                                        saldo += transacao.valor.toDouble()
                                    }
                                }
                            } else {
                                when (t.tipo) {
                                    "Débito" -> {
                                        saldo -= t.valor.toDouble()
                                        saldo -= transacao.valor.toDouble()
                                    }
                                    "Crédito" -> {
                                        saldo += t.valor.toDouble()
                                        saldo += transacao.valor.toDouble()
                                    }
                                }
                            }
                            c.saldoinicial = saldo.toString()
                            daoConta.atualizaConta(c)
                            break
                        }
                    }

                    daoTransacao.atualizaTransacao(t)
                    Toast.makeText(this, "Transação atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()

                } else {
                    //Caso usuário não tenha preenchido o valor da transação é apresentado consulta_transacao
                    etDetalheValorTransacao.error = if (etDetalheValorTransacao.text.isEmpty())
                        "Digite o valor da transação" else null
                    etDetalheDataTransacao.error = if (etDetalheDataTransacao.text.isEmpty())
                        "Informe a nova data da transação" else null
                }
            }

            // Rotina para apagar uma transação
            R.id.action_apagar -> {
                var conta = daoConta.leiaContaId(transacao.conta)
                daoTransacao.apagaTransacao(transacao.id_)

                var saldo = conta.saldoinicial.toDouble()
                if (transacao.tipo == "Débito"){
                    saldo += transacao.valor.toDouble()
                } else {
                    saldo -= transacao.valor.toDouble()
                }

                conta.saldoinicial = saldo.toString()
                daoConta.atualizaConta(conta)

                Toast.makeText(this, "Transação apagada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}