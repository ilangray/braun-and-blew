
// global variable containing the data
DATA = null

// Loads data from JSON, invokes cb with any errors when done
function load(cb) {
	d3.json("filtered-hurricane-data.json", function (error, data) {
		console.log("loaded filtered dataset. retrieved " + data.length + " records.")
		// save data
		DATA = data;

		// invoke cb
		cb(error);

	});
	
}