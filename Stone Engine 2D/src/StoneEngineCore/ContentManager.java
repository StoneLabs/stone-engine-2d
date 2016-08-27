package StoneEngineCore;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import StoneEngineRendering.*;
import StoneEngineUtil.*;

public class ContentManager 
{
private final String path;
    
    public ContentManager(String path)
    {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory())
        {
            Debug.Warning("Content Manager path not existing! (" + dir.getPath() + ")");
            
            File parentFile = new File(dir.getParentFile().getPath());
            Debug.Log("Checking for parent directory (" + parentFile.getPath() + ")");
            
            if (!parentFile.exists() || !parentFile.isDirectory())
            {
                Debug.Error("Parent directory missing! Could not create directory!");
                this.path = null;
                return;
            }
            Debug.Log("Creating directory...");
            
            boolean success = dir.mkdir();
            if (!success)
            {
                Debug.Error("Directory could not be created!");
                this.path = null;
                return;
            }
            Debug.Log("Directory created successfully!");
        }
        this.path = path;
    }
    
    public void LogWorkspace()
    {
        Debug.Log("Indexing content manager path...");
        RecursiveLog("{PARENT}\\", path);
    }
    private void RecursiveLog(String Prefix, String path)
    {
        File f = new File(path);
        if (!f.exists())
            return;
            
        File[] files = f.listFiles();
        for (File file : files)
            if (file.isDirectory())
                RecursiveLog(Prefix + file.getName() + "\\", file.getPath());
        for (File file : files)
            if (!file.isDirectory())
                Debug.Log(Prefix + file.getName());
    }
    
    public Bitmap LoadBitmap(String fileName)
    {
        Debug.Log("Loading " + fileName + "...");
        File file = new File(path + "\\" + fileName);
        if (!file.exists() || file.isDirectory())
        {
            Debug.Error("File not found! (" + fileName + ")");
            return null;
        }
        
        try
        {
            BufferedImage image = ImageIO.read(file);
        
            int width = image.getWidth();
            int height = image.getHeight();
            
            int imgPixels[] = new int[width * height];
            image.getRGB(0, 0, width, height, imgPixels, 0, width); //last argument as scanline stride for the rgbArray
            byte[] components = new byte[width * height * 4];
    
            for(int i = 0; i < width * height; i++)
            {
                int pixel = imgPixels[i];
                //components[i * 4]     = (byte)((pixel >> 24) & 0xFF);
                components[i * 4 + 0] = (byte)((pixel >> 24) & 0xFF); // A
                components[i * 4 + 1] = (byte)((pixel      ) & 0xFF); // B
                components[i * 4 + 2] = (byte)((pixel >> 8 ) & 0xFF); // G
                components[i * 4 + 3] = (byte)((pixel >> 16) & 0xFF); // R
            }
            
            return new Bitmap(width, height, components);
        } catch (Exception ex) { Debug.Error("Exception while loading bitmap!"); return null; }
    }
}
