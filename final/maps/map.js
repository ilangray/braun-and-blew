
var Map = (function() {
    var JSON_DATA = "world-110m.json";

    var width = 960,
        height = 620;

    var MAX_WIND = 160,
        MIN_WIND = 10;

    var projection = 
        d3.geo.mercator()
        .precision(.1)
        .scale(400)
        .center([-40, 50]);

    var path = d3.geo.path()
        .projection(projection);

    var graticule = d3.geo.graticule();

    // interpolate from green to red for the wind speed scale
    var low = d3.rgb(255, 255, 0),
        high = d3.rgb(255, 0, 0),
        interp = d3.interpolateRgb(low, high);
        interpOther = d3.interpolateRgb(d3.rgb(255, 255, 255),
                                        d3.rgb(0, 0, 0));

    var svg = d3.select(".container").append("svg")
        .attr("width", width)
        .attr("height", height)
        .attr("class", "map");

    // load data and render the countries
    d3.json(JSON_DATA, function(error, world) {
    	_drawCountries(world);
    });

    d3.select(self.frameElement).style("height", height + "px");

    /// UTIL

    // returns the pixels corresponding to the given latlng location
    function getPixels(location) {
        var latlng = [location.long, location.lat]
        var pxls = projection(latlng);
        
        return {
            x: pxls[0], y: pxls[1]
        }
    };

    // returns a YYYY string representing the storm's year
    function getYear(storm) {
        var date = new Date(storm.data[0].date);
        return date.getFullYear();
    }

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
        // remove storms
        svg.selectAll("g.storm").remove();
        
        // remove annotations
        svg.select('#annotations').remove();
    }

    /// DRAW

    function _drawCountries(world) {
        var g = svg.append('g').attr('id', 'countries')
            .insert("path", ".graticule")
            .datum(topojson.merge(world, world.objects.countries.geometries))
            .attr("class", "land")
            .attr("d", path);
    }

    // draws the storm in the given mode
    function _drawStorm(mode, storm) {
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

        var g = svg.append("g").attr("name", storm.name).attr("class", 'storm')

        // draw a line through all datapoints
        var point = g.append("path")
            .attr("d", renderLine(storm.data))
            .attr("fill", "none")
            .attr("stroke", origColor);

        point
            .on("mouseover", function(d, i) {
                point.attr("stroke", "red")
                     .attr("stroke-width", 10);
                renderTooltip(storm);
            })
            .on("mouseout", function(d, i) {
                point.attr("stroke", origColor)
                     .attr("stroke-width", LITTLE_STROKE);
                clearTooltip();
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
        var g = svg.append("g").attr("name", storm.name).attr("class", "storm")

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
            }).on("mouseover", function(d) {
                renderTooltip(storm);
                g.selectAll("path")
                 .attr("stroke", function(d) {
                    return interpOther(d[0].maxWind / MAX_WIND);
                 })
            }).on("mouseout", function(d) {
                clearTooltip();
                g.selectAll("path")
                 .attr("stroke", function(d) {
                    return interp(d[0].maxWind / MAX_WIND);
                 })
            });
    }

    function _drawAnnotations(annotations) {
        console.log("is array ? ", _.isArray(annotations))
        console.log("drawing annotations = ", annotations.length)

        svg.append("g").attr("id", "annotations")
            .selectAll("circle")
            .data(annotations)
            .enter()
            .append("circle")
            .attr("cx", function (d) { 
                console.log("getting cx prop of datum = ", d)
                return d.cx; 
            })
            .attr("cy", function (d) { return d.cy; })
            .attr("r", function (d) { return d.r; })
            .attr("stroke", function (d) { return d.stroke; })
            .attr("stroke-width", 5)
            .attr('fill', 'none')
    }

    function renderTooltip(storm) {
        svg.append("rect")
           .attr("x", 20)
           .attr("y", 20)
           .attr("width", 120)
           .attr("height", 20)
           .attr("fill", "#74BECB");

        svg.append("text")
           .attr("x", 25)
           .attr("y", 35)
           .attr("stroke", "black")
           .attr("fill", "black")
           .text(storm.name + ", " + getYear(storm));
    }

    function clearTooltip() {
        svg.selectAll("rect").remove();
        svg.selectAll("text").remove();
    }

    // draws a list of storms in a given mode
    function render(mode, storms, annotations) {
        // clear current storms
        clearStorms(mode);

        // draw new ones in the current mode
        _.each(storms, function (s) {
            _drawStorm(mode, s);
        })

        // draw annotations
        if (annotations) {
            _drawAnnotations(annotations)
        }
    }

    // export a draw function and the modes
    return {
        render: render,
        Modes: Modes
    }
})();
