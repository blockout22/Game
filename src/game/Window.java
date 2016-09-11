package game;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

public class Window {

	private static long window;
	private static String TITLE;
	private static int WIDTH;
	private static int HEIGHT;

	private static boolean closeRequested = false;
	private static boolean resizeListener = false;
	private static boolean vsync = false;

	public static long createWindow() {
		return createWindow(800, 600, "Title", false);
	}

	public static long createWindow(int WIDTH, int HEIGHT) {
		return createWindow(WIDTH, HEIGHT, "Title", false);
	}

	public static long createWindow(int WIDTH, int HEIGHT, String TITLE) {
		return createWindow(WIDTH, HEIGHT, TITLE, false);
	}

	/**
	 * create window
	 * 
	 * @param WIDTH
	 * @param HEIGHT
	 * @param TITLE
	 * @param enableVsync
	 * @return
	 */
	public static long createWindow(int WIDTH, int HEIGHT, String TITLE, boolean enableVsync) {
		Window.WIDTH = 800;
		Window.HEIGHT = 600;
		Window.TITLE = "Title";
		GLFWErrorCallback.createPrint(System.err).set();
		// GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException();
		}

		window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, TITLE, MemoryUtil.NULL, MemoryUtil.NULL);

		if (window == MemoryUtil.NULL) {
			System.err.println("Window returned NULL");
			System.exit(-1);
		}

		GLFW.glfwMakeContextCurrent(window);
		if (enableVsync) {
			enableVSync();
		}
		GLFW.glfwShowWindow(window);
		GL.createCapabilities();
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		return window;
	}

	public static void setIcon(String path) {
		IntBuffer w = MemoryUtil.memAllocInt(1);
		IntBuffer h = MemoryUtil.memAllocInt(1);
		IntBuffer comp = MemoryUtil.memAllocInt(1);

		ByteBuffer icon16;
		ByteBuffer icon32;
		try {
			icon16 = ioResourceToByteBuffer(path, 2048);
			icon32 = ioResourceToByteBuffer(path, 4096);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		try (GLFWImage.Buffer icons = GLFWImage.malloc(2)) {
			ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
			icons.position(0).width(w.get(0)).height(h.get(0)).pixels(pixels16);

			ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
			icons.position(1).width(w.get(0)).height(h.get(0)).pixels(pixels32);

			icons.position(0);
			GLFW.glfwSetWindowIcon(window, icons);

			STBImage.stbi_image_free(pixels32);
			STBImage.stbi_image_free(pixels16);
		}
		MemoryUtil.memFree(comp);
		MemoryUtil.memFree(h);
		MemoryUtil.memFree(w);
	}

	public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		if (Files.isReadable(path)) {
			try (SeekableByteChannel fc = Files.newByteChannel(path)) {
				buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
				while (fc.read(buffer) != -1)
					;
			}
		} else {
			try (InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource); ReadableByteChannel rbc = Channels.newChannel(source)) {
				buffer = BufferUtils.createByteBuffer(bufferSize);

				while (true) {
					int bytes = rbc.read(buffer);
					if (bytes == -1)
						break;
					if (buffer.remaining() == 0)
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
				}
			}
		}
		buffer.flip();
		return buffer;
	}

	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}

	public static boolean isCloseRequested() {
		if (closeRequested) {
			return true;
		}

		if (resizeListener) {
			int w = WIDTH;
			int h = HEIGHT;

			int wCheck = Window.getWidth();
			int hCheck = Window.getHeight();

			if (w != wCheck || h != hCheck) {
				GL11.glViewport(0, 0, wCheck, hCheck);
			}
		}

		boolean shouldClose = false;
		boolean closeStage = GLFW.glfwWindowShouldClose(getWindowID());
		if (closeStage) {
			shouldClose = true;
		} else {
			shouldClose = false;
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		}

		return shouldClose;
	}

	public static void setClearColor(float r, float g, float b) {
		GL11.glClearColor(r, g, b, 1);
	}

	public static void update() {
		GLFW.glfwSwapBuffers(getWindowID());
		GLFW.glfwPollEvents();
	}

	public static void close() {
		GLFW.glfwSetErrorCallback(null).free();
		GLFW.glfwDestroyWindow(getWindowID());
		GLFW.glfwTerminate();
	}

	public static void requestClose() {
		closeRequested = true;
	}

	public static void enableDepthBuffer() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void disableDepthBuffer() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void enableResizeListener() {
		resizeListener = true;
	}

	public static void disableResizeListener() {
		resizeListener = false;
	}

	public static void enableVSync() {
		GLFW.glfwSwapInterval(1);
		vsync = true;
	}

	public static void disableVSync() {
		GLFW.glfwSwapInterval(0);
		vsync = false;
	}

	public static void toggleVSync() {
		if (vsync) {
			disableVSync();
		} else {
			enableVSync();
		}
	}

	public static void setTitle(String title) {
		GLFW.glfwSetWindowTitle(getWindowID(), title);
		Window.TITLE = title;
	}

	public static int getWidth() {
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(getWindowID(), width, height);
		Window.WIDTH = width.get();

		return Window.WIDTH;
	}

	public static int getHeight() {
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(getWindowID(), width, height);
		Window.HEIGHT = height.get();

		return Window.HEIGHT;
	}

	public static double getAspectRatio() {
		double aspectRatio = getWidth() / getHeight();
		return aspectRatio;
	}

	public static double getCursorXpos() {
		double x = Double.valueOf(getCursorPos().split(":")[0]);

		return x;
	}

	public static double getCursorYpos() {
		double y = Double.valueOf(getCursorPos().split(":")[1]);

		return y;
	}

	public static String getCursorPos() {
		DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
		xpos.rewind();
		xpos.rewind();
		GLFW.glfwGetCursorPos(getWindowID(), xpos, ypos);

		double x = xpos.get();
		double y = ypos.get();

		xpos.clear();
		ypos.clear();

		return x + ":" + y;
	}

	public static long getWindowID() {
		return Window.window;
	}

	public static String getTitle() {
		return TITLE;
	}
}
