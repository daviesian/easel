package easel;

/**
 * This interface should be implemented by classes wishing to provide their
 * own 3D drawing algorithms to the Easel renderer.
 */
public interface Algorithm3D
{
	/**
	 * Called at 60 FPS to render the 3D world.
	 *
	 * @param  glContext an OpenGL drawing context that will allow you to draw to the 3D world.
	 */
	public void renderFrame(javax.media.opengl.GL2 glContext);
}