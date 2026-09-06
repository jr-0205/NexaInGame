package com.nexaclient.ingame.input;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Frame-based keyboard/mouse state and a rolling one-second CPS window. */
public final class InputTracker {
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>();
    private static boolean lastLeft;
    private static boolean lastRight;

    private InputTracker() { }

    public static void update(MinecraftClient client) {
        long window = client.getWindow().getHandle();
        boolean left = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long now = System.currentTimeMillis();
        if (left && !lastLeft) LEFT_CLICKS.addLast(now);
        if (right && !lastRight) RIGHT_CLICKS.addLast(now);
        lastLeft = left;
        lastRight = right;
        trim(LEFT_CLICKS, now);
        trim(RIGHT_CLICKS, now);
    }

    public static int leftCps() { return LEFT_CLICKS.size(); }
    public static int rightCps() { return RIGHT_CLICKS.size(); }
    public static boolean leftMouse() { return lastLeft; }
    public static boolean rightMouse() { return lastRight; }

    private static void trim(Deque<Long> values, long now) {
        while (!values.isEmpty() && now - values.peekFirst() > 1_000L) values.removeFirst();
    }
}
