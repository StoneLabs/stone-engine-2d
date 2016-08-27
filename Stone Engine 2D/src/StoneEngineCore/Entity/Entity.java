package StoneEngineCore.Entity;

import java.util.Arrays;
import StoneEngineCore.*;
import StoneEngineSpace.*;
import StoneEngineRendering.*;

public class Entity 
{
	private float m_x;
	private float m_y;
	private AABB  m_aabb;
	private EntityComponent[] m_components;
	
	public float GetX() 			{ return m_x; 		}
	public float GetY() 			{ return m_y; 		}
	public AABB  GetAABB() 			{ return m_aabb; 	}
	
	public void SetX(float x) 		{ m_x = x; 			}
	public void SetY(float y) 		{ m_y = y; 			}
	public void SetAABB(AABB aabb) 	{ m_aabb = aabb; 	}
	
	public Entity(float minX, float minY, float maxX, float maxY)
	{
		m_x = (minX + maxX)/2.0f;
		m_y = (minY + maxY)/2.0f;
		m_aabb = new AABB(minX, minY, maxX, maxY);
		m_components = new EntityComponent[0];
	}
	
	public Entity AddComponent(EntityComponent component)
	{
		m_components = Arrays.copyOf(m_components, m_components.length + 1);
		m_components[m_components.length - 1] = component;
		component.SetParent(this);
		return this;
	}
	
	public Entity UpdateAABB()
	{
		//TODO remove new expression for speed purposes.
		float diffX = m_x - m_aabb.GetCenterX();
		float diffY = m_y - m_aabb.GetCenterY();

//		Debug.Seperator();
//		Debug.Log("" + diffX);
//		Debug.Log("" + diffY);
		
		
		float minX = m_aabb.GetMinX() + diffX;
		float maxX = m_aabb.GetMaxX() + diffX;
		
		float minY = m_aabb.GetMinY() + diffY;
		float maxY = m_aabb.GetMaxY() + diffY;
		
//		Debug.Log("" + minY);
//		Debug.Log("" + maxY);
//		Debug.Seperator();

		m_aabb = new AABB(minX, minY, maxX, maxY);
		return this;
	}
	
	public void Update(DeltaTime delta)
	{
		for (int i = 0; i < m_components.length; i++)
		{
			m_components[i].Update(delta);
		}
	}
	
	public void Render(RenderContext context)
	{
		for (int i = 0; i < m_components.length; i++)
		{
			m_components[i].Render(context);
		}
	}
	
	public boolean IntersectsAABB(Entity entity)
	{
		return m_aabb.IntersectsAABB(entity.GetAABB());
	}
}
