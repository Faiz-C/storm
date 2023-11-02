package org.storm.core.structures

data class TreeNode<T>(
    val id: String,
    val parentId: Int? = null,
    val branches: MutableList<TreeNode<T>> = mutableListOf(),
    val item: T
) {
    val isLeaf get() = this.branches.isEmpty()
}
