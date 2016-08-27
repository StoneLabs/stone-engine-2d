package StoneEngineSpace;

public class AABB 
{
	private float m_minX;
	private float m_maxX;
	
	private float m_minY;
	private float m_maxY;
	
	public float GetMinX() { return m_minX; }
	public float GetMaxX() { return m_maxX; }
	public float GetMinY() { return m_minY; }
	public float GetMaxY() { return m_maxY; }
	
	public AABB(float minX, float minY, float maxX, float maxY)
	{
		m_minX = minX; m_maxX = maxX;
		m_minY = minY; m_maxY = maxY;
	}
	
	public float GetCenterX() {
		return (m_minX + m_maxX)/2f;
	}
	public float GetCenterY() {
		return (m_minY + m_maxY)/2f;
	}
	public float GetWidth() {
		return m_maxX - m_minX;
	}
	public float GetHeight() {
		return m_maxY - m_minY;
	}

	public boolean ContainsAABB(AABB other)
	{
		return ContainsAABB(other.m_minX, other.m_minY ,
							 other.m_maxX, other.m_maxY);
	}
	public boolean ContainsAABB(float minX, float minY, float maxX, float maxY)
	{
		return ContainsPoint(minX, minY) && ContainsPoint(maxX, maxY);
	}
	
	public boolean ContainsPoint(float x, float y)
	{
		return x >= m_minX && x <= m_maxX &&
			   y >= m_minY && y <= m_maxY ;
	}
	
	public boolean IntersectsAABB(AABB other)
	{
		return IntersectsRect(other.m_minX, other.m_minY ,
							 other.m_maxX, other.m_maxY);
	}
	public boolean IntersectsRect(float minX, float minY, float maxX, float maxY)
	{
		return 	m_minX < maxX && m_maxX > minX && 
				m_minY < maxY && m_maxY > minY ;
				
		//Below you can see how one can waste one hour trying to optimize
		//a simple box collision check... ;D
				
//		float m_centerX = m_minX + m_maxX/2;
//		float  m_widthX = m_maxX - m_minX/2;
//		float   centerX =   minX +   maxX/2;
//		float    widthX =   maxX -   minX/2;
//		
//		float m_centerY = m_minY + m_maxY/2;
//		float  m_widthY = m_maxY - m_minY  ;
//		float   centerY =   minY +   maxY/2;
//		float    widthY =   maxY -   minY  ;
//
//		Debug.Log(m_minX + " " + m_minY + " " + m_maxX + " " + m_maxY);
//		Debug.Log(minX + " " + minY + " " + maxX + " " + maxY);
//
//		Debug.Log(""+ ( 
//			(Math.abs(m_centerX - centerX) * 2 < 
//					(m_widthX + widthX)) &&
//			(Math.abs(m_centerY - centerY) * 2 < 
//					(m_widthY + widthY)) ));
//		return 
//			(Math.abs(m_centerX - centerX) * 2 < 
//					(m_maxX - m_minX + maxX - minX)) &&
//			(Math.abs(m_centerY - centerY) * 2 < 
//					(m_maxY - m_minY + maxY - minY)) ;
//	    boolean x = Abs(a.c[0] - b.c[0]) <= (a.r[0] + b.r[0]);
//	    boolean y = Abs(a.c[1] - b.c[1]) <= (a.r[1] + b.r[1]);
//		return !( minX > m_maxX
//		       || maxX < m_minX
//		       || maxY > m_minY
//		       || minY < m_maxY
//		       )
//			   || 
//				;
		//NOTE: not quite working
//		Debug.Log("(Math.abs("+m_minX+" - "+minX+") * 2 < (Math.abs("+m_maxX+" - "+m_minX+") + Math.abs("+maxX+" - "+minX+")))");
//		Debug.Log("(Math.abs("+(m_minX - minX)+") * 2 < (Math.abs("+(m_maxX - m_minX)+") + Math.abs("+(maxX - minX)+")))");
//		
//		Debug.Log("("+Math.abs(m_minX - minX)+" * 2 < ("+Math.abs(m_maxX - m_minX) + "+" + Math.abs(maxX - minX)+"))");
//		Debug.Log("("+(Math.abs(m_minX - minX) * 2) + " < "+(Math.abs(m_maxX - m_minX) + Math.abs(maxX - minX))+")");
//		
//		Debug.Log("TEST");
//		boolean t = Math.abs(m_minX - minX) * 2 <
//			Math.abs(m_maxX - m_minX) + Math.abs(maxX - minX);
//		Debug.Log(t+"");
//		
//		Debug.Log(""+(Math.abs(m_minY - minY) * 2 < 
//				(m_maxY - m_minY + maxY - minY)));

	}
}
