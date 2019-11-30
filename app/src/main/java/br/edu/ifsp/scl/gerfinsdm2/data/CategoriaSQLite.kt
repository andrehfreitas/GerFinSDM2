package br.edu.ifsp.scl.gerfinsdm2.data

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite.Constantes.CREATE_TABLE_CATEGORIA
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite.Constantes.DATABASE_NAME
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite.Constantes.KEY_CODIGO_CATEGORIA
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite.Constantes.KEY_DESCRICAO_CATEGORIA
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite.Constantes.KEY_NOME_CATEGORIA
import br.edu.ifsp.scl.gerfinsdm2.data.CategoriaSQLite.Constantes.TABLE_CATEGORIA
import br.edu.ifsp.scl.gerfinsdm2.model.Categoria


class CategoriaSQLite (contexto: Context): CategoriaDAO {

    object Constantes{
        val DATABASE_NAME = "gerfinteste.db"
        val TABLE_CATEGORIA = "categoria"
        val KEY_CODIGO_CATEGORIA = "codigo"
        val KEY_NOME_CATEGORIA = "nome"
        val KEY_DESCRICAO_CATEGORIA = "descricao"

        // Statement que será usado na primeira vez para criar a tabela
        val CREATE_TABLE_CATEGORIA = "CREATE TABLE IF NOT EXISTS ${TABLE_CATEGORIA}(" +
                "${KEY_CODIGO_CATEGORIA} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "${KEY_NOME_CATEGORIA} TEXT NOT NULL, " +
                "${KEY_DESCRICAO_CATEGORIA} TEXT);"
    }

    // Referência para o Banco de Dados
    val database: SQLiteDatabase

    init {
        // Criando ou abrindo e conectando-se ao Banco de Dados
        database = contexto.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null)

        // Criando a tabela categoria
        try {
            database.execSQL(CREATE_TABLE_CATEGORIA)
        }

        catch (e: SQLException){
            Log.e(contexto.getString(R.string.app_name),"Problema com a criação da tabela!")
        }
    }


    // Verifica se a tabela está vazia e insere uma categoria para Ajuste de Saldo da Conta
    override fun verificaTabela() {
        val cursor = database.rawQuery ("SELECT EXISTS (SELECT 1 FROM $TABLE_CATEGORIA)", null)
        if (cursor != null) {
            cursor.moveToFirst()
            if (cursor.getInt(0) == 0) {
                database.execSQL("INSERT INTO $TABLE_CATEGORIA ($KEY_NOME_CATEGORIA, $KEY_DESCRICAO_CATEGORIA) " +
                        "VALUES ('Ajuste de Saldo', 'Usado para realizar ajuste de saldo de uma conta quando está incoerente')")
            }
        }
    }

    override fun criaCategoria(cat: Categoria){
        // Fazendo o mapeamento de atributo-valor
        val values = ContentValues()
        values.put(KEY_NOME_CATEGORIA, cat.nome)
        values.put(KEY_DESCRICAO_CATEGORIA, cat.descricao)

        // Inserindo dados no banco de dados
        database.insert(TABLE_CATEGORIA, null, values)
    }

    override fun atualizaCategoria(categoria: Categoria) {
        val values = ContentValues()
        values.put(KEY_NOME_CATEGORIA, categoria.nome)
        values.put(KEY_DESCRICAO_CATEGORIA, categoria.descricao)

        // Execução do comando para atualização
        database.update(TABLE_CATEGORIA, values,"$KEY_CODIGO_CATEGORIA = ?", arrayOf(categoria.id_.toString()))
    }

    override fun leiaCategoria(): ArrayList<Categoria> {
        val listaCategorias = arrayListOf<Categoria>()

        // Consulta categorias do banco de dados
        val categorias = "SELECT * FROM $TABLE_CATEGORIA ORDER BY $KEY_NOME_CATEGORIA;"
        val categoriasCursor = database.rawQuery(categorias, null)
        while (categoriasCursor.moveToNext()) {
            listaCategorias.add(converteCursorCategoria(categoriasCursor))
        }
        return listaCategorias
    }

    override fun leiaNomeCategoria(): ArrayList<String> {
        val listaNomesCategorias = arrayListOf<String>()
        val categorias = "SELECT * FROM $TABLE_CATEGORIA ORDER BY $KEY_NOME_CATEGORIA;"
        val categoriaCursor = database.rawQuery(categorias, null)
        while (categoriaCursor.moveToNext()) {
            listaNomesCategorias.add(categoriaCursor.getString(1))
        }
        return listaNomesCategorias
    }

    override fun leiaCategoriaId(id: Int): Categoria {
        val categoriaCursor = database.query(true, TABLE_CATEGORIA,null,
            "${KEY_CODIGO_CATEGORIA} = ?",arrayOf("$id"),null, null,null,null)
        return if (categoriaCursor.moveToFirst()) converteCursorCategoria(categoriaCursor)
        else Categoria()
    }

    override fun apagaCategoria(id: Int) {
        database.delete(TABLE_CATEGORIA, "${KEY_CODIGO_CATEGORIA} = ?", arrayOf(id.toString()))
    }

    // Converte uma linha do Cursor para uma objeto da classe Categoria
    private fun converteCursorCategoria(cursor: Cursor): Categoria {
        return Categoria(
            cursor.getInt(cursor.getColumnIndex(KEY_CODIGO_CATEGORIA)),
            cursor.getString(cursor.getColumnIndex(KEY_NOME_CATEGORIA)),
            cursor.getString(cursor.getColumnIndex(KEY_DESCRICAO_CATEGORIA))
        )
    }

    override fun leiaCategoriaNome(nome: String): Categoria {
        val categoriaCursor = database.query(true, TABLE_CATEGORIA, null,
            "${KEY_NOME_CATEGORIA} = ?",arrayOf("$nome"),null, null,null,null)
        return if (categoriaCursor.moveToFirst()) converteCursorCategoria(categoriaCursor)
        else Categoria()
    }
}