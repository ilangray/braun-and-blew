
var width = 960,
    height = 960;

var projection = 
    //d3.geo.kavrayskiy7()
    d3.geo.mercator()
    // d3.geo.albers()
    .precision(.1);

var path = d3.geo.path()
    .projection(projection);

var graticule = d3.geo.graticule();

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

// draws the given hurricane
function drawHurricane(hurricane) {
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