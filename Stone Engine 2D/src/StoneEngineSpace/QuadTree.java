package StoneEngineSpace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import StoneEngineCore.Entity.*;
import StoneEngineUtil.Debug;

// Start with NW and go on clock wise
//0 NW
//1 NE?
//2 SE?
//3 SW?
public class QuadTree 
{
	private QuadTree nodes[];
	private int capacity;
	private ArrayList<Entity> objects;
	private AABB aabb;

	public QuadTree(AABB aabb, int capacity) 
	{
		this.aabb = aabb;
		this.capacity = capacity;
		objects = new ArrayList<>(capacity);
		nodes = null;
	}

	private QuadTree(QuadTree other) 
	{
		this.nodes = other.nodes;
		this.objects = other.objects;
		this.capacity = other.capacity;
		this.aabb = other.aabb;
	}

	public void Add(Entity obj) 
	{
		if (AddInternal(obj)) 
		{
			return;
		}

		float dirX = obj.GetAABB().GetCenterX() - aabb.GetCenterX();
		float dirY = obj.GetAABB().GetCenterY() - aabb.GetCenterY();
		Expand(dirX, dirY);
		Add(obj);
	}

	private void Expand(float dirX, float dirY) 
	{
		QuadTree thisAsNode = new QuadTree(this);

		float minX = aabb.GetMinX();
		float minY = aabb.GetMinY();
		float maxX = aabb.GetMaxX();
		float maxY = aabb.GetMaxY();

		float expanseX = maxX - minX;
		float expanseY = maxY - minY;

		nodes = null;
		objects = new ArrayList<>();

		if (dirX <= 0 && dirY <= 0) 
		{
			aabb = new AABB(minX - expanseX, minY - expanseY, maxX, maxY);
			Subdivide();
			nodes[1] = thisAsNode;
		} else if (dirX <= 0 && dirY > 0) 
		{
			aabb = new AABB(minX - expanseX, minY, maxX, maxY + expanseY);
			Subdivide();
			nodes[3] = thisAsNode;
		} else if (dirX > 0 && dirY > 0) 
		{
			aabb = new AABB(minX, minY, maxX + expanseX, maxY + expanseY);
			Subdivide();
			nodes[2] = thisAsNode;
		} else if (dirX > 0 && dirY <= 0) 
		{
			aabb = new AABB(minX, minY - expanseY, maxX + expanseX, maxY);
			Subdivide();
			nodes[0] = thisAsNode;
		} else 
		{
			Debug.Error("Error: QuadTree direction is invalid (?): " + dirX
							+ " (dirX) " + dirY + " (dirY)");
		}
	}

	private boolean AddInternal(Entity obj) 
	{
		if (!aabb.ContainsAABB(obj.GetAABB())) 
		{
			return false;
		}
		if (nodes == null) 
		{
			if (objects.size() < capacity) 
			{
				objects.add(obj);
				return true;
			}
			Subdivide();
		}
		if (!AddToChildNode(obj)) 
		{
			objects.add(obj);
		}
		return true;
	}

	private void Subdivide() 
	{
		float minX = aabb.GetMinX();
		float minY = aabb.GetMinY();
		float maxX = aabb.GetMaxX();
		float maxY = aabb.GetMaxY();

		float halfXLength = (maxX - minX) / 2.0f;
		float halfYLength = (maxY - minY) / 2.0f;

		nodes = (QuadTree[]) (new QuadTree[4]);

		minY += halfYLength;
		maxX -= halfXLength;
		nodes[0] = new QuadTree(new AABB(minX, minY, maxX, maxY), capacity);

		minX += halfXLength;
		maxX += halfXLength;
		nodes[1] = new QuadTree(new AABB(minX, minY, maxX, maxY), capacity);

		minY -= halfYLength;
		maxY -= halfYLength;
		nodes[3] = new QuadTree(new AABB(minX, minY, maxX, maxY), capacity);

		minX -= halfXLength;
		maxX -= halfXLength;
		nodes[2] = new QuadTree(new AABB(minX, minY, maxX, maxY), capacity);

		ReinsertObjects();
	}

	private void ReinsertObjects() 
	{
		Iterator<Entity> it = objects.iterator();
		while (it.hasNext()) 
		{
			if (AddToChildNode(it.next())) 
			{
				it.remove();
			}
		}
	}

	private boolean AddToChildNode(Entity obj) 
	{
		for (int i = 0; i < nodes.length; i++) 
		{
			if (nodes[i].AddInternal(obj)) 
			{
				return true;
			}
		}
		return false;
	}

