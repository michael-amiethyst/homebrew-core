package org.bashpile.core.bast.types

/** All the relevant data for a variable for [org.bashpile.core.BashpileState] */
data class VariableTypeInfo(val id: String, val majorType: TypeEnum, val minorType: TypeEnum, val readonly: Boolean)
