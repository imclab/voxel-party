package mvc;

/**
 * The Controller which handles input
 * 
 * Controller runs in a seperate thread from the View to ensure responsiveness
 * 
 * TODO: cap controller tick
 * 
 * @author Lane Aasen <laneaasen@gmail.com>
 * 
 */


import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Controller implements Runnable {
	Thread inputThread;
	boolean stop = false;
	Model model;
	float sensitivity = 0.1f;
	float mouseSensitivity = 0.003f;

	/**
	 * Creates a Controller
	 * 
	 * @param model a Model for the Controller to manipulate
	 * @param view
	 */
	public Controller(Model model) {
		this.inputThread = new Thread(this, "input_thread");
		this.inputThread.start();
		this.model = model;

		try {
			Mouse.create();
			Mouse.setGrabbed(true);
			Keyboard.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the Controller
	 */
	@Override
	public void run() {
		while (!stop) {
			processInput();
			
			try {
				Thread.sleep(15l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Processes input once
	 */
	public void processInput() {
		int dy = 0;
		int dx = 0;
		
		while (Mouse.next()) {
			dy += Mouse.getEventDY();
			dx += Mouse.getEventDX();
		}

		this.model.camera.pitch -= dy * this.mouseSensitivity;
		this.model.camera.yaw += dx * this.mouseSensitivity;

		this.model.camera.update();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			this.model.camera.eye.z += this.sensitivity;
			this.model.camera.update();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			this.model.camera.eye.x += this.sensitivity;
			this.model.camera.update();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.model.camera.eye.z -= this.sensitivity;
			this.model.camera.update();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			this.model.camera.eye.x -= this.sensitivity;	
			this.model.camera.update();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
			this.model.camera.eye.y -= this.sensitivity;
			this.model.camera.update();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
			this.model.camera.eye.y += this.sensitivity;
			this.model.camera.update();
		}
	}

	/**
	 * Stops the Controller
	 */
	public void stop() {
		this.stop = true;
	}
}