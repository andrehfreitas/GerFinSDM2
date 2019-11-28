package br.edu.ifsp.scl.gerfinsdm2.data

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.CREATE_TABLE_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.DATABASE_NAME
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.KEY_CATEGORIA_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.KEY_CODIGO_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.KEY_CONTA_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.KEY_DATA_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.KEY_DESCRICAO_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.KEY_TIPO_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.KEY_VALOR_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.data.TransacaoSQLite.Constantes.TABLE_TRANSACAO
import br.edu.ifsp.scl.gerfinsdm2.model.Transacao

class TransacaoSQLite (contexto: Context): TransacaoDAO {

    object Constantes {
        val DATABASE_NAME = "gerfin4.db"
        val TABLE_TRANSACAO = "transacao"
        val KEY_CODIGO_TRANSACAO = "codigo"
        val KEY_TIPO_TRANSACAO = "tipo"
        val KEY_CONTA_TRANSACAO = "conta"
        val KEY_CATEGORIA_TRANSACAO = "categoria"
        val KEY_DATA_TRANSACAO = "data"
        val KEY_VALOR_TRANSACAO = "valor"
        val KEY_DESCRICAO_TRANSACAO = "descricao"

        // Statement que será usado na primeira vez para criar a tabela
        val CREATE_TABLE_TRANSACAO = "CREATE TABLE IF NOT EXISTS ${TABLE_TRANSACAO}(" +
                "${KEY_CODIGO_TRANSACAO} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "${KEY_TIPO_TRANSACAO} TEXT NOT NULL, " +
                "${KEY_CONTA_TRANSACAO} INTEGER NOT NULL, " +
                "${KEY_CATEGORIA_TRANSACAO} INTEGER NOT NULL, " +
                "${KEY_DATA_TRANSACAO} TEXT NOT NULL, " +
                "${KEY_VALOR_TRANSACAO} TEXT NOT NULL, " +
                "${KEY_DESCRICAO_TRANSACAO} TEXT, " +
                "FOREIGN KEY($KEY_CATEGORIA_TRANSACAO) REFERENCES ${CategoriaSQLite.Constantes.TABLE_CATEGORIA}(${CategoriaSQLite.Constantes.KEY_CODIGO_CATEGORIA}) ON DELETE CASCADE, " +
                "FOREIGN KEY($KEY_CONTA_TRANSACAO) REFERENCES ${ContaSQLite.Constantes.TABLE_CONTA}(${ContaSQLite.Constantes.KEY_CODIGO_CONTA}) ON DELETE CASCADE);"
    }

    // Referência para o Banco de Dados
    val database: SQLiteDatabase

    init {
        // Criando ou abrindo e conectando-se ao Banco de Dados
        database = contexto.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null)

