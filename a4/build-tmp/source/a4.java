import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class a4 extends PApplet {


Kontroller kontroller;

public void setup() {
  size(600, 400);	
  
  ArrayList<Datum> data = new DerLeser("data_aggregate.csv").readIn();
  kontroller = new Kontroller(data);
}


public void draw() {
  kontroller.render();
}
public class Datum {
	public final int id;
	public final String time;	
	public final String destIP;	
	public final String sourceIP;
	public final String destPort;
	public final String operation;
	public final String priority;
	public final String protocol;
	private boolean marked = false;

	public Datum (int id, String time, String destIP, String sourceIP, 
		String destPort, String operation, String priority, String protocol) {	
		
		this.id = id;
		this.time = time;	
		this.destIP = destIP;	
		this.sourceIP = sourceIP;
		this.destPort = destPort;
		this.operation = operation;
		this.priority = priority;
		this.protocol = protocol;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean m) {
		marked = m;
	}

}
public class DerLeser {
	private final String fileName;

	public DerLeser (String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<Datum> readIn() {
		ArrayList<Datum> toReturn = new ArrayList<Datum>();
		String[] lines = loadStrings(fileName);

		int counter = 0;
		for (String l : lines) {
			if (l.startsWith("Time")) {  // Header
				continue;
			}

			toReturn.add(createDatum(l, counter));

			counter++;
		}

		tPrintOne(toReturn);

		return toReturn;
	}


	// Takes in a string that is comma-separated Datum and makes Datum
	private Datum createDatum(String l, int counter) {
		String[] listL = split(l, ",");

		return new Datum(counter, listL[0], listL[3], listL[1], listL[4], 
			listL[6], listL[5], listL[7]);
	}

	public void tPrintOne(ArrayList<Datum> d) {
		Datum dat = d.get(100);
		println("id = " + dat.id);
		println("time = " + dat.time);
		println("destIP = " + dat.destIP);
		println("sourceIP = " + dat.sourceIP);
		println("destPort = " + dat.destPort);
		println("operation = " + dat.operation);
		println("priority = " + dat.priority);
		println("protocol = " + dat.protocol);
	}

}

class Kontroller {
  
  private final ArrayList<Datum> data;
//  private final Bar bar;
//  private final FDG fdg;
//  private final Line line;
  
  public Kontroller(ArrayList<Datum> data) {
    this.data = data;
    
//    this.bar = null;
//    this.fdg = null;
//    this.line = null;
  } 
  
  public void render() {
    deselectAllData();
    
    // mouse over
    Datum mousedOver = getMousedOverDatum();
    if (mousedOver != null) {
      mousedOver.setMarked(true); 
    }
    
    // render:
    updateGraphPositions();
//    bar.render();
//    line.render();
//    fdg.render(); 
  }
  
  // repositions the graphs based on the current width/height of the screen
  private void updateGraphPositions() {
    
  }
  
  // returns the datum currently moused-over, or null if none.
  private Datum getMousedOverDatum() {
    
    
    
    return null;
  }
  
  private void selectData(ArrayList<Datum> toSelect) {
    for (Datum d : toSelect) {
      d.setMarked(true);
    } 
  }
  
  private void deselectAllData() {
    for (Datum d : data) {
      d.setMarked(false);
    } 
  }
  
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "a4" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
