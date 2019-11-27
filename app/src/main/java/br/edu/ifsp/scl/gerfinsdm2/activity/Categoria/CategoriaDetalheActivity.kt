package br.edu.ifsp.scl.gerfinsdm2.activity.Categoria

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Categoria
import kotlinx.android.synthetic.main.activity_detalhe_categoria.*


class CategoriaDetalheActivity: AppCompatActivity() {

    private lateinit var categoria: Categoria
    private lateinit var dao: CategoriaSQLite
    private var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_categoria)
        dao = CategoriaSQLite(this)

        // Chamada da activity de atualização e preenchimento das view com dados do banco
        if (intent.hasExtra("categoria")) {
            this.categoria = intent.getParcelableExtra("categoria") as Categoria

            val nome = this.findViewById<EditText>(R.id.etDetalheNomeCategoria)
            val descricao = this.findViewById<EditText>(R.id.etDetalheDescricaoCategoria)

            id = categoria.id_
            nome.setText(categoria.nome)
            descricao.setText(categoria.descricao)
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
                val nome = etDetalheNomeCategoria.text.toString()
                val descricao = etDetalheDescricaoCategoria.text.toString()

                //Verificando se o usuário preencheu todos os EditText
                if (etDetalheNomeCategoria.text.isNotEmpty()) {
                    // Atualizando dos dados da categoria do banco de dados
                    val cat = Categoria()
                    cat.id_ = id
                    cat.nome = nome
                    cat.descricao = descricao
                    dao.atualizaCategoria(cat)

                    Toast.makeText(this, "Categoria atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()

                } else {
                    //Caso usuário não tenha preenchido todos os campos é apresentado consulta_transacao
                    etDetalheNomeCategoria.error = if (etDetalheNomeCategoria.text.isEmpty())
                        "Digite o nome da categoria" else null
                }
            }

            // Clique no ícone para apagar uma categoria
            R.id.action_apagar -> {
                dao.apagaCategoria(categoria.id_)
                Toast.makeText(this, "Categoria apagada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}