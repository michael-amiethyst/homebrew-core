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
        val top: Stackframe = stack.last()
        top.add(VariableTypeInfo(id, type, subtype, readonly))
    }

    fun assertInScope(id: String) {
        variableInfo(id) // called for the side effect
    }

    /** Only returns null on null [id].  Throws [java.lang.IllegalStateException] if ID not on current stackframe */
    fun variableInfo(id: String?): VariableTypeInfo? {
        if (id == null) {
            return null
        }

        val topmostStackframeWithId = stack.findLast { frame -> frame.any { it.id == id } }
        val info = topmostStackframeWithId?.find { it.id == id }
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

}
