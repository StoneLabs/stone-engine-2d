import java.util.Iterator;
import java.util.Set;

import StoneEngineCore.*;
import StoneEngineCore.Entity.*;
import StoneEngineUtil.*;
import StoneEngineSpace.*;
import StoneEngineRendering.*;

@SuppressWarnings("all")
public class Game extends StoneEngineGame
{
	public Game()
	{
		super(600, 600);
	}
	
	Bitmap img1;
	Bitmap img2;
	public void Initialization() 
	{
//		Stop();
		
//		Debug.Log("Hi there!");
//		Entity test = new Entity(-1f, -1f, 1f, 1f);
//		test.AddComponent(new TestComponent3(img2));
//		AddEntity(test);
//
//		Entity test2 = new Entity(-1f, -1f, -0.25f, -0.5f);
//		test2.AddComponent(new TestComponent3(img1));
//		test2.AddComponent(new TestComponent2());
//		AddEntity(test2);
		
		float range = 50f;
		for (int i = 0; i < 10000; i++)
		{
//			if (i%1000==0)
//				Debug.Log(i + " / 200000");
			float xLoc = ((float)Math.random())*range*2.0f-range;
			float yLoc = ((float)Math.random())*range*2.0f-range;
			Entity ent = new Entity(
					-0.1f + xLoc, -0.1f + yLoc,
					0.1f + xLoc, 0.1f + yLoc
					).AddComponent(new TestComponent3(img2));
//			Debug.Log("Adding " + ent.toString());
			AddEntity(ent);			
		}

		AddEntity(new Entity(
				-1.1f, -1.1f,
				-0.9f, -0.9f
				).AddComponent(new TestComponent3(img2)).AddComponent(new TestComponent2()));		
	}
	
	public void LoadContent(ContentManager contentManager) 	 
	{
		Debug.Log("Reached contentloader!");
//		img = contentManager.LoadBitmap("colorTest.png");
////		for (int i = 0; i < 10; i++)
//			img1 = contentManager.LoadBitmap("TransparencyTest.png");
			img2 = contentManager.LoadBitmap("bricks.jpg");
//		img = new Bitmap(100, 100);
//		for (int x = 0; x < 100; x++)
//			for (int y = 0; y < 100; y++)
//				img.DrawPixel(x, y, (byte)(Math.random()*255f), (byte)(Math.random()*255f), (byte)(Math.random()*255f));
	}
	
	float x1 = 0;
	float x2 = 0;
	public void Update(DeltaTime delta)		 
	{
		x1 += delta.GetDeltaTime(DeltaTime.Unit.SECONDS);
		x2 += delta.GetDeltaTime(DeltaTime.Unit.SECONDS);
		if (x1 >= 1)
		{
			Debug.Log("Reached Update! (" + delta.GetFPS() + "fps |" + delta.GetDeltaTime(DeltaTime.Unit.MILLISECONDS) + "ms)");
			x1 -= 1;
		}
	}
	public void Render(RenderContext frameBuffer)	
	{
//		frameBuffer.Clear();

		if ((int)x2 % 2 == 0)
		{
//			frameBuffer.FillRect(-1f, -1f, -0.9f, -0.9f, (byte)255, (byte)0, (byte)0);
//			frameBuffer.DrawImage(0.9f, -1f, 1f, -0.9f, img);
		}
	}
}
