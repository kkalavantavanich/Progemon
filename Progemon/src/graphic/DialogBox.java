package graphic;

import java.awt.Font;

import javax.swing.SwingUtilities;

public class DialogBox implements IRenderable{
	
	protected static final String DIALOG_BOX_PATH = "load\\img\\dialogbox\\Theme1.png";
	
	private static final int x = 0, y = 240;
	private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 15);
	private static String message = "", nextWord = "";
	private static String[] messageOnScreen = {"", ""};
	private static Font font = DEFAULT_FONT;
	private static int textDelay = 5, textDelayCounter = 5, newLineDelay = 0, newLineDelayCounter = 0, currentLine = 0;
	private static int yShift = 0;
	
	@Override
	public void draw() {
		// TODO Auto-generated method stub
		if(textDelayCounter == textDelay){
			if(nextWord.length() > 0){
				messageOnScreen[currentLine] += nextWord.substring(0, 1);
				nextWord = nextWord.substring(1, nextWord.length());
			}
			else if(message.split(" ").length > 0 && message.length() > 0){
				if(SwingUtilities.computeStringWidth(ScreenComponent.g2.getFontMetrics(font), (messageOnScreen[currentLine] + message.split(" ")[0])) > 280){
					if(currentLine < 1){
						currentLine += 1;
					}
					else{
						toNewLine();
					}
				}
				else{
					nextWord = message.split(" ")[0];
					message = message.substring(nextWord.length(), message.length());
					if(message.split(" ").length > 0 && message.length() > 0){
						message = message.substring(1, message.length());
						nextWord += " ";
					}
				}
			}
			textDelayCounter = 0;
		}
		else{
			textDelayCounter++;
		}
		DrawingUtility.drawDialogBox();
	}
	
	@Override
	public void getDepth() {
		// TODO Auto-generated method stub
		
	}
	
	private static void toNewLine(){
		if(newLineDelayCounter == newLineDelay){
			if(yShift >= 25){
				messageOnScreen[0] = messageOnScreen[1];
				messageOnScreen[1] = "";
				yShift = 0;
			}
			else{
				yShift += 5;
			}
			newLineDelayCounter=0;
		}
		else{
			newLineDelayCounter++;
		}
	}
	
	public static int getX() {
		return x;
	}
	
	public static int getY() {
		return y;
	}
	
	public static String getMessage() {
		return message;
	}
	
	public static void sentMessage(String message) {
		DialogBox.message = message;
	}
	
	public static void clear() {
		DialogBox.message = "";
	}
	
	public static Font getFont() {
		return font;
	}
	
	public static void setFont(Font font) {
		DialogBox.font = font;
	}
	
	public static int getTextDelay() {
		return textDelay;
	}
	
	public static void setTextDelay(int textDelay) {
		DialogBox.textDelay = textDelay;
	}
	
	public static String[] getMessageOnScreen() {
		return messageOnScreen;
	}
	
	public static int getyShift() {
		return yShift;
	}

}