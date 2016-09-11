package game;

public abstract class Camera {
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch;
	private float yaw;
	private float roll;
	
	private float FOV;
//	private float ASPECT_RATIO;
	private float z_near;
	private float z_far;
	
	private float pitch_min = -90;
	private float pitch_max = 90;
	
	private Matrix4 projectionMatrix;
	
	public Camera(float fov, float z_near, float z_far, Vector3f position) {
		this.FOV = fov;
		this.z_far = z_far;
		this.z_near = z_near;
		this.position = position;
		
		createProjectionMatrix(Window.getWidth(), Window.getHeight());
	}
	
	public abstract void update();
	
	protected void createProjectionMatrix(int width, int height) {
		// width / height
		float aspectRatio = (float) width / height;
		float y_scale = 1f / (float) Math.tan(Math.toRadians(FOV / 2f)) * aspectRatio;
		float x_scale = y_scale / aspectRatio;
		float frustum_length = z_far - z_near;

		projectionMatrix = new Matrix4();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((z_far + z_near) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * z_near * z_far) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	public boolean isInBounds(float x, float y, float z) {
		if (x - position.x < z_far && x - position.x > -z_far) {
			if (y - position.y < z_far && y - position.y > -z_far - 10) {
				if (z - position.z < z_far && z - position.z > -z_far) {
					return true;
				}
			}
		}

		return false;
	}
	
	public float getPitch_min() {
		return pitch_min;
	}

	public void setPitch_min(float pitch_min) {
		this.pitch_min = pitch_min;
	}

	public float getPitch_max() {
		return pitch_max;
	}

	public void setPitch_max(float pitch_max) {
		this.pitch_max = pitch_max;
	}

	public void changePitch(float amt){
		this.pitch += amt;
	}
	
	public void changeYaw(float amt){
		this.yaw += amt;
	}

	public Matrix4 getProjectionMatrix() {
		return projectionMatrix;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}
}