	public void Remove(Entity obj) 
	{
		if (!RemoveInternal(obj)) 
		{
			objects.remove(obj);
		}
	}

	private boolean RemoveInternal(Entity obj) 
	{
		if (!aabb.ContainsAABB(obj.GetAABB())) 
		{
			return false;
		}

		if (objects.remove(obj)) 
		{
			return true;
		}

		if (nodes == null) 
		{
			return false;
		}

		for (int i = 0; i < nodes.length; i++) 
		{
			if (nodes[i].RemoveInternal(obj)) 
			{
				Prune();
				return true;
			}
		}

		return false;
	}

	private void Prune() 
	{
		if (!IsNodesEmpty()) 
		{
			return;
		}

		nodes = null;
	}

	private boolean IsNodesEmpty() 
	{
		for (int i = 0; i < nodes.length; i++) 
		{
			if (!nodes[i].IsEmpty()) 
			{
				return false;
			}
		}

		return true;
	}

	private boolean IsEmpty() 
	{
		if (!objects.isEmpty()) 
		{
			return false;
		}

		if (nodes == null) 
		{
			return true;
		}

		return IsNodesEmpty();
	}

	public void Clear() 
	{
		objects.clear();

		if (nodes != null) 
		{
			for (int i = 0; i < nodes.length; i++) 
			{
				nodes[i].Clear();
			}
		}
		nodes = null;
	}

	public Set<Entity> GetAll() 
	{
		return AddAll(new HashSet<Entity>());
	}
	public Set<Entity> GetAll(Set<Entity> result) 
	{
		return AddAll(result);
	}

	private Set<Entity> AddAll(Set<Entity> result) 
	{
		result.addAll(objects);

		if (nodes != null) 
			for (int i = 0; i < nodes.length; i++) 
				nodes[i].AddAll(result);

		return result;
	}

	public Set<Entity> QueryRange(AABB range)
	{
		return QueryRange(new HashSet<Entity>(), range);
	}
	public Set<Entity> QueryRange(Set<Entity> result, AABB range)
	{
		if (!aabb.IntersectsAABB(range))
			return result;

		if (range.ContainsAABB(aabb))
			return AddAll(result);

		if (nodes != null)
			for (int i = 0; i < nodes.length; i++)
				result = nodes[i].QueryRange(result, range);

		for (Entity current : objects)
			if (current.GetAABB().IntersectsAABB(range))
				result.add(current);

		return result;
	}
}


