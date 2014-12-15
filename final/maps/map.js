
var Map = (function() {
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
        var orig = _.initial(datapoints);  // drop last
        var shifted = _.rest(datapoints);  // drop first

        return _.zip(orig, shifted);
    }

    /// MODES

    var Modes = {
        Path: 'path',
        Heat: 'heat'
    }

    /// CLEARING

    // clears all hurricanes
    function clearStorms(mode) {
        if (mode == Modes.Path) {
            svg.selectAll("g").remove();
        } else {
            // dont clear the first path, because
            // it is the outline of the land
            svg.selectAll("path").filter(function (d, i) {
                return i > 0
            }).remove()
        }
    }

    /// DRAW

    // draws the storm in the given mode
    function drawStorm(mode, storm) {
        if (mode === Modes.Path) {
            _drawPath(storm);
        } else if (mode === Modes.Heat) {
            _drawHeatmap(storm);
        } else {
            console.assert(false, "Unknown mode = " + mode);
        }
    }

    // draws a hurricane in path mode, i.e. as a line through all data pts
    function _drawPath(storm) {
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

        // draw a line through all datapoints
        var point = svg.append("path")
            .attr("d", renderLine(storm.data))
            .attr("fill", "transparent")
            .attr("stroke", origColor);

        point.on("mouseover", function(d, i) {
                point.attr("stroke", "red")
                     .attr("stroke-width", 10);
            })
            .on("mouseout", function(d, i) {
                point.attr("stroke", origColor)
                     .attr("stroke-width", LITTLE_STROKE);
            });
    }

    // private function to render a storm in MODE.Heatmap
    function _drawHeatmap(storm) {
        var renderLine = d3.svg.line()
            .x(function (d) { 
                // console.log("d = ", d)
                return getPixels(d.location).x; 
            })
            .y(function (d) { 
                return getPixels(d.location).y;
            })
            .interpolate("cardinal")

        // wrap each storm in a 'g' tag with the name set
        var g = svg.append("g").attr("name", storm.name)

        g.selectAll("path")
            .data(chunk(storm.data))
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
    }

    function drawStorms(mode, storms) {
        // clear current storms
        clearStorms(mode);

        // draw new ones in the current mode
        _.each(storms, function (s) {
            drawStorm(mode, s);
        })
    }

    // export a draw function and the modes
    return {
        drawStorms: drawStorms,
        Modes: Modes
    }
})();
