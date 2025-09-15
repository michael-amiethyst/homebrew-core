package org.bashpile.core

import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.VariableTypeInfo

/**
 * Sometimes the type of a node is known at creation, so the type may be in [org.bashpile.core.bast.BastNode] as well.
 */
class BashpileState {

    // TODO foreach -- make full stack
    val stackframe: MutableList<VariableTypeInfo> = mutableListOf()

    fun addVariableInfo(id: String, type: TypeEnum, subtype: TypeEnum, readonly: Boolean) {
        stackframe.add(VariableTypeInfo(id, type, subtype, readonly))
    }

    fun variableInfo(id: String?): VariableTypeInfo? {
        return stackframe.find { it.id == id }
    }
}