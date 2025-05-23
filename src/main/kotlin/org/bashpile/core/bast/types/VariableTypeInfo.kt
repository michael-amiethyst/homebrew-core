package org.bashpile.core.bast.types

/** All the relevant data for a variable on the [org.bashpile.core.bast.BastNode.stackframe] */
data class VariableTypeInfo(val id: String, val type: String, val subtype: String, val readonly: Boolean)
