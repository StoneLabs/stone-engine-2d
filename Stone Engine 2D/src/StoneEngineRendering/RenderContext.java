package StoneEngineRendering;

import StoneEngineUtil.*;

public class RenderContext extends Bitmap
{
	//TODO think about renaming 'TransparencyMode' to 'AlphaMode' (for simplicity)
	public static enum TransparencyMode
	{
		//TODO think about renaming 'simple' to 'basic' (for accuracy)
		NONE, SIMPLE, FULL
	}
	
    public RenderContext (int width, int height)
    {
    	super(width, height);
    } 
        
    //TODO Simplify, naming convention!
    public void DrawImage(float xStart, float yStart, 
			  			  float xEnd, float yEnd, 
    					  Bitmap image, TransparencyMode tMode)
    {
    	float halfWidth = GetWidth()/2.0f;
    	float halfHeight = GetHeight()/2.0f;

    	float imageStartX = 0.0f;
    	float imageStartY = 0.0f;
    	
    	float imageStepX = 1.0f/(((xEnd * halfWidth) + halfWidth)
				-((xStart * halfWidth) + halfWidth));
    	
    	float imageStepY = 1.0f/(((yEnd * halfHeight) + halfHeight)
    				-((yStart * halfHeight) + halfHeight));
    	
    	
    	if (xStart < -1.0f)
    	{
    		imageStartX = -((xStart + 1.0f)/(xEnd - xStart));
    		xStart = -1.0f;
    	}
    	else if (xStart > 1.0f)
    	{
    		imageStartX = -((xStart + 1.0f)/(xEnd - xStart));
    		xStart = 1.0f;
    	}
    	
    	if (yStart < -1.0f)
    	{
    		imageStartX = -((yStart + 1.0f)/(yEnd - yStart));
    		yStart = -1.0f;
    	}
    	else if (yStart > 1.0f)
    	{
    		imageStartX = -((yStart + 1.0f)/(yEnd - yStart));
    		yStart = 1.0f;
    	}
    	
    	xEnd = Util.Clamp(xEnd, -1.0f, 1.0f);
    	yEnd = Util.Clamp(yEnd, -1.0f, 1.0f);
    	
    	xStart = ( xStart * halfWidth ) + halfWidth;
    	yStart = ( yStart * halfHeight) + halfHeight;
    	xEnd   = ( xEnd   * halfWidth ) + halfWidth;
    	yEnd   = ( yEnd   * halfHeight) + halfHeight;
//    	Debug.Log(yStart + " - " + yEnd);
    	
    	switch (tMode)
    	{
	    	case NONE:
		    	DrawImageInternal((int)xStart, (int)yStart,
		    					  (int)xEnd  , (int)yEnd,
		    					  imageStartX, imageStartY,
		    					  imageStepX , imageStepY,
		    					  image);
		    	break;
	    	case SIMPLE:
	    		DrawImageSimpleTransparencyInternal(
	    			  (int)xStart, (int)yStart,
  					  (int)xEnd  , (int)yEnd,
  					  imageStartX, imageStartY,
  					  imageStepX , imageStepY,
  					  image);
	    		break;
	    	case FULL:
	    		DrawImageAlphaBlendedInternal(
		    			  (int)xStart, (int)yStart,
	  					  (int)xEnd  , (int)yEnd,
	  					  imageStartX, imageStartY,
	  					  imageStepX , imageStepY,
	  					  image);
	    		break;
	    	default:
	    		Debug.Error("Invalid transparency mode!");
    	}
    }

    private void DrawImageInternal(
    		int xStart, int yStart, 
    		int xEnd, int yEnd, 
			float xTexStart, float yTexStart, 
			float srcXStep, float srcYStep, 
			Bitmap image)
    {
//    	float srcXStep = (xTexEnd - xTexStart) / (float)(xEnd - xStart);
//    	float srcYStep = (yTexEnd - yTexStart) / (float)(yEnd - yStart);
    	
    	float srcX = yTexStart;
		for (int x = (int)xStart; x < (int)xEnd; x++)
		{
			float srcY = xTexStart;
			for (int y = (int)yStart; y < (int)yEnd; y++)
			{
//				Debug.Seperator();
//				Debug.Log(x + "|" + y);
//				Debug.Log(srcX + "|" + srcY);
    			image.CopyNearest(x, y, srcX, srcY, this);
    			srcY += srcYStep;
			}
			
			srcX += srcXStep;
		}
    }
    
