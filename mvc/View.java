package mvc;

/** 
 * The View which is responsible for rendering a Model to the screen 
 * 
 * @author Lane Aasen <laneaasen@gmail.com>
 * 
 */

import objects.Grid;

import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.glu.GLU;

public class View {
	Model model;
	boolean stop = false;

	/**
	 * Constructs a View
	 * 
	 * Camera position is currently implied; it looks down the z axis by default
	 * 
	 * @param model model, or world to be drawn
	 */
	public View(Model model) {
		this.model = model;
	}

	/**
	 * Initializes OpenGL
	 */
	public void init() {
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();

		glViewport(0, 0, width, height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective(45.0f, ((float) width / (float) height), 0.1f, 100.0f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glShadeModel(GL_SMOOTH);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearDepth(1.0f);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		this.run();
	}

	/**
	 * Continuously renders the world
	 */
	public void run() {
		while (!Display.isCloseRequested() && !this.stop) {
			Display.sync(60);
			this.render();
		}

		Display.destroy();
	}

	/**
	 * Renders the world once
	 */
	public void render() {

		// check if the model is currently being written to and skip a frame if it is
		if(!model.locked) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	

			// adjust camera angle
			glLoadIdentity();
			GLU.gluLookAt(this.model.camera.eye.x, this.model.camera.eye.y, this.model.camera.eye.z,
					this.model.camera.focal.x, this.model.camera.focal.y, this.model.camera.focal.z,
					0.0f, 1.0f, 0.0f);


			glColor3f(1.0f, 1.0f, 1.0f);
			
			for( Grid grid : this.model.grids ) {
				grid.draw();
			}

			Display.update();
		}
	}

	public void stop() {
		this.stop = true;
	}
}