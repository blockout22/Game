package game;

import game.shaderwriter.ShaderWriter;

public class Game {
	
	public Game()
	{
		init();
		loop();
		close();
	}
	
	private void init() {
		Window.createWindow(800, 600);
		Window.setIcon("icon.png");
	}
	
	private void loop() {
		while(!Window.isCloseRequested())
		{
			Window.update();
		}
	}

	private void close() {
		Window.close();
		
		ShaderWriter sw = new ShaderWriter();
		sw.addUniform("mat4", "projectionMatrix");
		sw.addUniform("mat4", "viewMatrix");
		sw.addUniform("mat4", "modelMatrix");
		System.out.println(sw.compile());
	}

	public static void main(String[] args)
	{
		new Game();
	}
}