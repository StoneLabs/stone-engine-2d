package StoneEngineRendering;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Display extends Canvas
{
	private static final long serialVersionUID = -585250460545460914L;

	/** The main window */
    private final JFrame            m_frame;
    
    /** The bitmap which is drawn to the frame */
    private final RenderContext     m_frameBuffer;
    
    /** The buffer used to bring the Bitmap to the screen */
    private final BufferedImage     m_displayImage;
    
    /** A buffer array containing the frame components */
    private final byte[]            m_displayComponents;
    
    /** The canvas buffers */
    private final BufferStrategy    m_bufferStrategy;
    
    /** A Graphics object which is able to draw to canvas */
    private final Graphics          m_graphics;

    public Display(int width, int height)
    {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        m_frameBuffer = new RenderContext(width, height);
        m_displayImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        m_displayComponents = 
            ((DataBufferByte)m_displayImage.getRaster().getDataBuffer()).getData();
        
        m_frame = new JFrame();
        m_frame.setVisible(true);
        m_frame.setResizable(false);
        m_frame.setTitle("Stone Engine");
        m_frame.setLocationRelativeTo(null);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        m_frame.add(this);
        m_frame.pack();

        createBufferStrategy(1);
        m_bufferStrategy = getBufferStrategy();
        m_graphics = m_bufferStrategy.getDrawGraphics();
    }
    
    private String title = "New project";
    public void SetTitle(String title)
    {
        this.title = title;
        m_frame.setTitle("Stone Engine 2D [DEV-0] - " + title);
    }
    public void FpsTitle(float fps)
    {
        m_frame.setTitle((int)fps + "Stone Engine 2D [DEV-0] - " + title);
    }
    
    public RenderContext GetFrameBuffer()
    {
        return m_frameBuffer;
    }
    
    public void SwapBuffers()
    {
        m_frameBuffer.CopyToBGRByteArray(m_displayComponents);
        m_graphics.drawImage(m_displayImage, 0, 0, m_frameBuffer.GetWidth(), m_frameBuffer.GetHeight(), null);
        m_bufferStrategy.show();
    }
    
    public void Dispose()
    {
    	m_frame.dispose();
    }
}


