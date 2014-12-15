// returns a YYYY string representing the storm's year
function getYear(storm) {
    var date = new Date(storm.data[0].date);
    return date.getFullYear();
}

function getName(storm) {
	return storm.name;
}

// storms is an array of objects containing
// 'year' and 'name' properties
function filterStorms(storms) {
	// returns true iff the query is in the given list of storms
	var matches = function (query) {
		var year = getYear(query);
		var name = getName(query);

		return _.any(storms, function (s) {
			return s.year === year && s.name === name;
		});
	}


	return _.filter(DATA, matches);
}