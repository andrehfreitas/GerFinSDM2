package br.edu.ifsp.scl.gerfinsdm2.activity.Transacao

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.*

class TransacaoCadastroActivity : AppCompatActivity(){

    private lateinit var listaNomeContas: ArrayList<String>
    private lateinit var listaNomeCategorias: ArrayList<String>
    private lateinit var listaContas: ArrayList<Conta>
    private lateinit var listaCategorias: ArrayList<Categoria>
    private lateinit var daoConta: ContaSQLite
    private lateinit var daoCategorias: CategoriaSQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadtransacoes)

        val c = Calendar.getInstance();
        val df =  SimpleDateFormat("dd/MM/yyyy")
        val dataformatada = df.format(c.getTime())
        etDataTransacao.setText(dataformatada)
        calendario(c, etDataTransacao)

        daoConta = ContaSQLite(this)
        daoCategorias = CategoriaSQLite(this)

        listaContas = daoConta.leiaConta()
        listaCategorias = daoCategorias.leiaCategoria()

        listaNomeContas = daoConta.leiaNomeConta()
        listaNomeContas.add(0, "Selecione a conta")
        listaNomeCategorias = daoCategorias.leiaNomeCategoria()
        listaNomeCategorias.add(0, "Selecione a categoria")

        // Preenchendo Spinner com o nome das Contas
        val spinnerConta = spnContaTransacao
        val arrayAdapterConta =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaNomeContas)
        spinnerConta.adapter = arrayAdapterConta

        // Preenchendo Spinner com o nome das Categorias
        val spinnerCategoria = spnCategoriaTransacao
        val arrayAdapterCategoria =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaNomeCategorias)
        spinnerCategoria.adapter = arrayAdapterCategoria
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cadastro, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_salvar -> {

                //Capturando os dados informados pelo usuário
                val tipo = spnTipoTransacao.selectedItem.toString()
                val nomeConta = spnContaTransacao.selectedItem.toString()
                val nomeCategoria = spnCategoriaTransacao.selectedItem.toString()
                val data = etDataTransacao.text.toString()
                val valor = etValorTransacao.text.toString()
                val descricao = etDescricaoTransacao.text.toString()
                val qtdeVezes = etQtdeTransacao.text.toString()
                val periodicidade = etDiasTransacao.text.toString()

                // Verificando se o usuário preencheu o valor da transação, único campo obrigatório
                // sem valor default. Caso positivo realiza operação de inclusão de transação.
                if (etValorTransacao.text.isNotEmpty()) {

                    var conta = 0
                    for (c in listaContas) {
                        if (c.nome == nomeConta) {
                            // Recebe Id da conta para armazenar na tabela de transação
                            conta = c.id_
                            var saldo = c.saldoinicial.toDouble()
                            // Calcula nova saldo da Conta transacionada
                            when (tipo) {
                                "Débito" -> saldo -= valor.toDouble()
                                "Crédito" -> saldo += valor.toDouble()
                            }
                            c.saldoinicial = saldo.toString()
                            // Atualiza saldo da Conta no BD
                            daoConta.atualizaConta(c)
                            break
                        }
                    }

                    var categoria = 0
                    for (cat in listaCategorias) {
                        if (cat.nome == nomeCategoria) {
                            // Recebe Id da categoria para armazenar na tabela de transação
                            categoria = cat.id_
                        }
                    }

                    // Cria objeto de transação e seta os valores
                    val t = Transacao()
                    t.tipo = tipo
                    t.conta = conta
                    t.categoria = categoria
                    t.data = data
                    t.valor = valor
                    t.descricao = descricao

                    // Após receber todos os parâmetros inclui a transação no BD
                    val dao = TransacaoSQLite(this)
                    dao.criaTransacao(t)

                    if (periodicidade.isNotEmpty() && qtdeVezes.isNotEmpty()) {
                        val dataFormatada = SimpleDateFormat("dd/MM/yyy")
                        val dataInicial: Calendar = Calendar.getInstance()
                        var dataNova: Date = dataFormatada.parse(data)

                        // For será executado na quantidade de vezes que a transação se repete
                        for (i in 1..qtdeVezes.toInt()) {

                            // Copiando as informações da transação informada
                            val novaTransacao = t.copy()
                            dataInicial.time = dataNova

                            // Adicionando a periodicidade na data informada pelo usuário
                            dataInicial.add(Calendar.DATE, periodicidade.toInt())
                            dataNova = dataInicial.time

                            // Transação recebe nova data com a periodicidade
                            novaTransacao.data = dataFormatada.format(dataNova)

                            // Operação de inclusão da transação que se repete com a nova data
                            for (c in listaContas) {
                                if (c.nome == nomeConta) {
                                    var saldo = c.saldoinicial.toDouble()
                                    when (tipo) {
                                        "Débito" -> saldo -= valor.toDouble()
                                        "Crédito" -> saldo += valor.toDouble()
                                    }
                                    c.saldoinicial = saldo.toString()
                                    daoConta.atualizaConta(c)
                                    break
                                }
                            }

                            dao.criaTransacao(novaTransacao)
                        }
                    }

                    Toast.makeText(this, "Transação salva com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    //Caso usuário não tenha preenchido o valor da transação é apresentado dialog_consulta_transacao
                    etValorTransacao.error = if (etValorTransacao.text.isEmpty())
                        "Digite o valor da transação" else null
                }
            }
        }
        return super.onOptionsItemSelected(item)
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