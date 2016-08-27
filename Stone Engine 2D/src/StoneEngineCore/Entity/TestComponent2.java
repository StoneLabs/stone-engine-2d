package StoneEngineCore.Entity;

import StoneEngineCore.DeltaTime;
import StoneEngineRendering.RenderContext;

public class TestComponent2 extends EntityComponent
{
//	float x = 0;
	public void Update(DeltaTime delta)
	{
		Entity parent = GetParent();
		parent.SetY(parent.GetY() + delta.GetDeltaTime(DeltaTime.Unit.SECONDS)/20f);
		parent.SetX(parent.GetX() + delta.GetDeltaTime(DeltaTime.Unit.SECONDS)/20f);

//		x += delta.GetDeltaTime(DeltaTime.Unit.SECONDS);
//		if (x > 1)
//		{ Debug.Log("Currently at " + parent.GetX()); x = 0; }
	}
	public void Render(RenderContext context)
	{
	}
}
