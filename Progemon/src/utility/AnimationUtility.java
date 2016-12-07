package utility;

import java.util.ArrayList;

import graphic.Animation;
import graphic.ScreenEffect;

public class AnimationUtility {
	
	private static ScreenEffect loadScreen00 = new ScreenEffect(20, 3, false, false);
	private static ScreenEffect loadScreen01 = new ScreenEffect(27, 3, false, false);
	
	public AnimationUtility() {
		// TODO Auto-generated constructor stub
		loadScreen00.loadImage("load\\img\\animation\\loadscreen00.png");
		loadScreen01.loadImage("load\\img\\animation\\loadscreen01.png");
	}
	
	public static ScreenEffect getLoadScreen00() {
		return loadScreen00;
	}
	
	public static ScreenEffect getLoadScreen01() {
		return loadScreen01;
	}

}