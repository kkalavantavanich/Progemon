package utility;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;

public class InputUtility {

	private static int mouseX, mouseY;
	private static int dragX, dragY;
	private static int scrollUp, scrollDown;
	private static boolean mouseLeftClick, mouseRightClick, mouseOnScreen;
	private static boolean mouseLeftPress, mouseRightPress;
	private static Set<KeyCode> keyPressed = new HashSet<KeyCode>();
	private static Set<KeyCode> keyTriggered = new HashSet<KeyCode>();

	public static int getMouseX() {
		return mouseX;
	}

	public static void setMouseX(int mouseX) {
		if (mouseLeftPress) {
			dragX = mouseX - InputUtility.mouseX;
		}
		InputUtility.mouseX = mouseX;
	}

	public static int getMouseY() {
		return mouseY;
	}

	public static void setMouseY(int mouseY) {
		if (mouseLeftPress) {
			dragY = mouseY - InputUtility.mouseY;
		}
		InputUtility.mouseY = mouseY;
	}

	public static boolean isMouseLeftClick() {
		return mouseLeftClick;
	}

	public static void setMouseLeftClick(boolean mouseLeftClick) {
		InputUtility.mouseLeftClick = mouseLeftClick;
	}

	public static boolean isMouseRightClick() {
		return mouseRightClick;
	}

	public static void setMouseRightClick(boolean mouseRightClick) {
		InputUtility.mouseRightClick = mouseRightClick;
	}

	public static boolean isMouseOnScreen() {
		return mouseOnScreen;
	}

	public static void setMouseOnScreen(boolean mouseOnScreen) {
		InputUtility.mouseOnScreen = mouseOnScreen;
	}

	public static boolean isMouseLeftPress() {
		return mouseLeftPress;
	}

	public static void setMouseLeftPress(boolean mouseLeftPress) {
		InputUtility.mouseLeftPress = mouseLeftPress;
	}

	public static boolean isMouseRightPress() {
		return mouseRightPress;
	}

	public static void setMouseRightPress(boolean mouseRightPress) {
		InputUtility.mouseRightPress = mouseRightPress;
	}

	public static boolean getKeyPressed(KeyCode keycode) {
		return keyPressed.contains(keycode);
	}

	public static void setKeyPressed(KeyCode keycode, boolean pressed) {
		if (pressed) {
			keyPressed.add(keycode);
		} else {
			keyPressed.remove(keycode);
		}
	}

	public static boolean getKeyTriggered(KeyCode keycode) {
		return keyTriggered.contains(keycode);
	}

	public static void setKeyTriggered(KeyCode keycode, boolean pressed) {
		if (pressed && !getKeyPressed(keycode)) {
			keyTriggered.add(keycode);
		} else {
			keyTriggered.remove(keycode);
		}
	}
	
	public static int getDragX() {
		return dragX;
	}
	
	public static int getDragY() {
		return dragY;
	}
	
	public static int getScrollDown() {
		return scrollDown;
	}
	
	public static int getScrollUp() {
		return scrollUp;
	}
	
	public static void setScroll(int scroll) {
		if (scroll > 0) {
			scrollUp = scroll;
		}
		else{
			scrollDown = -scroll;
		}
	}

	/** Update Trigger Mechanism at end of Clock.tick() */
	public static void postUpdate() {
		if (mouseLeftClick) {
			mouseLeftPress = true;
		}
		if (mouseRightClick) {
			mouseRightPress = true;
		}
		dragX = 0;
		dragY = 0;
		scrollDown = 0;
		scrollUp = 0;
		mouseLeftClick = false;
		mouseRightClick = false;
		keyTriggered.clear();
	}
}
