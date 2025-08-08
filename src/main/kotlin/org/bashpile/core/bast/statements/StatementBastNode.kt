package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN

open class StatementBastNode(
    children: List<BastNode> = listOf(),
    id: String? = null,
    majorType: TypeEnum = UNKNOWN) : BastNode(children, id, majorType) {
        /** Takes a single node as a child */
        constructor(child: BastNode, id: String? = null, majorType: TypeEnum = UNKNOWN)
                : this(child.toList(), id, majorType)
    }
