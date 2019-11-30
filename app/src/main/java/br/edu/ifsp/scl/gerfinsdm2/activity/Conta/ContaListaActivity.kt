package br.edu.ifsp.scl.gerfinsdm2.activity.Conta

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.activity.Transacao.TransacaoCadastroActivity
import br.edu.ifsp.scl.gerfinsdm2.adapter.ContaAdapter
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite
import kotlinx.android.synthetic.main.activity_listaconta.*
import kotlinx.android.synthetic.main.content_activity_listaconta.*

class ContaListaActivity : AppCompatActivity() {

    lateinit var contaAdapter: ContaAdapter
    private lateinit var dao: ContaSQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listaconta)
        setSupportActionBar(toolbar)

        dao = ContaSQLite(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_adicionar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_adicionar -> {
                val i = Intent(applicationContext, ContaCadastroActivity::class.java)
                startActivityForResult(i, 1)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        //Criar a lista de contas na activity
        val listaContas = dao.leiaConta()
        contaAdapter = ContaAdapter(this, listaContas)
        recyclerViewContas.adapter = contaAdapter
        recyclerViewContas.layoutManager = LinearLayoutManager(this)
        recyclerViewContas.smoothScrollToPosition(listaContas.size)
    }
}

