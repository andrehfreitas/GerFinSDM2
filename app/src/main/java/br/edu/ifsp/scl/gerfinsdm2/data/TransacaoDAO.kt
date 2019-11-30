package br.edu.ifsp.scl.gerfinsdm2.data

import br.edu.ifsp.scl.gerfinsdm2.model.Transacao

interface TransacaoDAO {

    fun criaTransacao(transacao: Transacao)
    fun atualizaTransacao (transacao: Transacao)
    fun leiaTransacao(where: String = ""): MutableList<Transacao>
    fun apagaTransacao(id: Int)
    fun buscaTransacao(transacao: Transacao):MutableList<Transacao>
    fun buscaValorNoMes(tipoTransacao: String, mesAtual: String): Float
}