package org.bashpile.core.bast

import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN


/**
 * For inner nodes in the tree data structure sense.  Used as a "holder node".
 *
 * See [Wikipedia - Internal Node](https://en.wikipedia.org/wiki/Tree_(abstract_data_type)#:~:text=An%20internal%20node)
 */
class InternalBastNode(
    children: List<BastNode> = listOf(), majorType: TypeEnum = UNKNOWN, val renderSeparator: String = "")
    : BastNode(children.toMutableList(), majorType = majorType) {
    /** Convenience constructor */
    constructor(vararg children: BastNode) : this(children.toList())

    override fun render(): String {
        return children.joinToString(renderSeparator) { it.render() }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): InternalBastNode {
        return InternalBastNode(nextChildren.map { it.deepCopy() }, majorType, renderSeparator)
    }
}
