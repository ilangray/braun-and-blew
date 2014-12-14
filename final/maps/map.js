
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

	// draw the first hurricane
    /*
    drawHurricane([
            {
                "date": "2005-09-23T22:00:00.000Z",
                "stormType": "TD",
                "location": {
                    "lat": 23.1,
                    "long": -75.1
                },
                "maxWind": 30,
                "minPressure": 1008
            },
            {
                "date": "2005-09-24T04:00:00.000Z",
                "stormType": "TD",
                "location": {
                    "lat": 23.4,
                    "long": -75.7
                },
                "maxWind": 30,
                "minPressure": 1007
            },
            {
                "date": "2005-09-24T10:00:00.000Z",
                "stormType": "TD",
                "location": {
                    "lat": 23.8,
                    "long": -76.2
                },
                "maxWind": 30,
                "minPressure": 1007
            },
            {
                "date": "2005-09-24T16:00:00.000Z",
                "stormType": "TS",
                "location": {
                    "lat": 24.5,
                    "long": -76.5
                },
                "maxWind": 35,
                "minPressure": 1006
            },
            {
                "date": "2005-09-24T22:00:00.000Z",
                "stormType": "TS",
                "location": {
                    "lat": 25.4,
                    "long": -76.9
                },
                "maxWind": 40,
                "minPressure": 1003
            },
            {
                "date": "2005-09-25T04:00:00.000Z",
                "stormType": "TS",
                "location": {
                    "lat": 26,
                    "long": -77.7
                },
                "maxWind": 45,
                "minPressure": 1000
            },
            {
                "date": "2005-09-25T10:00:00.000Z",
                "stormType": "TS",
                "location": {
                    "lat": 26.1,
                    "long": -78.4
                },
                "maxWind": 50,
                "minPressure": 997
            },
            {
                "date": "2005-09-25T16:00:00.000Z",
                "stormType": "TS",
                "location": {
                    "lat": 26.2,
                    "long": -79
                },
                "maxWind": 55,
                "minPressure": 994
            },
            {
                "date": "2005-09-25T22:00:00.000Z",
                "stormType": "TS",
                "location": {
                    "lat": 26.2,
                    "long": -79.6
                },
                "maxWind": 60,
                "minPressure": 988
            },
            {
                "date": "2005-09-26T02:30:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 26,
                    "long": -80.1
                },
                "maxWind": 70,
                "minPressure": 984
            },
            {
                "date": "2005-09-26T04:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 25.9,
                    "long": -80.3
                },
                "maxWind": 70,
                "minPressure": 983
            },
            {
                "date": "2005-09-26T10:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 25.4,
                    "long": -81.3
                },
                "maxWind": 65,
                "minPressure": 987
            },
            {
                "date": "2005-09-26T16:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 25.1,
                    "long": -82
                },
                "maxWind": 75,
                "minPressure": 979
            },
            {
                "date": "2005-09-26T22:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 24.9,
                    "long": -82.6
                },
                "maxWind": 85,
                "minPressure": 968
            },
            {
                "date": "2005-09-27T04:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 24.6,
                    "long": -83.3
                },
                "maxWind": 90,
                "minPressure": 959
            },
            {
                "date": "2005-09-27T10:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 24.4,
                    "long": -84
                },
                "maxWind": 95,
                "minPressure": 950
            },
            {
                "date": "2005-09-27T16:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 24.4,
                    "long": -84.7
                },
                "maxWind": 100,
                "minPressure": 942
            },
            {
                "date": "2005-09-27T22:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 24.5,
                    "long": -85.3
                },
                "maxWind": 100,
                "minPressure": 948
            },
            {
                "date": "2005-09-28T04:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 24.8,
                    "long": -85.9
                },
                "maxWind": 100,
                "minPressure": 941
            },
            {
                "date": "2005-09-28T10:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 25.2,
                    "long": -86.7
                },
                "maxWind": 125,
                "minPressure": 930
            },
            {
                "date": "2005-09-28T16:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 25.7,
                    "long": -87.7
                },
                "maxWind": 145,
                "minPressure": 909
            },
            {
                "date": "2005-09-28T22:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 26.3,
                    "long": -88.6
                },
                "maxWind": 150,
                "minPressure": 902
            },
            {
                "date": "2005-09-29T04:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 27.2,
                    "long": -89.2
                },
                "maxWind": 140,
                "minPressure": 905
            },
            {
                "date": "2005-09-29T10:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 28.2,
                    "long": -89.6
                },
                "maxWind": 125,
                "minPressure": 913
            },
            {
                "date": "2005-09-29T15:10:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 29.3,
                    "long": -89.6
                },
                "maxWind": 110,
                "minPressure": 920
            },
            {
                "date": "2005-09-29T16:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 29.5,
                    "long": -89.6
                },
                "maxWind": 110,
                "minPressure": 923
            },
            {
                "date": "2005-09-29T18:45:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 30.2,
                    "long": -89.6
                },
                "maxWind": 105,
                "minPressure": 928
            },
            {
                "date": "2005-09-29T22:00:00.000Z",
                "stormType": "HU",
                "location": {
                    "lat": 31.1,
                    "long": -89.6
                },
                "maxWind": 80,
                "minPressure": 948
            },
            {
                "date": "2005-09-30T04:00:00.000Z",
                "stormType": "TS",
                "location": {
                    "lat": 32.6,
                    "long": -89.1
                },
                "maxWind": 50,
                "minPressure": 961
            },
            {
                "date": "2005-09-30T10:00:00.000Z",
                "stormType": "TS",
                "location": {
                    "lat": 34.1,
                    "long": -88.6
                },
                "maxWind": 40,
                "minPressure": 978
            },
            {
                "date": "2005-09-30T16:00:00.000Z",
                "stormType": "TD",
                "location": {
                    "lat": 35.6,
                    "long": -88
                },
                "maxWind": 30,
                "minPressure": 985
            },
            {
                "date": "2005-09-30T22:00:00.000Z",
                "stormType": "TD",
                "location": {
                    "lat": 37,
                    "long": -87
                },
                "maxWind": 30,
                "minPressure": 990
            },
            {
                "date": "2005-10-01T04:00:00.000Z",
                "stormType": "EX",
                "location": {
                    "lat": 38.6,
                    "long": -85.3
                },
                "maxWind": 30,
                "minPressure": 994
            },
            {
                "date": "2005-10-01T10:00:00.000Z",
                "stormType": "EX",
                "location": {
                    "lat": 40.1,
                    "long": -82.9
                },
                "maxWind": 25,
                "minPressure": 996
            }
        ])
*/
});

d3.select(self.frameElement).style("height", height + "px");

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