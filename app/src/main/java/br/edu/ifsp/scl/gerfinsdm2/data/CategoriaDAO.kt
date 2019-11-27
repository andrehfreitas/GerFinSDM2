package br.edu.ifsp.scl.gerfinsdm2.data

import br.edu.ifsp.scl.gerfinsdm2.model.Categoria

interface CategoriaDAO{

    fun criaCategoria(categoria: Categoria)
    fun atualizaCategoria (categoria: Categoria)
    fun leiaCategoria(): MutableList<Categoria>
    fun leiaNomeCategoria(): MutableList<String>
    fun leiaCategoriaNome(nome: String): Categoria
    fun leiaCategoriaId(id: Int): Categoria
    fun apagaCategoria(id: Int)

}