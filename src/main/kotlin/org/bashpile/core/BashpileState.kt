package org.bashpile.core

import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.VariableTypeInfo


typealias Stackframe = MutableList<VariableTypeInfo>

/**
 * Sometimes the type of a node is known at creation, so the type may be in [org.bashpile.core.bast.BastNode] as well.
 */
class BashpileState: AutoCloseable {

    val stack: MutableList<Stackframe> = mutableListOf(mutableListOf())

    fun addVariableInfo(id: String, type: TypeEnum, subtype: TypeEnum, readonly: Boolean) {
        stack.top().add(VariableTypeInfo(id, type, subtype, readonly))
    }

    fun assertInScope(id: String) {
        variableInfo(id) // called for the side effect
    }

    /** Only returns null on null [id].  Throws [java.lang.IllegalStateException] if ID not on current stackframe */
    fun variableInfo(id: String?): VariableTypeInfo? {
        if (id == null) {
            return null
        }

        val info = stack.top().find { it.id == id }
        check (info != null) { "Could not find $id on current stackframe"}
        return info
    }

    fun pushStackframe() {
        stack.add(mutableListOf())
    }

    override fun close() {
        // pop stack
        stack.removeLast()
        check(stack.isNotEmpty()) { "You cannot pop every frame off of stack" }
    }

    /** Gets the top stackframe.  Peeks at the Stack */
    private fun MutableList<Stackframe>.top(): Stackframe = this[this.size - 1]
}
