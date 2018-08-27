package com.jaxer.example.collection.queue;

import java.util.PriorityQueue;

/**
 * Created by jxr on 3:22 PM 2018/8/27
 * 优先级队列实现 ToDoList
 */
public class ToDoList extends PriorityQueue {
    static class ToDoItem implements Comparable<ToDoItem> {
        private char primary;
        private int secondary;
        private String item;

        public ToDoItem(char primary, int secondary, String item) {
            this.primary = primary;
            this.secondary = secondary;
            this.item = item;
        }

        @Override
        public int compareTo(ToDoItem toDoItem) {
            if (primary > toDoItem.primary) {
                return 1;
            }
            if (primary == toDoItem.primary) {
                if (secondary > toDoItem.secondary) {
                    return 1;
                } else if (secondary == toDoItem.secondary) {
                    return 0;
                }
                return -1;
            }
            return -1;
        }

        @Override
        public String toString() {
            return Character.toString(primary) + secondary + ":" + item;
        }
    }

    public void add(String item, char primary, int secondary) {
        super.add(new ToDoItem(primary, secondary, item));
    }

    public static void main(String[] args) {
        ToDoList toDoList = new ToDoList();
        toDoList.add("Empty trash", 'C', 4);
        toDoList.add("Feed dog", 'A', 2);
        toDoList.add("Feed bird", 'B', 7);
        toDoList.add("Water lawn", 'A', 1);
        while (!toDoList.isEmpty()) {
            System.out.println(toDoList.remove());
        }
    }
}
