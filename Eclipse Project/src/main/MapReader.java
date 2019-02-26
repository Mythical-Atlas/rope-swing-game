package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator; 
import java.util.Map; 
  
import org.json.simple.JSONArray; 
import org.json.simple.JSONObject; 
import org.json.simple.parser.*; 

import objects.Tile;

// use this:
// https://www.json-generator.com/j/cglqaRcMSW?indent=4

public class MapReader {
	public static Tile[] readTileMap(InputStream path) throws FileNotFoundException, IOException, ParseException {
		JSONObject object = (JSONObject)new JSONParser().parse(new InputStreamReader(path)); 
		
		JSONArray layersArray = (JSONArray)object.get("layers");
		JSONObject layers = (JSONObject)layersArray.get(0);
        JSONArray data = (JSONArray)layers.get("data");
        
        int w = (int)(long)object.get("width");
		int h = (int)(long)object.get("height");
		
		int[] tilemap = new int[w * h];
        int numTiles = 0;
        
        for(int i = 0; i < w * h; i++) {
        	tilemap[i] = (int)(long)data.get(i);
        	if(tilemap[i] == 2) {numTiles++;}
        }
		
		Tile[] output = new Tile[numTiles];
		int index = 0;
		
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				int i = x + (y * w);
				
				if(tilemap[i] == 2) {
					output[index] = new Tile(x * 32, y * 32, 32, 32);
					index++;
				}
			}
		}
		
		System.out.println();
		
		return(output);
	}
}
