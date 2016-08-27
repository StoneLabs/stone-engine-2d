import StoneEngineUtil.Debug;

@SuppressWarnings("all")
public class main 
{
	public static void main(String[] args)
	{
		Debug.Log("==START GAME==");
		Game game = new Game();
		game.Run();
		game.Dispose();
		Debug.Log("==TERMINATE==");
	}
}
