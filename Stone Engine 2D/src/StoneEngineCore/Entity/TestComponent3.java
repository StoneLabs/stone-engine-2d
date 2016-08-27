package StoneEngineCore.Entity;

import StoneEngineCore.*;
import StoneEngineSpace.*;
import StoneEngineRendering.*;

public class TestComponent3 extends EntityComponent
{
	private final Bitmap m_sprite;
	
	public TestComponent3(Bitmap sprite)
	{
		m_sprite = sprite;
	}
	
	public void Update(DeltaTime delta)
	{
		
	}
	public void Render(RenderContext context)
	{
		AABB parentAABB = GetParent().GetAABB();
		context.DrawImage(parentAABB.GetMinX(), parentAABB.GetMinY(),
						  parentAABB.GetMaxX(), parentAABB.GetMaxY(),
						  m_sprite, RenderContext.TransparencyMode.FULL);
	}
}