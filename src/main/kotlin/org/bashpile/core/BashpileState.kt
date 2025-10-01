package org.bashpile.core

import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.VariableTypeInfo


typealias Stackframe = MutableList<VariableTypeInfo>

/**
 * Sometimes the type of a node is known at creation, so the type may be in [org.bashpile.core.bast.BastNode] as well.
 */
class BashpileState {

    val stack: MutableList<Stackframe> = mutableListOf(mutableListOf())

    fun addVariableInfo(id: String, type: TypeEnum, subtype: TypeEnum, readonly: Boolean) {
        stack.top().add(VariableTypeInfo(id, type, subtype, readonly))
    }

    fun variableInfo(id: String?): VariableTypeInfo? {
        return stack.top().find { it.id == id }
    }

    private fun MutableList<Stackframe>.top(): Stackframe = this[this.size - 1]
}
