package org.bashpile.core

/** All the relevant data for a variable for [org.bashpile.core.engine.CallStack] */
data class VariableTypeInfo(val id: String, val majorType: TypeEnum, val minorType: TypeEnum, val readonly: Boolean) {
    fun coercesTo(type: TypeEnum): Boolean = majorType.coercesTo(type)
}
