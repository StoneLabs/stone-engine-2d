package StoneEngineCore.Entity;

import StoneEngineCore.*;
import StoneEngineSpace.*;
import StoneEngineRendering.*;

public class TestComponent extends EntityComponent
{
	public void Update(DeltaTime delta)
	{
		
	}
	public void Render(RenderContext context)
	{
		AABB parentAABB = GetParent().GetAABB();
		context.FillRect(parentAABB.GetMinX(), parentAABB.GetMinY(),
						 parentAABB.GetMaxX(), parentAABB.GetMaxY(),
						 (byte)255, (byte)0, (byte)0);
	}
}
