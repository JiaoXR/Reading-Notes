package com.jaxer.example.enumeration.eg2;

/**
 * Created by jxr on 6:50 PM 2018/8/27
 * enum 配合 switch...case 使用
 */
public class TrafficLight {
    private Signal color = Signal.GREEN;

    private void change() {
        switch (color) {
            case RED:
                color = Signal.GREEN;
                break;
            case GREEN:
                color = Signal.YELLOW;
                break;
            case YELLOW:
                color = Signal.RED;
                break;
        }
    }

    @Override
    public String toString() {
        return "The color is " + color;
    }

    public static void main(String[] args) {
        TrafficLight trafficLight = new TrafficLight();
        for (int i = 0; i < 7; i++) {
            System.out.println(trafficLight);
            trafficLight.change();
        }
    }
}
