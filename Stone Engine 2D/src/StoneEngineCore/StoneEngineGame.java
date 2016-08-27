package StoneEngineCore;


import java.util.Iterator;
import java.util.Set;

import StoneEngineCore.Entity.*;
import StoneEngineRendering.*;
import StoneEngineSpace.*;
import StoneEngineUtil.*;

public class StoneEngineGame
{
	@SuppressWarnings("unused")
	private final AABB				m_infiniteAABB = new AABB(Float.MIN_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	
	private final Display 			m_display; 
	
	private final RenderContext		m_frameBuffer;
	
	private final ContentManager 	m_contentManager;
	
	private QuadTree				m_scene;
	
	private boolean 				m_running = true;
	
	public StoneEngineGame() {this(600, 600);}
	public StoneEngineGame(int width, int height)
	{
		Debug.Log("Initializing Game...");
		{
			Debug.Log("Initializing scene...");
			m_scene = new QuadTree(new AABB(-1, -1, 1, 1), 10);
		}
		{
			Debug.Log("Initializing display...");
			m_display     = new Display(width, height);
			m_frameBuffer = m_display.GetFrameBuffer();
		}
		{
			Debug.Log("Initializing content loader...");
			String currentPath = System.getProperty("user.dir") + "\\content\\";
			m_contentManager = new ContentManager(currentPath);
			m_contentManager.LogWorkspace();
		}
		{
			Debug.Log("Calling LoadContent methods...");
			LoadContent(m_contentManager);
			Debug.Log("Calling Initialization methods...");
			Initialization();
			Debug.Log("Found " + m_scene.GetAll().size() + " elemnts in tree!");
		}
	}
	
	public void AddEntity(Entity entity)
	{
		m_scene.Add(entity);
	}
	public void RemoveEntity(Entity entity)
	{
		m_scene.Remove(entity);
	}

	private long m_lastNanoTime = System.nanoTime();
	public final void Run()
	{
		Debug.Log("Starting main game loop...");
		while (m_running)
		{
			long currentNanoTime = System.nanoTime();
			DeltaTime delta = new DeltaTime(currentNanoTime - m_lastNanoTime);
			{
				{
					Update(delta);
					UpdateEntities(delta);
				}
				{
					m_frameBuffer.Clear();
					
					RenderEntities(m_frameBuffer);
					Render(m_frameBuffer);
					
					m_display.SwapBuffers();
				}
			}
			m_lastNanoTime = currentNanoTime;
		}
		Debug.Log("Game stopped!");
	}
	
	public final void Stop()
	{
		Debug.Log("Stopping main game loop...");
		m_running = false;
	}
	
	private final void UpdateEntities(DeltaTime delta)
	{
//		Set<Entity> entities = m_scene.GetAll(new HashSet<Entity>());
		Set<Entity> entities = m_scene.QueryRange(new AABB(-10, -10, 10, 10));
//		Set<Entity> entities = m_scene.QueryRange(m_infiniteAABB);
//		Debug.Log("Query infinite range returns " + entities.size() + " entries!");
//		Iterator<Entity> it = entities.iterator();
		
		for (Entity current : entities)
		{			
			float startX = current.GetX();
			float startY = current.GetY();
			
			current.Update(delta);
			
			if (startX != current.GetX() ||
				startY != current.GetY() )
			{
				m_scene.Remove(current);
				current.UpdateAABB();
				m_scene.Add(current);
			}
		}
	}
	private final void RenderEntities(RenderContext context)
	{
		Set<Entity> entities = m_scene.QueryRange(new AABB(-1, -1, 1, 1));
		Iterator<Entity> it = entities.iterator();
//		Debug.Log(entities.size()+"");
		while (it.hasNext())
		{
			Entity current = it.next();
			current.Render(context);
		}
	}
	
	public void Initialization() 					{ Debug.Warning("Initialization function should be overriden!");	}
	public void LoadContent(ContentManager m) 		{ Debug.Warning("LoadContent function should be overriden!");		}
	public void Update(DeltaTime t) 				{ Debug.Warning("Update function should be overriden!");		 	}
	public void Render(RenderContext c) 			{ Debug.Warning("Render function should be overriden!");		 	}

	public void Dispose()
	{
		m_display.Dispose();
	}
}
