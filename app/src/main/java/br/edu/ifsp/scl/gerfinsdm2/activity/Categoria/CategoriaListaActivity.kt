package br.edu.ifsp.scl.gerfinsdm2.activity.Categoria

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.activity.Conta.ContaCadastroActivity
import br.edu.ifsp.scl.gerfinsdm2.adapter.CategoriaAdapter
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite
import kotlinx.android.synthetic.main.activity_listacategoria.*
import kotlinx.android.synthetic.main.content_activity_listacategoria.*

class CategoriaListaActivity : AppCompatActivity() {

    private lateinit var categoriaAdapter: CategoriaAdapter
    private lateinit var dao: CategoriaSQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listacategoria)
        setSupportActionBar(toolbar2)

        dao = CategoriaSQLite(this)
        dao.verificaTabela()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_adicionar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_adicionar -> {
                val i = Intent(applicationContext, CategoriaCadastroActivity::class.java)
                startActivityForResult(i, 1)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        //Criar a lista de categorias na activity
        val listaCategorias = dao.leiaCategoria()
        categoriaAdapter = CategoriaAdapter(this, listaCategorias)
        recyclerViewCategorias.adapter = categoriaAdapter
        recyclerViewCategorias.layoutManager = LinearLayoutManager(this)
        recyclerViewCategorias.smoothScrollToPosition(listaCategorias.size)
    }
}

