package br.edu.ifsp.scl.gerfinsdm2.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Conta (
    var id_: Int = 0,
    var nome: String = "",
    var saldoinicial: String = "")

    : Parcelable {
        override fun equals(other: Any?): Boolean {
            if (other == null || other !is Conta)
                return false
            return id_ == other.id_
        }
}

