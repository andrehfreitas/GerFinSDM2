package br.edu.ifsp.scl.gerfinsdm2.data

import br.edu.ifsp.scl.gerfinsdm2.model.Conta

interface ContaDAO {

    fun criaConta(conta: Conta)
    fun atualizaConta (conta: Conta)
    fun leiaConta(): MutableList<Conta>
    fun leiaNomeConta(): MutableList<String>
    fun leiaContaNome(nome: String): Conta
    fun leiaContaId(id: Int): Conta
    fun apagaConta(id: Int)
}