    private void DrawImageSimpleTransparencyInternal(
    		int xStart, int yStart, 
    		int xEnd, int yEnd, 
			float xTexStart, float yTexStart, 
			float srcXStep, float srcYStep, 
			Bitmap image)
    {    	
    	int destIndex = (xStart + yStart * GetWidth())*4;
    	int destYInc  = (GetWidth() - (xEnd - xStart))*4;
    	    	
    	float srcY = yTexStart;
    	float srcIndexFloatStep = (srcXStep * (float)(image.GetWidth() - 1));
    	
		for (int y = (int)yStart; y < (int)yEnd; y++)
		{
//			float srcY = xTexStart;
			float srcIndexFloat = ((xTexStart * (image.GetWidth() - 1))
					+(int)(srcY * (image.GetHeight()-1))*image.GetWidth());
					
			for (int x = (int)xStart; x < (int)xEnd; x++)
			{
				int srcIndex = (int)(srcIndexFloat)*4;

				if (image.GetComponent(srcIndex) < (byte)0x0) //ALPHA: index + 0
					CopyPixel(destIndex, srcIndex, image);
//				image.CopyNearest(x, y, srcX, srcY, this);
				
				destIndex += 4;
				srcIndexFloat += srcIndexFloatStep;
//    			srcY += srcYStep;
			}
			srcY += srcYStep;
			destIndex += destYInc;
//			srcX += srcXStep;
		}
    }
    
    //TODO optimize?
    private void DrawImageAlphaBlendedInternal(
    		int xStart, int yStart, 
    		int xEnd, int yEnd, 
			float xTexStart, float yTexStart, 
			float srcXStep, float srcYStep, 
			Bitmap image)
    {    	
    	int destIndex = (xStart + yStart * GetWidth())*4;
    	int destYInc  = (GetWidth() - (xEnd - xStart))*4;
    	    	
    	float srcY = yTexStart;
    	float srcIndexFloatStep = (srcXStep * (float)(image.GetWidth() - 1));
    	
		for (int y = (int)yStart; y < (int)yEnd; y++)
		{
//			float srcY = xTexStart;
			float srcIndexFloat = ((xTexStart * (image.GetWidth() - 1))
					+(int)(srcY * (image.GetHeight()-1))*image.GetWidth());
					
			for (int x = (int)xStart; x < (int)xEnd; x++)
			{
				int srcIndex = (int)(srcIndexFloat)*4;
				
				int srcA = image.GetComponent(srcIndex    )	&0xFF;
				int srcB = image.GetComponent(srcIndex + 1)	&0xFF;
				int srcG = image.GetComponent(srcIndex + 2)	&0xFF;
				int srcR = image.GetComponent(srcIndex + 3)	&0xFF;

//				byte tarA = GetComponent(x, y, Components.ALPHA);
				int tarB = GetComponent(destIndex + 1)		&0xFF;
				int tarG = GetComponent(destIndex + 2)		&0xFF;
				int tarR = GetComponent(destIndex + 3)		&0xFF;
				
				int srcAmnt = -srcA+255;
				int tarAmnt = 255 - srcAmnt;
				
				byte newB = (byte)((tarB*srcAmnt + srcB*tarAmnt) >> 8);
				byte newG = (byte)((tarG*srcAmnt + srcG*tarAmnt) >> 8);
				byte newR = (byte)((tarR*srcAmnt + srcR*tarAmnt) >> 8);

				SetComponent(destIndex + 1, newB);
				SetComponent(destIndex + 2, newG);
				SetComponent(destIndex + 3, newR);
//				image.CopyNearest(x, y, srcX, srcY, this);
				
				destIndex += 4;
				srcIndexFloat += srcIndexFloatStep;
//    			srcY += srcYStep;
			}
			srcY += srcYStep;
			destIndex += destYInc;
//			srcX += srcXStep;
		}
    }
    
    public void FillRect(float xStart, float yStart,
    		float xEnd, float yEnd,
    		byte r, byte g, byte b)
    {
//    	float halfWidth = GetWidth()/2.0f;
//    	float halfHeight = GetHeight()/2.0f;
    	
    	xStart = Util.Clamp(( xStart+1)/2.0f	* GetWidth()	, 0, GetWidth());	//Screen-space transformation
    	xEnd   = Util.Clamp(( xEnd+1)  /2.0f	* GetWidth()	, 0, GetWidth());
    	yStart = Util.Clamp(( yStart+1)/2.0f	* GetHeight()	, 0, GetHeight());
    	yEnd   = Util.Clamp(( yEnd+1)  /2.0f	* GetHeight()	, 0, GetHeight());

    	FillRectInternal((int)xStart, (int)xEnd, 
				 		 (int)yStart, (int)yEnd,
//    					 (int)yStart, (int)yEnd, //since *-1
    					 r, g, b);
    }
    public void FillRectInternal(int xStart, int xEnd,
    		int yStart, int yEnd,
    		byte r, byte g, byte b)
    {
		for (; xStart < xEnd; xStart++)
			for (int y = yStart; y < yEnd; y++)
    			DrawPixel(xStart, y, r, g, b);
    }
}
