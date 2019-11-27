package br.edu.ifsp.scl.gerfinsdm2.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transacao(
    var id_: Int = 0,
    var tipo: String = "",
    var conta: Int = 0,
    var categoria: Int = 0,
    var data: String = "",
    var valor: String = "",
    var descricao: String = "",
    val periodicidade: String = "")

    : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Transacao)
            return false
        return id_ == other.id_
    }
}