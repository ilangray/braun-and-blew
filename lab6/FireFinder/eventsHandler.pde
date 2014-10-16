
import controlP5.*;
import java.sql.ResultSet;


String table_name = "forestfire";

/**
 * @author: Fumeng Yang
 * @since: 2014
 * we handle the events from the interface for you
 */

void controlEvent(ControlEvent theEvent) {
    if (interfaceReady) {
        if (theEvent.isFrom("checkboxMon") ||
            theEvent.isFrom("checkboxDay")) {
            submitQuery();
        }
        if (theEvent.isFrom("Temp") ||
            theEvent.isFrom("Humidity") ||
            theEvent.isFrom("Wind")) {
            queryReady = true;
        }

        if (theEvent.isFrom("Close")) {
            closeAll();
        }
    }
}

/**
 * generate and submit a query when mouse is released.
 * don't worry about this method
 */
void mouseReleased() {
    if (queryReady == true) {
        submitQuery();
        queryReady = false;
    }
}


String monthQuery() {
  ArrayList<String> selectedOptions = new ArrayList<String>();
   
   for (String option : months) {
     if (checkboxMon.getState(option)) {
       selectedOptions.add(option);
     } 
   }
   
   return inQuery("month", selectedOptions.toArray(new String[0]));
}

String dayQuery() {
  ArrayList<String> selectedOptions = new ArrayList<String>();
   
   for (String option : days) {
     if (checkboxDay.getState(option)) {
       selectedOptions.add(option);
     } 
   }
   
   return inQuery("day", selectedOptions.toArray(new String[0]));
}

String humidityQuery() {
  return rangeQuery("humidity", rangeHumidity.getLowValue(), rangeHumidity.getHighValue());
}

String windQuery() {
  float minWind = rangeWind.getLowValue();
  float maxWind = rangeWind.getHighValue();
 
 return rangeQuery("wind", minWind, maxWind); 
}

String tempQuery() {
  float maxTemp = rangeTemp.getHighValue();
  float minTemp = rangeTemp.getLowValue();
  
  return rangeQuery("temp", minTemp, maxTemp);
}

String rangeQuery(String property, float min, float max) {
  return property + " BETWEEN " + min + " AND " + max; 
}

String inQuery(String property, String[] options) {
  String query = property + " IN (";
  boolean first = true;
  
  for (String option : options) {
    if (!first) {
      query += ", "; 
    }
    
    query += "'" + option + "'"; 
    
    first = false;
  }
  
  if (options.length > 0) {
    query += ", "; 
  }
  
  query += "'dog'";
  
  return query + ")";
}

String andQuery(String... queries) {
  String total = "";
  boolean first = true;
  
  for (String q : queries) {
    if (!first) {
       total += " AND ";
    }
    
    total += q;
    total += " ";
   
    first = false; 
  }
  
  return total + ";";
}

void submitQuery() {
    /**
     ** Finish this
     **/

    /** abstract information from the interface and generate a SQL
     ** use int checkboxMon.getItems().size() to get the number of items in checkboxMon
     ** use boolean checkboxMon.getState(index) to check if an item is checked
     ** use String checkboxMon.getState(index).getName() to get the name of an item
     **
     ** checkboxDay (Mon-Sun) is similar with checkboxMon
     **/
    println("the " + checkboxMon.getItem(0).getName() + " is " + checkboxMon.getState(0));

    /** Finish this
     **
     ** finish the sql
     ** do read information from the ResultSet
     **/
    String cond = andQuery(tempQuery(), windQuery(), humidityQuery(), dayQuery(), monthQuery());
    String sql = "SELECT X, Y from forestfire WHERE " + cond;
    ResultSet rs = null;

    try {
        // submit the sql query and get a ResultSet from the database
       rs  = (ResultSet) DBHandler.exeQuery(sql);
       
       points.clear();
       
       while (rs.next()) {
         points.add(new Point(rs.getFloat("X"), rs.getFloat("Y")));
       }

    } catch (Exception e) {
        // should be a java.lang.NullPointerException here when rs is empty
        e.printStackTrace();
    } finally {
        closeThisResultSet(rs);
    }
}



void closeThisResultSet(ResultSet rs) {
    if(rs == null){
        return;
    }
    try {
        rs.close();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

void closeAll() {
    DBHandler.closeConnection();
    frame.dispose();
    exit();
}
