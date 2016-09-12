package game;

import game.world.WorldShader;

public class Game {

	private String title = "Game";
	private long last_time;
	private int fps;

	private Camera camera;
	private WorldShader shader;

	private Mesh mesh;
	private Texture texture;
	private MeshObject object;
	
	private float SPEED = 0.01f;

	public Game() {
		init();
		loop();
		close();
	}

	private void init() {
		Window.createWindow(800, 600, title);
		Window.setIcon("icon.png");

		Window.enableDepthBuffer();
		camera = new Camera(70, 0.1f, 10000f);
		shader = new WorldShader("vertexShader.glsl", "fragmentShader.glsl");
		shader.bind();
		shader.loadMatrix(shader.getProjectionMatrix(), camera.getProjectionMatrix());

		mesh = OBJLoader.load("stall.obj");
		texture = TextureLoader.loadTexture("stall.png");
		object = new MeshObject(mesh, new Vector3f(0, 0, 0), new Vector3f(0, 150f, 0), 1f) {

			@Override
			public void update() {
				// TODO Auto-generated method stub

			}
		};

		last_time = Time.getTime();
	}

	private void loop() {
		// Window.enableVSync();
		while (!Window.isCloseRequested()) {
			Time.setDelta();
			if (Time.getTime() - last_time >= 1000) {
				last_time += 1000;
				Window.setTitle(title + " [FPS: " + fps + "]");
				fps = 0;
			}
			fps++;
			shader.bind();
			{
				shader.loadViewMatrix(camera);
				mesh.enable();
				{
					mesh.render(shader, shader.getModelMatrix(), object, camera);
				}
				mesh.disable();
			}
			if(Input.isKeyDown(Window.getWindowID(), Key.KEY_W))
			{
				camera.getPosition().x += Math.sin(camera.getYaw() * Math.PI / 180) * SPEED * Time.getDelta();
				camera.getPosition().z += -Math.cos(camera.getYaw() * Math.PI / 180) * SPEED * Time.getDelta();
			}else if(Input.isKeyDown(Window.getWindowID(), Key.KEY_S))
			{
				camera.getPosition().x -= Math.sin(camera.getYaw() * Math.PI / 180) * SPEED * Time.getDelta();
				camera.getPosition().z -= -Math.cos(camera.getYaw() * Math.PI / 180) * SPEED * Time.getDelta();
			}
			
			if(Input.isKeyDown(Window.getWindowID(), Key.KEY_A))
			{
				camera.getPosition().x += Math.sin((camera.getYaw() - 90) * Math.PI / 180) * SPEED * Time.getDelta();
				camera.getPosition().z += -Math.cos((camera.getYaw() - 90) * Math.PI / 180) * SPEED * Time.getDelta();
			}else if(Input.isKeyDown(Window.getWindowID(), Key.KEY_D))
			{
				camera.getPosition().x += Math.sin((camera.getYaw() + 90) * Math.PI / 180) * SPEED * Time.getDelta();
				camera.getPosition().z += -Math.cos((camera.getYaw() + 90) * Math.PI / 180) * SPEED * Time.getDelta();
			}
			
			shader.unbind();
			camera.update();
			Input.update(Window.getWindowID());
			Window.update();
		}
	}

	private void close() {
		mesh.cleanUp();
		texture.cleanUp();
		shader.cleanup();
		Window.close();
	}

	public static void main(String[] args) {
		new Game();
	}
}