package br.edu.ifsp.scl.gerfinsdm2.activity.Categoria

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite
import br.edu.ifsp.scl.gerfinsdm2.model.Categoria
import kotlinx.android.synthetic.main.activity_cadcategoria.*


class CategoriaCadastroActivity: AppCompatActivity() {


    private lateinit var dao: CategoriaSQLite


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadcategoria)

        dao = CategoriaSQLite(this)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cadastro, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Clique no ícone para salvar os dados
            R.id.action_salvar -> {
                val nome = etNomeCategoria.text.toString()
                val descricao = etDescricaoCategoria.text.toString()

                //Verificando se o usuário preencheu o EditText do nome da Categoria
                if (etNomeCategoria.text.isNotEmpty()) {
                    // Capturando os dados dos EditText e inserindo no banco de dados
                    var cat = Categoria()
                    cat.nome = nome
                    cat.descricao = descricao
                    dao.criaCategoria(cat)
                    Toast.makeText(this, "Categoria salva com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }else {
                    //Caso usuário não tenha preenchido o campo de nome da categoria é apresentada mensagem de alerta
                    etNomeCategoria.error = if (etNomeCategoria.text.isEmpty())
                        "Digite o nome da categoria" else null
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}