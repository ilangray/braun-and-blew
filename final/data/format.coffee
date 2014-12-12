
_ = require 'lodash'
fs = require 'fs'

# used in the raw data to indicate missing data
MISSING_DATA = -999

# where we should write the output
OUTPUT =
    ALL: "full-hurricane-data.json"
    FILTERED: "filtered-hurricane-data.json"

# split a line on commas and trim each section
splitLine = (line) -> _.invoke line.split(","), "trim"

# reads the file and splits it into trimmed lines
read = (fileName) ->
    data = fs.readFileSync fileName, "utf8"
    lines = _.map data.split("\n"), splitLine

    return lines

# parses a date object from the given date + time
parseDate = (date, time) ->
    year = date[0..3]
    month = date[4..5]
    day = date[6..7]
    hours = time[0..1]
    mins = time[2..3]

    new Date year, month, day, hours, mins

# parses a pair of lat/long coords
parseLatLng = (lat, lng) ->
    # 
    sign = ([num..., dir]) ->
        # console.log "-- num = ", num
        # console.log "-- dir = ", dir
        mult = if dir is "S" or dir is "W" then -1 else 1

        val = parseFloat(num.join(""))
        # console.log "-- val = ", val

        return mult * val

    obj = {
        lat:  sign lat
        long: sign lng
    }

    # console.log "lat,lng = #{lat},#{lng} ====> ", obj

    return obj



# parses a header line
parseHeader = ([id, name, count]) ->
    return {
        id
        name
        count: parseInt count
    }

# parses a data point line
parseDataPoint = ([date, time, recordType, stormType, lat, long, maxWind, minPressure]) ->
    return {
        date: parseDate date, time
        stormType
        location: parseLatLng lat, long
        maxWind: parseInt maxWind
        minPressure: parseInt minPressure
    }

# parses a storm from the given lines and returns the storm plus the remaining lines
parseStorm = (lines) ->
    storm = parseHeader lines[0]
    dataLines = lines[1..storm.count]
    storm.data = _.map dataLines, parseDataPoint

    remainder = lines[storm.count+1..]

    return [storm, remainder]

# parses storms from the input until none remain.
parseAllStorms = (lines) ->
    while lines.length > 1
        [storm, lines] = parseStorm lines
        storm

# prints the given storms
output = (dest, storms, type = "all") ->
    console.log "Dataset '#{type}' contains #{storms.length} storms"

    fs.writeFile dest, JSON.stringify(storms, null, 4), (err) ->
        if err then console.log "Error in writing output = ", err

# removes storms with missing data
filter = (storms) ->
    # a datum is missing data if its maxWind or minPressure is missing
    isDatumMissingData = (datum) ->
        missingWind = datum.maxWind == MISSING_DATA
        missingPressure = datum.minPressure == MISSING_DATA
        return missingWind or missingPressure

    # a storm is missing data if it has at least one data
    # point that is missing a maxWind or minPressure value
    isStormMissingData = (storm) ->
        _.any storm.data, isDatumMissingData

    # reject those storms that are missing data
    _.reject storms, isStormMissingData

# runs the transformation
main = (fileName) ->
    # read input file, chunk into lines
    lines = read fileName

    # parse storms
    storms = parseAllStorms lines

    # output + filter
    output OUTPUT.ALL,      storms,         "All"
    output OUTPUT.FILTERED, filter(storms), "Storms with non-missing data"


# invoke main
main process.argv[2]
