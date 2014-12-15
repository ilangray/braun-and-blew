
// global variable containing the data
DATA = null

function maxStormSpeed(storms) {
	function maxWindSpeed(storm) {
		return _.max(storm.data, "maxWind").maxWind;
	}

	return _(storms)
		.map(maxWindSpeed)
		.max()
		.value();
}

function minStormSpeed(storms) {
	function minWindSpeed(storm) {
		// console.log("storm = ", storm.data)
		return _.min(storm.data, "maxWind").maxWind
	}

	return _(storms)
		.map(minWindSpeed)
		.min()
		.value();
}

// Loads data from JSON, invokes cb with any errors when done
function load(cb) {
	d3.json("filtered-hurricane-data.json", function (error, data) {
		console.log("loaded filtered dataset. retrieved " + data.length + " records.")
		// save data
		DATA = data;

		console.log("max wind = " + maxStormSpeed(DATA));
		console.log("min wind = " + minStormSpeed(DATA));

		// invoke cb
		cb(error);

	});
	
}