        // Criando a tabela Transações
        try {
            database.execSQL(CREATE_TABLE_TRANSACAO)
        } catch (e: SQLException) {
            Log.e(contexto.getString(R.string.app_name), "Problema com a criação da tabela!")
        }
    }

    override fun criaTransacao(transacao: Transacao) {
        val values = ContentValues()

        values.put(KEY_TIPO_TRANSACAO, transacao.tipo)
        values.put(KEY_CONTA_TRANSACAO, transacao.conta)
        values.put(KEY_CATEGORIA_TRANSACAO, transacao.categoria)
        values.put(KEY_DATA_TRANSACAO, transacao.data)
        values.put(KEY_VALOR_TRANSACAO, transacao.valor)
        values.put(KEY_DESCRICAO_TRANSACAO, transacao.descricao)

        database.insert(TABLE_TRANSACAO, null, values)
    }

    // Operação de atualização de uma transação do BD
    override fun atualizaTransacao(transacao: Transacao) {
        val values = ContentValues()

        values.put(KEY_TIPO_TRANSACAO, transacao.tipo)
        values.put(KEY_CONTA_TRANSACAO, transacao.conta)
        values.put(KEY_CATEGORIA_TRANSACAO, transacao.categoria)
        values.put(KEY_DATA_TRANSACAO, transacao.data)
        values.put(KEY_VALOR_TRANSACAO, transacao.valor)
        values.put(KEY_DESCRICAO_TRANSACAO, transacao.descricao)

        database.update(
            TABLE_TRANSACAO,
            values,
            "$KEY_CODIGO_TRANSACAO = ?",
            arrayOf(transacao.id_.toString())
        )
    }

    // Apaga transação pelo Id do BD
    override fun apagaTransacao(id: Int) {
        database.delete(TABLE_TRANSACAO, "$KEY_CODIGO_TRANSACAO = ?", arrayOf(id.toString()))
    }



    // Retorna todas transacoes do banco de dados
    override fun leiaTransacao(where: String): ArrayList<Transacao> {
        val listaTransacoes = arrayListOf<Transacao>()
        var transacao = ""
        if (where == "") {
            transacao = "SELECT * FROM $TABLE_TRANSACAO;"
        } else {
            transacao = "SELECT * FROM $TABLE_TRANSACAO WHERE $where;"
        }
        val transacaoCursor = database.rawQuery(transacao, null)
        while (transacaoCursor.moveToNext()) {
            listaTransacoes.add(converteCursorTransacao(transacaoCursor))
        }
        return listaTransacoes
    }

    // Converte uma linha do Cursor para uma objeto de Transacao
    private fun converteCursorTransacao(cursor: Cursor): Transacao {
        return Transacao(
            cursor.getInt(cursor.getColumnIndex(KEY_CODIGO_TRANSACAO)),
            cursor.getString(cursor.getColumnIndex(KEY_TIPO_TRANSACAO)),
            cursor.getInt(cursor.getColumnIndex(KEY_CONTA_TRANSACAO)),
            cursor.getInt(cursor.getColumnIndex(KEY_CATEGORIA_TRANSACAO)),
            cursor.getString(cursor.getColumnIndex(KEY_DATA_TRANSACAO)),
            cursor.getString(cursor.getColumnIndex(KEY_VALOR_TRANSACAO)),
            cursor.getString(cursor.getColumnIndex(KEY_DESCRICAO_TRANSACAO))
        )
    }

    override fun buscaTransacao(transacao: Transacao): MutableList<Transacao> {

        var listaTransacao = arrayListOf<Transacao>()

        //Verificação
        var tipo = transacao.tipo.isEmpty()
        var conta = transacao.conta.toString() == "0"
        var categoria = transacao.categoria.toString() == "0"
        var inicio = transacao.data.isEmpty()
        var fim = transacao.periodicidade.isEmpty()

        Log.d(
            "Query",
            "Tipo: " + tipo.toString() + ", Conta: " + conta.toString() + ", Categoria: " + categoria.toString()
                    + ", Inicio: " + inicio.toString() + ", Fim: " + fim.toString()
        )

        var tipoCreditoDebito = transacao.tipo.split(" e ")

        if ((tipo) and (conta) and (categoria) and (inicio) and (fim)) {

            listaTransacao = leiaTransacao()

        } else {

            //Adiciona esse parametro para poder realizar busca por qualquer campo
            var sql = " 1 = 1"

            //Verifica quais campos devem ser inseridos na query
            if (!tipo) {

                if (transacao.tipo.equals("Crédito e Débito")) {
                    sql += " AND tipo in ('${tipoCreditoDebito[0]}','${tipoCreditoDebito[1]}')"
                } else {
                    sql += " AND tipo = '${transacao.tipo}'"
                }
            }
            if (!conta) {
                sql += " AND conta = '${transacao.conta}'"
            }
            if (!categoria) {
                sql += " AND categoria = '${transacao.categoria}'"
            }
            if ((!inicio) and (!fim)) {
                sql += " AND data BETWEEN '${transacao.data}' AND '${transacao.periodicidade}'"
            }

            Log.d("Query", sql)


            listaTransacao = leiaTransacao(sql)
        }

        return listaTransacao
    }
}