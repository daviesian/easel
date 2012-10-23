package easel;

import easel.InnerRenderer;

/**
 * The Easel Renderer is provided to allow you to experiment with graphics algorithm
 * implementation without worrying about the details of UI building or OpenGL configuration.
 * <p>
 * This framework is provided as a teaching resource for the Computer Graphics 
 * and Image Processing course in Part IB of the University of Cambridge Computer Science Tripos.
 */
public class Renderer
{
	/**
	 * Creates the Easel window with a blank 2D drawing canvas of the specified size.
	 * The window provides a "Run" button which will trigger the specified algorithm
	 * when clicked. A speed slider is also provided, allowing you to slow down
	 * execution of the algorithm.
	 *
	 * @param  width  width of the drawing canvas, in pixels.
	 * @param  height height of the drawing canvas, in pixels.
	 * @param  algorithm an implementation of the {@link Algorithm2D} interface
	 */
	public static void init2D(int width, int height, easel.Algorithm2D algorithm)
	{
		easel.InnerRenderer.init2D(width,height,algorithm);
	}
	
	/**
	 * Creates the Easel window with a blank 3D drawing canvas.
	 * <p>
	 * The image is redrawn at 60 FPS, and the renderFrame function of the 
	 * specified algorithm will be called on every frame.
	 *
	 * @param  algorithm an implementation of the {@link Algorithm3D} interface
	 */
	public static void init3D(easel.Algorithm3D algorithm)
	{
		easel.InnerRenderer.init3D(algorithm);
	}
	
	/**
	 * Sets the colour of the specified pixel on the drawing canvas.
	 * <p>
	 * Should be called from inside the runAlgorithm method of your
	 * implementation of {@link Algorithm2D}
	 *
	 * @param  x the x coordinate of the pixel to be coloured.
	 * @param  y the y coordinate of the pixel to be coloured.
	 * @param  red the red component of the desired RGB colour.
	 * @param  green the green component of the desired RGB colour.
	 * @param  blue the blue component of the desired RGB colour.
	 */
	public static void setPixel(int x, int y, int red, int green, int blue)
	{
		easel.InnerRenderer.setPixel(x,y,red,green,blue);
	}
	
	/**
	 * Draws an axis-aligned unit cube at the origin of the 3D world.
	 * <p>
	 * Should be called from inside the renderFrame method of your
	 * implementation of {@link Algorithm3D}
	 *
	 * @param  glContext the drawing context to use.
	 */
	public static void drawCube(javax.media.opengl.GL glContext)
	{
		easel.InnerRenderer.drawCube(glContext);
	}
}