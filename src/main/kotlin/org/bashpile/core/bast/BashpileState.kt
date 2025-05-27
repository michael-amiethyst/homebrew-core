package org.bashpile.core.bast

import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.VariableTypeInfo

class BashpileState {
    val stackframe: MutableList<VariableTypeInfo> = mutableListOf()

    fun addVariableInfo(id: String, type: TypeEnum, subtype: TypeEnum, readonly: Boolean) {
        stackframe.add(VariableTypeInfo(id, type, subtype, readonly))
    }

    fun variableInfo(id: String?): VariableTypeInfo? {
        return stackframe.find { it.id == id }
    }
}