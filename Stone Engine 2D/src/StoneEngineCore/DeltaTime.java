package StoneEngineCore;

public class DeltaTime
{
	public static enum Unit { NANOSECONDS, MILLISECONDS, SECONDS, FPS }
	
	private final float m_deltaTimeNs;
	private final float m_deltaTimeMs;
	private final float m_deltaTimeS;
	private final float m_currentFPS ;
	
	public DeltaTime(long nanoSeconds)
	{
		m_deltaTimeNs = nanoSeconds;
		m_deltaTimeMs = nanoSeconds / 1000000f;
		m_deltaTimeS  = nanoSeconds / 1000000000f;
		m_currentFPS  = 1000000000f / nanoSeconds;
	}

	public float GetFPS()  		{ return m_currentFPS; 	}
	public float GetDeltaTime() { return m_deltaTimeNs; }
	public float GetDeltaTime(Unit unit) 
	{ 
		switch (unit)
		{
			case NANOSECONDS:
				return m_deltaTimeNs;
			case MILLISECONDS:
				return m_deltaTimeMs;
			case SECONDS:
				return m_deltaTimeS;
			case FPS:
				return m_currentFPS;
			default:
				throw new IllegalArgumentException();
		}
	}
}