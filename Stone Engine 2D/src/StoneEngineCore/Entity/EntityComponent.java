package StoneEngineCore.Entity;

import StoneEngineCore.*;
import StoneEngineRendering.*;

public abstract class EntityComponent 
{
	private Entity m_parent;
	
	public void SetParent(Entity entity)
	{
		m_parent = entity;
	}
	
	public Entity GetParent() {return m_parent; }
	
	public abstract void Update(DeltaTime delta);
	public abstract void Render(RenderContext render);
}
