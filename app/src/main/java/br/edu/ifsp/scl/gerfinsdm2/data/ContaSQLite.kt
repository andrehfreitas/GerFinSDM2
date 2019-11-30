package br.edu.ifsp.scl.gerfinsdm2.data

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import br.edu.ifsp.scl.gerfinsdm2.R
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite.Constantes.CREATE_TABLE_CONTA
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite.Constantes.DATABASE_NAME
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite.Constantes.KEY_CODIGO_CONTA
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite.Constantes.KEY_NOME_CONTA
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite.Constantes.KEY_SALDOINICIAL_CONTA
import br.edu.ifsp.scl.gerfinsdm2.data.ContaSQLite.Constantes.TABLE_CONTA
import br.edu.ifsp.scl.gerfinsdm2.model.Conta


class ContaSQLite (context: Context): ContaDAO {

    object Constantes {
        val DATABASE_NAME = "gerfinteste.db"
        val TABLE_CONTA = "contas"
        val KEY_CODIGO_CONTA = "codigo"
        val KEY_NOME_CONTA = "nome"
        val KEY_SALDOINICIAL_CONTA = "saldoinicial"

        // Statement que será usado na primeira vez para criar a tabela
        val CREATE_TABLE_CONTA = "CREATE TABLE IF NOT EXISTS ${TABLE_CONTA}(" +
                "${KEY_CODIGO_CONTA} INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "${KEY_NOME_CONTA} TEXT NOT NULL, " +
                "${KEY_SALDOINICIAL_CONTA} TEXT NOT NULL);"
    }


    // Referência para o Banco de Dados
    val database: SQLiteDatabase

    init {
        // Criando ou abrindo e conectando-se ao Banco de Dados
        database = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null)

        // Criando a tabela contas
        try {
            database.execSQL("PRAGMA foreign_keys=ON")
            database.execSQL(CREATE_TABLE_CONTA)
        } catch (e: SQLException) {
            Log.e(context.getString(R.string.app_name), "Problema com a criação da tabela!")
        }
    }


    // Inserção de dados das contas no banco de dados
    override fun criaConta(c: Conta) {
        // Fazendo o mapeamento de atributo-valor
        val values = ContentValues()
        values.put(KEY_NOME_CONTA, c.nome)
        values.put(KEY_SALDOINICIAL_CONTA, c.saldoinicial)

        // Inserindo dados no banco de dados
        database.insert(TABLE_CONTA, null, values)
    }


    // Atualização do nome da conta
    override fun atualizaConta(conta: Conta) {
        val values = ContentValues()
        values.put(KEY_NOME_CONTA, conta.nome)
        values.put(KEY_SALDOINICIAL_CONTA, conta.saldoinicial)

        // Execução do comando para atualização de dados
        database.update(TABLE_CONTA, values, "$KEY_CODIGO_CONTA = ?", arrayOf(conta.id_.toString()))
    }


    // Consulta que retorna todas as contas da tabela Conta
    override fun leiaConta(): ArrayList<Conta> {
        val listaContas = arrayListOf<Conta>()
        val contas = "SELECT * FROM $TABLE_CONTA ORDER BY $KEY_NOME_CONTA;"
        val contasCursor = database.rawQuery(contas, null)
        while (contasCursor.moveToNext()) {
            listaContas.add(converteCursorConta(contasCursor))
        }
        return listaContas
    }


    // Consulta na tabela Contas e retorna o nome de todas as contas
    override fun leiaNomeConta(): ArrayList<String> {
        val listaNomesContas = arrayListOf<String>()
        val contas = "SELECT * FROM $TABLE_CONTA ORDER BY $KEY_NOME_CONTA;"
        val contasCursor = database.rawQuery(contas, null)
        while (contasCursor.moveToNext()) {
            listaNomesContas.add(contasCursor.getString(1))
        }
        return listaNomesContas
    }


    // Lê a Conta passado o Id dela
    override fun leiaContaId(id: Int): Conta {
        val contaCursor = database.query(
            true, TABLE_CONTA, null,
            "$KEY_CODIGO_CONTA = ?", arrayOf("$id"), null, null, null, null
        )
        return if (contaCursor.moveToFirst()) converteCursorConta(contaCursor)
        else Conta()
    }


    // Apaga uma conta
    override fun apagaConta(id: Int) {
        database.delete(TABLE_CONTA, "$KEY_CODIGO_CONTA = ?", arrayOf(id.toString()))
    }


    // Lê a conta pelo passando o nome como parâmetro
    override fun leiaContaNome(nome: String): Conta {
        val contaCursor = database.query(
            true, TABLE_CONTA, null,
            "$KEY_NOME_CONTA = ?", arrayOf("$nome"), null, null, null, null
        )
        return if (contaCursor.moveToFirst()) converteCursorConta(contaCursor)
        else Conta()
    }


    // Converte uma linha do Cursor para um objeto da classe Conta
    private fun converteCursorConta(cursor: Cursor): Conta {
        return Conta(
            cursor.getInt(cursor.getColumnIndex(KEY_CODIGO_CONTA)),
            cursor.getString(cursor.getColumnIndex(KEY_NOME_CONTA)),
            cursor.getString(cursor.getColumnIndex(KEY_SALDOINICIAL_CONTA))
        )
    }
}