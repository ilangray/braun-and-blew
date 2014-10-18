// Reads in the file
class Configurator {
  public final String fileName;
  
  public Configurator(String fileName) {
    this.fileName = fileName;
  }
  
  // Just reads in lines and puts them in ArrayList -- does nothing else
  public ArrayList<String> read(String fileName) {
    String[] linesNormalArray = loadStrings(fileName);
    ArrayList<String> lines = new ArrayList<String>();
   
   for (int i = 0; i < linesNormalArray.length; i++) {
     lines.add(linesNormalArray[i]);
   } 
   return lines;
  }
  
}
