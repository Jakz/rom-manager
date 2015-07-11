package jack.rm.data.parser;

import java.util.regex.*;

import jack.rm.data.*;

public class ClrMameParser
{
	String buffer;
	
	Rom rom;
	
	public void load(String datFile)
	{
		try
		{
			//FileInputStream fis = new FileInputStream(datFile);
			
			String input =
				"game (" +
						 "name \"Rhythm Tengoku (v01) (JP)[128mbit][sram v110] 2792\"" +
						 "description \"Rhythm Tengoku (v01) (JP)[128mbit][sram v110] 2792\"" +
						 "rom ( name \"Rhythm Tengoku (v01) (JP)[128mbit][sram v110] 2792.gba\" size 16777216 crc A6CD88E1 )";
			
			try (java.util.Scanner scanner = new java.util.Scanner(input))
			{		
			  Pattern pattern = Pattern.compile("\".*?\"");
			  
  			while (scanner.hasNext())
  			{
  				String token = scanner.next();
  				
  				System.out.println("TOKEN: "+token);
  				
  				if (token.equals("game"))
  				{
  					scanner.skip("\\(");
  					if (scanner.next().equals("("))
  					{
  						token = scanner.next();
  						
  						if (token.equals("name"))
  						{
  							token = scanner.next(pattern);
  							System.out.println("name: "+token);
  						}
  						else if (token.equals("description"))
  						{
  							token = scanner.next(pattern);
  							System.out.println("description: "+token);
  						}
  						else if (token.equals("rom"))
  						{
  							token = scanner.next();
  							
  							if (token.equals("name"))
  							{
  								token = scanner.next(pattern);
  								System.out.println("rname: "+token);
  							}
  							else if (token.equals("size"))
  							{
  								token = scanner.next();
  								System.out.println("size: "+token);
  							}
  							else if (token.equals("crc"))
  							{
  								token = scanner.next();
  								System.out.println("crc: "+token);
  								
  							}
  							
  							scanner.next();
  						}
  					}
  					
  					scanner.next();
  				}
  			}
			}
			
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
