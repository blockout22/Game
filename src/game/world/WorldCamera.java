package game.world;

import game.Camera;
import game.Vector3f;
import game.Window;

public class WorldCamera extends Camera{
	
	private boolean mouseGrabbed = false;
	private double newX = Window.getWidth() / 2;
	private double newY = Window.getHeight() / 2;
	private double prevX = 0;
	private double prevY = 0;

	private float sensitivity = 3f;

	public WorldCamera(float fov, float z_near, float z_far, Vector3f position) {
		super(fov, z_near, z_far, position);
	}

	@Override
	public void update() {
//		mouse();
//		keyboard();

		if (getPitch() > getPitch_max()) {
			setPitch(getPitch_max());
		} else if (getPitch() < getPitch_min()) {
			setPitch(getPitch_min());
		}
	}
	
	

}
