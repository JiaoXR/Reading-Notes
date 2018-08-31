package com.jaxer.example.enumeration.state;

/**
 * Created by jxr on 5:22 PM 2018/8/31
 * 使用 enum 的状态机
 */
public enum Input {
    NICKEL(5), DIME(10), DOLLAR(100),
    STOP {
        @Override
        int amount() {
            throw new RuntimeException("SHUT_DOWN.amount()");
        }
    };

    int value;

    Input() {
    }

    Input(int value) {
        this.value = value;
    }

    int amount() {
        return value;
    }
}
