package tree.avl;

import java.util.Comparator;

/**
 * AVL树
 *
 * @author jaxer
 * date 15/01/2018
 */
public class AvlTree<T> {

    private AvlNode<T> root;
    private Comparator<T> comparator;

    public AvlTree() {
        this(null);
    }

    public AvlTree(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
    }

    public int height() {
        return height(root);
    }

    private int height(AvlNode<T> node) {
        return node == null ? -1 : node.height;
    }

    /**
     * 同二叉查找树
     */
    public boolean contains(T x) {
        return contains(x, root);
    }

    private boolean contains(T x, AvlNode<T> node) {
        if (node == null) {
            return false;
        }
        /* 通过比较 x 和节点的值, 来决定在左子树还是右子树中继续递归查找 */
        int result = innerCompareTo(x, node.element);
        if (result < 0) {
            return contains(x, node.left);
        } else if (result > 0) {
            return contains(x, node.right);
        } else {
            /* result = 0,说明已经查找到匹配的元素 */
            return true;
        }
    }

    /**
     * 同二叉查找树
     */
    public T findMin() {
        if (isEmpty()) {
            return null;
        }
        return findMin(root);
    }

    private T findMin(AvlNode<T> tree) {
        while (tree.left != null) {
            tree = tree.left;
        }
        return tree.element;
    }

    /**
     * 同二叉查找树
     */
    public T findMax() {
        if (isEmpty()) {
            return null;
        }
        return findMax(root);
    }

    private T findMax(AvlNode<T> tree) {
        while (tree.right != null) {
            tree = tree.right;
        }
        return tree.element;
    }

    public void print() {
        print(root);
    }

    private void print(AvlNode<T> tree) {
        if (tree == null) {
            return;
        }
        T left = tree.left != null ? tree.left.element : null;
        T right = tree.right != null ? tree.right.element : null;
        System.out.println(tree.element + ", l:" + left + ", r:" + right);
        print(tree.left);
        print(tree.right);
    }

    public boolean isAvlTree() {
        return isAvlTree(root);
    }

    private boolean isAvlTree(AvlNode<T> tree) {
        if (tree == null) {
            return true;
        }
        int x = height(tree.left) - height(tree.right);
        return Math.abs(x) < 2 && isAvlTree(tree.left) && isAvlTree(tree.right);
    }

    public void insert(T x) {
        root = insert(x, root);
    }

    /**
     * 内部插入方法
     * 插入时通过旋转来修正树的平衡性，并且只有x位置到根节点的路径上涉及的节点可能会被改变
     * 其中插入导致不平衡的情况只要有四种：(假设Q是需要重新平衡的节点)
     * 1.在Q左儿子的左子树插入元素;
     * 2.在Q左儿子的右子树插入元素;
     * 3.在Q右儿子的左子树插入元素;
     * 4.在Q右儿子的右子树插入元素.
     * 其中1,4是对称的情况，只需要单旋转就可以处理;
     * 2,3是对称的情况，需要双旋转来处理.
     *
     * @param x
     * @param tree
     * @return
     */
    private AvlNode<T> insert(T x, AvlNode<T> tree) {
        if (tree == null) {
            return new AvlNode<>(x);
        }

        int result = innerCompareTo(x, tree.element);
        /* 在左子树中插入 */
        if (result < 0) {
            tree.left = insert(x, tree.left);
            /* 因为上一步在左子树加入新的元素，所以可能发生高度变化的是左子树.
             * 所以当左子树高度与右子树高度相差2时则本节点发生了不平衡的情况
			 */
            if (height(tree.left) - height(tree.right) == 2) {
                /* 情况1:LL */
                if (innerCompareTo(x, tree.left.element) < 0) {
                    tree = rotateWithLeftChild(tree);
                }
                /* 情况2:LR */
                else {
                    tree = doubleRotateWithLeftChild(tree);
                }
            }
        }
        /* 在右子树中插入 */
        else if (result > 0) {
            tree.right = insert(x, tree.right);
            /* 同上分析 */
            if (height(tree.right) - height(tree.left) == 2) {
                /* 情况4:RR */
                if (innerCompareTo(x, tree.right.element) > 0) {
                    tree = rotateWithRightChild(tree);
                }
                /* 情况3:RL */
                else {
                    tree = doubleRotateWithRightChild(tree);
                }
            }
        }
        /* 有相同的元素 */
        else {
            //这里不做任何处理
            System.err.println(x + "已存在！");
        }
        /* 更新节点的高度  */
        tree.height = Math.max(height(tree.left), height(tree.right)) + 1;
        return tree;
    }

