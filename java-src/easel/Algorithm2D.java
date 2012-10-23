package easel;

/**
 * This interface should be implemented by classes wishing to provide their
 * own 2D drawing algorithms to the Easel renderer.
 */
public interface Algorithm2D
{
	/**
	 * Sets the colour of the specified pixel on the drawing canvas.
	 * <p>
	 * Should be called from inside the runAlgorithm method of your
	 * implementation of {@link Algorithm2D}
	 *
	 * @param  width the width of the drawing canvas, in pixels.
	 * @param  height the height of the drawing canvas, in pixels.
	 */
	public void runAlgorithm(int width, int height);
}