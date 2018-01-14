package tree.bst;

import java.util.Comparator;

/**
 * Created by jaxer on 12/01/2018.
 */
public class BinarySearchTree<T> {

    private TreeNode<T> root;
    private Comparator<T> comparator;

    public BinarySearchTree() {
        this(null);
    }

    public BinarySearchTree(Comparator<T> comparator) {
        this.comparator = null;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
    }

    /**
     * 查找最小元素（递归）
     */
    public T findMin() {
        return findMin(root);
    }

    private T findMin(TreeNode<T> node) {
        if (node == null) {
            return null;
        } else if (node.left == null) {
            return node.element;
        } else {
            return findMin(node.left);
        }
    }

    /**
     * 查找最小元素（非递归）
     */
    public T findMax() {
        return findMax(root);
    }

    private T findMax(TreeNode<T> node) {
        if (node == null) {
            return null;
        }
        while (node.right != null) {
            node = node.right;
        }
        return node.element;
    }

    /**
     * 二分搜索的方法查询元素(递归)
     */
    public boolean contains(T x) {
        return contains(x, root);
    }

    private boolean contains(T x, TreeNode<T> node) {
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
     * 插入元素
     */
    public void insert(T x) {
        root = insert(x, root);
    }

    private TreeNode<T> insert(T x, TreeNode<T> node) {
        /* 是否插入第一个元素,或者是在叶子节点处插入元素 */
        if (node == null) {
            return new TreeNode<>(x);
        }
        int result = innerCompareTo(x, node.element);
        if (result < 0) {
            node.left = insert(x, node.left);
        } else if (result > 0) {
            node.right = insert(x, node.right);
        } else {
            //Duplicate; do nothing
            System.err.println("The node " + x + " has already existed!");
        }
        return node;
    }

    /**
     * 删除元素
     */
    public void remove(T x) {
        if (!contains(x, root)) {
            System.err.println("Node " + x + " dost not exist!");
            return;
        }
        root = remove(x, root);
    }

    private TreeNode<T> remove(T x, TreeNode<T> node) {
        if (node == null) {
            return null;
        }
        int result = innerCompareTo(x, node.element);
        if (result < 0) {
            node.left = remove(x, node.left);
        } else if (result > 0) {
            node.right = remove(x, node.right);
        } else if (node.left != null && node.right != null) {
            //要删除的节点左右子节点均不为空
            node.element = findMin(node.right);
            node.right = remove(node.element, node.right);
        } else {
            node = (node.left != null) ? node.left : node.right;
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    private int innerCompareTo(T lhs, T rhs) {
        if (comparator != null) {
            return comparator.compare(lhs, rhs);
        } else {
            return ((Comparable<T>) lhs).compareTo(rhs);
        }
    }

    /**
     * 打印二叉树
     */
    public void print() {
        print(root);
    }

    private void print(TreeNode<T> node) {
        if (node == null) {
            return;
        }
        T left = node.left != null ? node.left.element : null;
        T right = node.right != null ? node.right.element : null;
        System.out.println(node.element + ", l:" + left + ", r:" + right);
        print(node.left);
        print(node.right);
    }

    /**
     * 获取树的高度
     */
    public int height() {
        return height(root);
    }

    private int height(TreeNode<T> node) {
        if (node == null) {
            return -1;
        }
        /* 先计算左子树和右子树的高度,求出最大值,然后高度加一 */
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /**
     * 内部节点类
     *
     * @param <T> 泛型
     */
    private static class TreeNode<T> {
        T element;
        TreeNode<T> left;
        TreeNode<T> right;

        TreeNode(T element) {
            this(element, null, null);
        }

        TreeNode(T element, TreeNode<T> left, TreeNode<T> right) {
            this.element = element;
            this.left = left;
            this.right = right;
        }
    }

}
