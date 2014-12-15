
var width = 960,
    height = 620;

var projection = 
    //d3.geo.kavrayskiy7()
    d3.geo.mercator()
    // d3.geo.albers()
    .precision(.1)
    .scale(400)
    .center([-40, 50]);

var path = d3.geo.path()
    .projection(projection);

var graticule = d3.geo.graticule();

// interpolate from green to red for the wind speed scale
var low = d3.rgb(255, 249, 100),
    high = d3.rgb(255, 0, 0),
    interp = d3.interpolateRgb(low, high);

var svg = d3.select(".container").append("svg")
    .attr("width", width)
    .attr("height", height)
    .attr("class", "map");

d3.json("world-110m.json", function(error, world) {
    // renders the actual states
	svg.insert("path", ".graticule")
		// .datum(topojson.merge(world, world.objects.states.geometries))
        .datum(topojson.merge(world, world.objects.countries.geometries))
		.attr("class", "land")
		.attr("d", path);
});

d3.select(self.frameElement).style("height", height + "px");

// clears all hurricanes
function clearHurricanes() {
    svg.selectAll("g").remove()
}

var getPixels = function (location) {
    var latlng = [location.long, location.lat]
    var pxls = projection(latlng);
    
    return {
        x: pxls[0], y: pxls[1]
    }
};

var MAX_WIND = 160;
var MIN_WIND = 10;

// chunks into adjacent pairs
function chunk(datapoints) {
    var chunks = []

    _.each(datapoints, function (d, i) {
        if (i == datapoints.length - 1) {
            return;
        }

        chunks.push([d, datapoints[i+1]])
    });

    // console.log("chunks = ", chunks)

    return chunks;
}

// draws a hurricane as a line through all of the data pts
function drawHurricane(hurricane) {
    var renderLine = d3.svg.line()
        .x(function (d) { 
            // console.log("d = ", d)
            return getPixels(d.location).x; 
        })
        .y(function (d) { 
            return getPixels(d.location).y;
        })
        .interpolate("cardinal")

    var origColor = "hsl(" + Math.random() * 360 + ",100%,50%)";
    var BIG_STROKE = 10
    var LITTLE_STROKE = 1

    // wrap each hurricane in a 'g' tag with the name set
    var g = svg.append("g").attr("name", hurricane.name)

    g.selectAll("path")
        .data(chunk(hurricane.data))
        .enter()
        .append("path")
        .attr("d", function (d) {
            // console.log("drawing a line for datum d = ", d)
            return renderLine(d);
        })
        .attr("stroke", function (d) {
            return interp(d[0].maxWind / MAX_WIND);
        })
        .attr("stroke-width", function (d) {
            return d[0].maxWind / MAX_WIND * 5
        })


/*
    // draw a line through all datapoints
    g.append("path")
        .attr("d", renderLine(hurricane.data))
        .attr("fill", "transparent")
        .attr("stroke", origColor);
    point.on("mouseover", function(d, i) {
            point.attr("stroke", "red")
                 .attr("stroke-width", 10);
        })
        .on("mouseout", function(d, i) {
            point.attr("stroke", origColor)
                 .attr("stroke-width", LITTLE_STROKE);
        })

    // draw circles for each datapoint
    g.selectAll("circle")
        .data(hurricane.data)
        .enter()
        .append("circle")
        .attr("r", function (datum) {
            return 1.5;
        })
        .attr("fill", function (d) {
            return interp(d.maxWind / MAX_WIND);
        })
        .attr("stroke", "transparent")
        .attr("cx", function (datum, i) {
            var latlng = [datum.location.long, datum.location.lat]
            var pxls = projection(latlng);
            return pxls[0]
        })
        .attr("cy", function (datum, i) {
            var latlng = [datum.location.long, datum.location.lat]
            return projection(latlng)[1];
        });
*/
}

// draws the given hurricane
function _drawHurricane(hurricane) {
    var point = svg.append("g").attr("name", hurricane.name).selectAll("circle")
        .data(hurricane.data)
        .enter()
        .append("circle")
        .attr("r", function (datum) {
            return 3; //datum.maxWind / 50
        })
        .attr("fill", "transparent")
        .attr("stroke", "green")
        .attr("cx", function (datum, i) {
            var latlng = [datum.location.long, datum.location.lat]
            var pxls = projection(latlng);
            return pxls[0]
        })
        .attr("cy", function (datum, i) {
            var latlng = [datum.location.long, datum.location.lat]
            return projection(latlng)[1];
        });
}   
