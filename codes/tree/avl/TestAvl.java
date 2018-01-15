package tree.avl;

/**
 * Created by jxr on 15/01/2018.
 */
public class TestAvl {
    public static void main(String[] args) {
        AvlTree<Integer> tree = new AvlTree<>();
        for (int i = 10; i > 0; i--) {
            tree.insert(i);
        }
        tree.print();
        System.out.println("——————————————————————————————");

        tree.remove(0);
        tree.remove(2);
        tree.print();
        System.out.println(tree.contains(6));
        System.out.println(tree.isAvlTree());
        tree.clear();

        System.out.println(tree.height());
    }
}
