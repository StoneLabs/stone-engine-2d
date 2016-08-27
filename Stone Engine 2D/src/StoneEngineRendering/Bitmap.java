package StoneEngineRendering;
import java.util.Arrays;

import StoneEngineUtil.*;

public class Bitmap
{	
	public static enum Components{ RED, GREEN, BLUE, ALPHA }
	
    private final int m_width;
    private final int m_height;
    private final byte[] m_components;
    
    public int GetWidth() { return m_width; }
    public int GetHeight() { return m_height; }
    public byte[] GetByteArray()
    {
    	return m_components;
    }
    
    
    public Bitmap (int width, int height, byte[] components)
    {
        if (components.length != width * height * 4)
            Debug.Warning("Component array size is not as long as expected!");
        m_width      = width;
        m_height     = height;
        m_components = components;
    }
    public Bitmap (int width, int height)
    {
        m_width      = width;
        m_height     = height;
        m_components = new byte[width * height * 4];
    }
    
    public void Clear()
    {
        Arrays.fill(m_components, (byte)0x00);
    }
    public void Clear(byte shadeOfGrey)
    {
        Arrays.fill(m_components, shadeOfGrey);
    }
    
    public void DrawPixel(int x, int y, byte r, byte g, byte b)
    {
        int index = (x + y*m_width)*4;
        m_components[index    ] = (byte)0x00;
        m_components[index + 1] = b;
        m_components[index + 2] = g;
        m_components[index + 3] = r;
    }
    
    public void CopyPixel(int destX, int destY, int srcX, int srcY, Bitmap src)
    {
        int destIndex = (destX + destY *      m_width   )*4;
        int  srcIndex = ( srcX +  srcY * src.GetWidth() )*4;
        CopyPixel(destIndex, srcIndex, src);
    }
    public void CopyPixel(int destIndex, int srcIndex, Bitmap src)
    {
        m_components[destIndex    ] = src.m_components[srcIndex    ];
        m_components[destIndex + 1] = src.m_components[srcIndex + 1];
        m_components[destIndex + 2] = src.m_components[srcIndex + 2];
        m_components[destIndex + 3] = src.m_components[srcIndex + 3];
    }
    
    public void CopyNearest(int destX, int destY, float srcXFloat, float srcYFloat, Bitmap dest)
    {
    	int srcX = (int)(srcXFloat * (m_width  - 1));
    	int srcY = (int)(srcYFloat * (m_height - 1));
    	
//    	Debug.Log(srcX + "; " + srcY + "; " + m_width);
    	int destIndex = (destX + destY *  dest.m_width)*4;
    	int  srcIndex = ( srcX +  srcY *       m_width)*4;
    	
//    	Debug.Log(destIndex + "; " + srcIndex);
    	dest.m_components[destIndex    ] = m_components[srcIndex    ];
    	dest.m_components[destIndex + 1] = m_components[srcIndex + 1];
    	dest.m_components[destIndex + 2] = m_components[srcIndex + 2];
    	dest.m_components[destIndex + 3] = m_components[srcIndex + 3];
    }
     
    public byte GetNearestComponent(float srcX2, float srcY2, Components component)
    {
    	int srcX = (int)(srcX2 * (m_width  - 1));
    	int srcY = (int)(srcY2 * (m_height - 1));
    	    	
    	return GetComponent(srcX, srcY, component);
    }
    public byte GetComponent(int srcX, int srcY, Components component)
    {
    	int  srcIndex = ( srcX +  srcY *       m_width)*4;
    	
    	switch (component)
    	{
    		case ALPHA:
    			return GetComponent(srcIndex    );
    		case BLUE:
    			return GetComponent(srcIndex + 1);
    		case GREEN:
    			return GetComponent(srcIndex + 2);
    		case RED:
    			return GetComponent(srcIndex + 3);
    	    default:
    	    	Debug.Error("Invalid component");
    	    	/*IMP*/ return (byte)0x00; /*IMP*/
    	}
    }
    public byte GetComponent(int srcIndex)
    {    	
    	return m_components[srcIndex];
    }
    
    public void SetNearestComponent(float srcX2, float srcY2, Components component, byte value)
    {
    	int srcX = (int)(srcX2 * (m_width  - 1));
    	int srcY = (int)(srcY2 * (m_height - 1));
    	    	
    	SetComponent(srcX, srcY, component, value);
    }
    public void SetComponent(int srcX, int srcY, Components component, byte value)
    {
    	int  srcIndex = ( srcX +  srcY *       m_width)*4;

    	switch (component)
    	{
    		case ALPHA:
    			SetComponent(srcIndex    , value);
    			break;
    		case BLUE:
    			SetComponent(srcIndex + 1, value);
    			break;
    		case GREEN:
    			SetComponent(srcIndex + 2, value);
    			break;
    		case RED:
    			SetComponent(srcIndex + 3, value);
    			break;
    	    default:
    	    	Debug.Error("Invalid component");
    	}
    }
    public void SetComponent(int srcIndex, byte value)
    {    	
    	m_components[srcIndex] = value;
    }

    public void CopyToABGRByteArray(byte[] target)
    {
    	for (int i = 0; i < m_width*m_height*4; i++)
    	{
    		target[i] = m_components[i];
    	}
    }
	public void CopyToBGRByteArray(byte[] dest)
	{
		for(int i = 0; i < m_width * m_height; i++)
		{
			dest[i * 3    ] = m_components[i * 4 + 1];
			dest[i * 3 + 1] = m_components[i * 4 + 2];
			dest[i * 3 + 2] = m_components[i * 4 + 3];
		}
	}
    
    //NOT IN USE / NOT WORKING
    //TODO: repair
//    public void CopyToIntArray(int[] target)
//    {
//        for (int i = 0; i < m_width*m_height; i++) //Loop throw pixels not components
//        {
//            int a = ((int)m_components[i*4    ] << 24); //24 bits -> 6 bytes
//            int r = ((int)m_components[i*4    ] << 16); //16 bits -> 4 bytes
//            int g = ((int)m_components[i*4 + 1] << 8 ); //8  bits -> 2 bytes
//            int b = ((int)m_components[i*4 + 2]      );
//            //Typical argb -> int conversation eg.
//            // a = C7 = 0x000000C7 =SHIFT= 0xC7000000
//            // r = D9 = 0x000000D9 =SHIFT= 0x00D90000
//            // g = FF = 0x000000FF =SHIFT= 0x0000FF00
//            // b = 43 = 0x00000043 =SHIFT= 0x00000048
//            // Bitwise AND (|) gives us:   0xC7D9FF48 (= -942014648)
//            target[i] = r|g|b;
//          //target[i] = a|r|g|b;
//        }
//    }
}
