package game;

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
		System.out.println("CLOSED");
	}

	public static void main(String[] args)
	{
		new Game();
	}
}