    public void remove(T x) {
        root = remove(x, root);
    }

    /**
     * 移除元素x
     * 可以近似的看作是插入操作的逆操作,导致不平衡的情况和插入操作一样有类似的四个原因。
     *
     * @param x
     * @param tree
     */
    private AvlNode<T> remove(T x, AvlNode<T> tree) {
        /* 空树或没有找到要删除的元素 */
        if (tree == null) {
            return null;
        }

        int result = innerCompareTo(x, tree.element);
        /* 在左子树中删除元素 */
        if (result < 0) {
            tree.left = remove(x, tree.left);
            /* 判断是否左子树减少元素后导致了不平衡, 此时右子树高度至少为2 */
            if (height(tree.right) - height(tree.left) == 2) {
                //System.out.println("左大：发生不平衡的节点：" + tree.element);
                //print(root);
                /* 如果右子树中的右子树高度比右子树中的左子树高度高，可看做是右子树的右子树插入元素导致的不平衡 */
                if (height(tree.right.right) >= height(tree.right.left)) {
                    tree = rotateWithRightChild(tree);
                } else {
                    tree = doubleRotateWithRightChild(tree);
                }
            }
        }
        /* 在右子树中删除元素 */
        else if (result > 0) {
            tree.right = remove(x, tree.right);
            if (height(tree.left) - height(tree.right) == 2) {
                //System.out.println("右大：发生不平衡的节点：" + tree.element);
                //print(root);
                if (height(tree.left.left) >= height(tree.left.right)) {
                    tree = rotateWithLeftChild(tree);
                } else {
                    tree = doubleRotateWithLeftChild(tree);
                }
            }
        }
        /* 找到要删除的元素,且该节点具有左子树和右子树 */
        else if (tree.left != null && tree.right != null) {
            /* 找到右子树中最小节点,用其值替换本节点值,并删除右子树中值最小的节点 */
            tree.element = findMin(tree.right);
            tree.right = remove(tree.element, tree.right);

            if (height(tree.left) - height(tree.right) == 2) {
                //System.out.println("右大：发生不平衡的节点：" + tree.element);
                //print(root);
                if (height(tree.left.left) >= height(tree.left.right)) {
                    tree = rotateWithLeftChild(tree);
                } else {
                    tree = doubleRotateWithLeftChild(tree);
                }
            }
        }
        /* 找到要删除的元素,且该节点只有左子树或者右子树或左右子树都没有 */
        else {
            tree = tree.left != null ? tree.left : tree.right;
        }
        if (tree != null) {
            tree.height = Math.max(height(tree.left), height(tree.right)) + 1;
        }
        return tree;
    }

    /**
     * 右旋转
     *
     * @param t 需要调整的树
     * @return 调整后的树
     */
    private AvlNode<T> rotateWithLeftChild(AvlNode<T> t) {
        AvlNode<T> newTree = t.left;
        t.left = newTree.right;
        newTree.right = t;
        //更新节点的高度
        t.height = 1 + Math.max(height(t.left), height(t.right));
        newTree.height = 1 + Math.max(height(newTree.left), t.height);
        return newTree;
    }

    /**
     * 左旋转
     */
    private AvlNode<T> rotateWithRightChild(AvlNode<T> t) {
        AvlNode<T> newTree = t.right;
        t.right = newTree.left;
        newTree.left = t;
        //更新节点的高度
        t.height = 1 + Math.max(height(t.left), height(t.right));
        newTree.height = 1 + Math.max(height(newTree.left), t.height);
        return newTree;
    }

    /**
     * 先左旋转，再右旋转
     */
    private AvlNode<T> doubleRotateWithLeftChild(AvlNode<T> t) {
        t.left = rotateWithRightChild(t.left);
        return rotateWithLeftChild(t);
    }

    /**
     * 先右旋转，再左旋转
     */
    private AvlNode<T> doubleRotateWithRightChild(AvlNode<T> t) {
        t.right = rotateWithLeftChild(t.right);
        return rotateWithRightChild(t);
    }

    @SuppressWarnings("unchecked")
    private int innerCompareTo(T lhs, T rhs) {
        if (comparator != null) {
            return comparator.compare(lhs, rhs);
        }
        return ((Comparable<T>) lhs).compareTo(rhs);
    }

    private static class AvlNode<T> {
        T element;
        AvlNode<T> left;
        AvlNode<T> right;
        int height;

        AvlNode(T element) {
            this(element, null, null);
        }

        AvlNode(T element, AvlNode<T> left, AvlNode<T> right) {
            this.element = element;
            this.left = left;
            this.right = right;
            this.height = 0;
        }
    }
}
