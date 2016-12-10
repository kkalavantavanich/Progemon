package graphic;

import java.util.concurrent.CopyOnWriteArrayList;

public class PsuedoAnimationHolder {
	
private static CopyOnWriteArrayList<PsuedoAnimation<?>> playingPsuedoAnimations = new CopyOnWriteArrayList<>();
	
	public static CopyOnWriteArrayList<PsuedoAnimation<?>> getPlayingPsuedoAnimations() {
		return playingPsuedoAnimations;
	}
	
	public static void addPlayingPsuedoAnimations(PsuedoAnimation<?> animation) {
		if (!playingPsuedoAnimations.contains(animation)) {
			playingPsuedoAnimations.add(animation);
		}
	}
	
	public static void removePlayingPsuedoAnimations(PsuedoAnimation<?> animation) {
		if (playingPsuedoAnimations.contains(animation)) {
			playingPsuedoAnimations.remove(animation);
		}
	}

}