//public class QuadTree 
//private QuadTree m_nodes[];
//private Entity   m_entities[];
//private int 	 m_numEntities;
//private AABB	 m_aabb;
//
////public Entity GetEntity() { return m_entity; }
//
//public QuadTree(AABB aabb, int numChildsPerNode)
//{
//	m_nodes 		= new QuadTree[4];
//	m_entities 		= new Entity[numChildsPerNode];
//	m_numEntities	= 0;
//	m_aabb			= aabb;
//}
//public QuadTree(QuadTree[] nodes, Entity[] entities, 
//				int numEntities, AABB aabb)
//{
//	m_nodes 		= nodes;
//	m_entities 		= entities;
//	m_numEntities	= numEntities;
//	m_aabb			= aabb;
//}
//
//public void Log()
//{
//	Log("\\");
//}
//private void Log(String prefix)
//{
//	for (int e = 0; e < m_numEntities; e++)
//	Debug.Log(prefix + m_entities[e]);
//	if (m_nodes[0] != null)
//		m_nodes[0].Log(prefix + "NW\\");
//	if (m_nodes[1] != null)
//		m_nodes[1].Log(prefix + "NE\\");
//	if (m_nodes[2] != null)
//		m_nodes[2].Log(prefix + "SE\\");
//	if (m_nodes[3] != null)
//		m_nodes[3].Log(prefix + "SW\\");
//}
//
//
//
//public boolean Remove(Entity entity)
//{
//	if (!entity.GetAABB().IntersectAABB(m_aabb))
//		return false;	//NOT IN TREE
//	
//	for (int i = 0; i < m_numEntities; i++)
//		if (m_entities[i] == entity)
//			RemoveEntityFromList(i);
//	
//	for (int i = 0; i < 4; i++)
//		if (m_nodes[i] != null && m_nodes[i].Remove(entity))
//			m_nodes[i]  = null;
//	
//	return IsThisNodeEmpty();
//}
//
//private boolean IsThisNodeEmpty()
//{
//	for (int i = 0; i < 4; i++)
//		if (m_nodes[i] != null)
//			return false;
//	
//	return m_numEntities == 0;
//		
//}
//
//private void RemoveEntityFromList(int index)
//{
//	for (int i = index + 1; i < m_numEntities; i++)
//		m_entities[i-1] = m_entities[i];
//	m_entities[m_numEntities -1] = null;
//	m_numEntities--;
//}
//
//public void Add(Entity entity)
//{
////	Debug.Log(m_numEntities + "/" + m_entities.length);
//	
//}
//
//private void AddInternal(Entity ent)
//{
//	if (!m_aabb.ContainsAABB(ent.GetAABB()))
//		return false;
//	
//	if (m_aabb.IntersectAABB(entity.GetAABB()))
//	{
//		if (m_numEntities < m_entities.length)
//		{
//			Debug.Log("Adding " + entity.toString() + " DONE");
//			m_entities[m_numEntities] = entity;
//			m_numEntities++;
//		}
//		else
//		{
//			AddToChild(entity);
//		}
//	}
//	else
//	{
//		ExpandTowardsEntity(entity);
//		     
//		Add(entity);
////		Log();
//	}
//}
//
//public void ExpandTowardsEntity(Entity entity)
//{
//	QuadTree thisAsNode = new QuadTree(m_nodes, m_entities, m_numEntities, m_aabb);
//	
//	float dirX = entity.GetX() - m_aabb.GetCenterX();
//	float dirY = entity.GetY() - m_aabb.GetCenterY();
//
////	Debug.Log("Entity pos: (" + entity.GetX() + "|" + entity.GetY() + ")");
////	Debug.Log("Tree center: (" + m_aabb.GetCenterX() + "|" + m_aabb.GetCenterY() + ")");
////	Debug.Log("Tree dimensions: (" + m_aabb.GetWidth() + "|" + m_aabb.GetHeight() + ")");
////	Debug.Log("Direction: (" + dirX + "|" + dirY + ")");
//	
//	{
//		//Clean up tree
//		m_numEntities = 0;
//		
//		m_nodes = new QuadTree[4];
//		
//		m_entities = new Entity[m_entities.length];
//	}
//
//	float minX = m_aabb.GetMinX();
//	float minY = m_aabb.GetMinY();
//	
//	float maxX = m_aabb.GetMaxX();
//	float maxY = m_aabb.GetMaxY();
//	
//	float width = maxX - minX;
//	float height = maxX - minX;
//	
//	/*  Warning here!
//	 * The direction of the point is the opposite of the direction in which the
//	 * current the has to be in the expanded tree! So SE direction means NW corner!
//	 * Greater/Less or equal is not necessary since 0 would mean it fits in the tree? 
//	 */
//	     if ( dirX >= 0 && dirY <= 0) // NW corner | SE expansion
//	{ 
////		Debug.Log("expand to SE");
//		m_nodes[0] = thisAsNode;
//		m_aabb = new AABB(minX, 		minY - height, 	maxX + width, 	maxY			);
//	}
//	else if ( dirX <= 0 && dirY <= 0) // NE corner | SW expansion
//	{
////		Debug.Log("expand to SW");
//		m_nodes[1] = thisAsNode;
//		m_aabb = new AABB(minX - width,	minY - height, 	maxX, 			maxY			);
//	}
//	else if ( dirX <= 0 && dirY >= 0) // SE corner | NW expansion
//	{
////		Debug.Log("expand to NW");
//		m_nodes[2] = thisAsNode;
//		m_aabb = new AABB(minX - width,	minY, 			maxX,			maxY + height	);
//	}
//	else if ( dirX >= 0 && dirY >= 0) // SW corner | NE expansion
//	{
////		Debug.Log("expand to NE");
//		m_nodes[3] = thisAsNode;
//		m_aabb = new AABB(minX,			minY, 			maxX + width, 	maxY + height	);
//	}
//	else
//		Debug.Error("Quad tree could not be expanded? [IMPOSSIBLE?]");
//}
////THIS CODE IT BASED ON THE WIKI EXAMPLES
////See wiki for more details... ;D
//private void AddToChild(Entity entity)
//{
////	Debug.Log("Innter child add function");
//	float minX = m_aabb.GetMinX();
//	float minY = m_aabb.GetMinY();
//	
//	float maxX = m_aabb.GetMaxX();
//	float maxY = m_aabb.GetMaxY();
//
//	float halfWidth = (maxX - minX)/2.0f;
//	float halfHeight = (maxX - minX)/2.0f;
//
//	float minXOrg = minX;
//	float minYOrg = minY;
//	float maxXOrg = maxX;
//	float maxYOrg = maxY;
//	
////	Debug.Log("   check... S1 " + minX + " " + minY + " " + maxX + " " + maxY);
//	
//	/*** NORTH WEST CORNER ***/
////	minY = minYOrg + halfHeight; //NORTH
////	maxY = maxYOrg;
////	maxX = maxXOrg - halfWidth;  //WEST
////	minX = minXOrg;
//	
////	Debug.Log("NW check... S1 " + minX + " " + minY + " " + maxX + " " + maxY);
//	TryToAddToChildNode(entity, minXOrg, minYOrg + halfHeight,
//			maxXOrg - halfWidth, maxYOrg, 0);
//
//	/*** NORTH EAST CORNER ***/
////	minY = minYOrg + halfHeight; //NORTH
////	maxY = maxYOrg;
////	minX = minXOrg + halfWidth;  //EAST
////	maxX = maxXOrg;
//
////	Debug.Log("NE check... S1 " + minX + " " + minY + " " + maxX + " " + maxY);
//	TryToAddToChildNode(entity, minXOrg + halfWidth, minYOrg + halfHeight,
//			maxXOrg, maxYOrg, 1);
//
//	/*** SOUTH EAST CORNER ***/
////	maxY = maxYOrg - halfHeight; //SOUTH
////	minY = minYOrg;
////	minX = minXOrg + halfWidth;  //EAST
////	maxX = maxXOrg;
//	
////	Debug.Log("SE check... S1 " + minX + " " + minY + " " + maxX + " " + maxY);
//	TryToAddToChildNode(entity, minXOrg + halfWidth, minYOrg,
//			maxXOrg, maxYOrg - halfHeight, 2);
//
//	/*** SOUTH WEST CORNER ***/
////	maxY = maxYOrg - halfHeight; //SOUTH
////	minY = minYOrg;
////	maxX = maxXOrg - halfWidth;  //WEST
////	minX = minXOrg;
//
////	Debug.Log("SW check... S1 " + minX + " " + minY + " " + maxX + " " + maxY);
//	TryToAddToChildNode(entity, minXOrg, minYOrg,
//			maxYOrg - halfHeight, maxYOrg - halfHeight, 3);
//}
//
//private void TryToAddToChildNode(Entity entity,
//		float minX, float minY, float maxX, float maxY,
//		int nodeIndex)
//{
//	if (entity.GetAABB().IntersectRect(minX, minY, maxX, maxY)) //Check if in corner
//	{
////		Debug.Log("NW check... S2");
//		if (m_nodes[nodeIndex] == null)			//IF corner is not existing, create it!
//		{
//			m_nodes[nodeIndex] = new QuadTree(
//				new AABB(minX, minY, maxX, maxY), m_entities.length
//				);
////			Debug.Log("Created new tree width length " + m_entities.length);
//		}
////		Debug.Log("Adding to tree!");
//		m_nodes[nodeIndex].Add(entity);
//	}
//}
//
//public Set<Entity> GetAll()
//{
//	Set<Entity> result = new HashSet<Entity>();
//	AddAll(result);
//	return result;
//}
//public void AddAll(Set<Entity> result)
//{
//	for (int i = 0; i < m_numEntities; i++)
//	{
//		int s = result.size();
//		result.add(m_entities[i]);
//		Debug.Log("Adding: " + m_entities[i].toString() + (s!=result.size()?"":"   IGNORE"));
//	}
//	
//	for (int i = 0; i < 4; i++)
//		if (m_nodes[i] != null)
//			m_nodes[i].AddAll(result);
//}
//
//public Set<Entity> QueryRange(AABB aabb)
//{
//	Set<Entity> results = new HashSet<Entity>();
//	
//	if (!m_aabb.IntersectAABB(aabb))
//	{
//		Debug.Error("AABB not in quad tree!");
//		return results;
//	}
//	
//	return QueryRangeInternal(aabb, results);
//}
//
//private Set<Entity> QueryRangeInternal(AABB aabb, 
//		Set<Entity> results)
//{
//	if (!m_aabb.IntersectAABB(aabb))
//		return results;
//	
//	for (int e = 0; e < m_numEntities; e++)
//		if (m_entities[e].GetAABB().IntersectAABB(aabb))
//			results.add(m_entities[e]);
//	
//	for (int e = 0; e < 4; e++)
//		if (m_nodes[e] != null)
//			m_nodes[e].QueryRangeInternal(aabb, results);
//	
//	return results;
//}