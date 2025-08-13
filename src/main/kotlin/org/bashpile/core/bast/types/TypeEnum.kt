package org.bashpile.core.bast.types

enum class TypeEnum {
    /** Does not coerce to anything */
    EMPTY,
    /** Coerces to anything */
    UNKNOWN,
    BOOLEAN,
    INTEGER,
    /** Also used for unknown calculation results */
    FLOAT,
    STRING,
    LIST,
    MAP,
    REFERENCE;

    fun coercesTo(type: TypeEnum): Boolean {
        return this == type || this == UNKNOWN || type == UNKNOWN
    }

    /** This is for reducing a list of types to what they coerce to, or EMPTY */
    fun fold(type: TypeEnum): TypeEnum {
        return if (this.coercesTo(type)) { type } else { EMPTY }
    }
}
