package game;

import game.world.WorldCamera;
import game.world.WorldShader;

public class Game {
	
	private WorldCamera camera;
	private WorldShader shader;
	
	private Mesh mesh;
	private Texture texture;
	private MeshObject object;
	
	public Game()
	{
		init();
		loop();
		close();
	}
	
	private void init() {
		Window.createWindow(800, 600);
		Window.setIcon("icon.png");
		Window.enableDepthBuffer();
		
		camera = new WorldCamera(70, 0.1f, 10000f, new Vector3f(0, 0, 20f));
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
	}
	
	private void loop() {
		while(!Window.isCloseRequested())
		{
			shader.bind();
			{
				shader.loadViewMatrix(camera);
				mesh.enable();
				{
					mesh.render(shader, shader.getModelMatrix(), object, camera);
				}
				mesh.disable();
			}
			shader.unbind();
			camera.update();
			Window.update();
		}
	}

	private void close() {
		mesh.cleanUp();
		texture.cleanUp();
		shader.cleanup();
		Window.close();
	}

	public static void main(String[] args)
	{
		new Game();
	}
}