/** the first element of is the min value of the column
 ** the second element of is the max value of the column
 **/

float[] rangeTempValue = {0, 1};  // slider of temp 
float[] rangeHumidityValue = {0, 1}; // slider of humidity 
float[] rangeWindValue = {0, 1}; // slider of wind 

/**
 * this function is called before initializing the interface
 */
void initSetting() {
    /** Finish this
     **
     ** initialize those three arrays
     ** initialize the first element of each array to the min value of the column
     ** initialize the second element of each array to the max value of the column
     **/

    String sql = "SELECT MIN(temp) AS MinTemp, MAX(temp) AS MaxTemp, MIN(humidity) AS MinHumidity, MAX(humidity) AS MaxHumidity, MIN(wind) AS MinWind, MAX(wind) AS MaxWind from forestfire";
    ResultSet rs = null;
     
    try {
        // submit the sql query and get a ResultSet from the database
        rs = (ResultSet) DBHandler.exeQuery(sql);
        
        rs.next();
         
        rangeTempValue[0] = rs.getFloat("MinTemp");
        rangeTempValue[1] = rs.getFloat("MaxTemp");
        
        rangeHumidityValue[0] = rs.getFloat("MinHumidity");
        rangeHumidityValue[1] = rs.getFloat("MaxHumidity");
        
        rangeWindValue[0] = rs.getFloat("MinWind");
        rangeWindValue[1] = rs.getFloat("MaxWind");
        
    } catch (Exception e) {
        // should be a java.lang.NullPointerException here when rs is empty
        e.printStackTrace();
    } finally {
        closeThisResultSet(rs);
    }
}
