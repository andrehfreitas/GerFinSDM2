package br.edu.ifsp.scl.gerfinsdm2.activity.Conta

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Conta
import kotlinx.android.synthetic.main.activity_detalhe_conta.*
import java.text.NumberFormat
import java.util.*


class ContaDetalheActivity: AppCompatActivity() {

    private lateinit var conta: Conta
    private lateinit var dao: ContaSQLite
    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_conta)
        dao = ContaSQLite(this)

        // Chamada da activity de atualização e preenchimento das view com dados do banco
        if (intent.hasExtra("conta")) {
            this.conta = intent.getParcelableExtra("conta") as Conta

            val f = NumberFormat.getCurrencyInstance(Locale("pt", "br"))

            val nome = this.findViewById<EditText>(R.id.etDetalheNomeConta)
            val saldo = this.findViewById<EditText>(R.id.etDetalheSaldoConta)

            saldo.isEnabled = false
            id = conta.id_
            nome.setText(conta.nome)
            saldo.setText(f.format(conta.saldoinicial.toDouble()))
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
                val nome = etDetalheNomeConta.text.toString()
                val saldoinicial = etDetalheSaldoConta.text.toString()

                //Verificando se o usuário preencheu todos os EditText
                if (etDetalheNomeConta.text.isNotEmpty() && etDetalheSaldoConta.text.isNotEmpty()) {
                    // Atualizando o nome da conta do banco de dados
                    val c = Conta()
                    c.id_ = id
                    c.nome = nome
                    c.saldoinicial = saldoinicial
                    dao.atualizaConta(c)

                    Toast.makeText(this, "Conta atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()

                }else {
                    //Caso usuário não tenha preenchido todos os campos é apresentado dialog_consulta_transacao
                    etDetalheNomeConta.error = if (etDetalheNomeConta.text.isEmpty())
                        "Digite o nome da conta" else null
                }
            }

            // Rotina para apagar uma conta
            R.id.action_apagar -> {
                dao.apagaConta(conta.id_)
                Toast.makeText(this, "Conta apagada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}