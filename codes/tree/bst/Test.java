package tree.bst;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by jaxer on 12/01/2018.
 */
public class Test {
    private static final Random RANDOM = new Random(47);

    public static void main(String[] args) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            int random = RANDOM.nextInt(47);
            set.add(random);
        }

        for (Integer integer : set) {
            System.out.print(integer + ", ");
        }

        System.out.println();
        System.out.println("——————————————————————————————");

        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        for (Integer integer : set) {
            tree.insert(integer);
        }
        tree.insert(1);
        tree.insert(1);
        tree.print();
        System.out.println("——————————————————————————————");

        System.out.println("min: " + tree.findMin());
        System.out.println("max: " + tree.findMax());
        System.out.println(tree.contains(5));
        System.out.println("height: " + tree.height());
        System.out.println("——————————————————————————————");

        tree.remove(2);
        tree.print();
    }
}
