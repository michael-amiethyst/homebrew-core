package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

/** See also [UnaryPrimaryBastNode] and [BinaryPrimaryBastNode]*/
abstract class PrimaryBastNode(left: BastNode?, protected val operator: String, right: BastNode)
    : BastNode(if (left != null) {
    mutableListOf(left, right)
    } else { mutableListOf(right) }, majorType = TypeEnum.BOOLEAN)
