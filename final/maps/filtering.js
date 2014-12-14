// make the sliders
$yearSlider = new Slider('#year-slider', {});
$speedSlider = new Slider('#speed-slider', {});

console.log("year slider = ", $yearSlider)

$yearSlider.on("slideStop", refreshFilter)
$speedSlider.on("slideStop", refreshFilter)

/// PREDICATES

var generateRangePredicate = function (property, range, fn) {
	if (!fn) {
		fn = _.identity
	}

	return function (obj) {
		var value = fn(obj[property]),
			min = range[0],
			max = range[1];

		return value >= min && value <= max;
	}
}

var generateYearPredicate = function () {
	var sliderRange = $yearSlider.getValue();

	var yearRange = [new Date(+sliderRange[0], 0), new Date(+sliderRange[1], 0)]

	console.log("slider range = ", sliderRange);
	console.log("year range = ", yearRange);

	return generateRangePredicate("date", yearRange, function (dateString) {
		return new Date(dateString);
	})
}

var generateSpeedPredicate = function () {
	var speedRange = $speedSlider.getValue();

	console.log("speed range = ", speedRange);

	return generateRangePredicate("maxWind", speedRange);
}

/// FILTERING

var applyFilter = function (p1, p2) {
	return _.filter(DATA, function (storm) {
		return _.any(storm.data, function (d) {
			return p1(d) && p2(d);
		});
	});
}

/// REFRESHING / REDRAWING

var refreshed = false

// invoked whenever the sliders change
function refreshFilter() {	
	console.log("refreshing filter");

	clearHurricanes();

	// make predicates
	var yearPredicate = generateYearPredicate();
	var speedPredicate = generateSpeedPredicate();

	// filter the dataset
	var filtered = applyFilter(yearPredicate, speedPredicate);

	console.log("Filtered the dataset down to " + filtered.length + " elements.")

	// redraw
	_.each(filtered, function (storm, i) {
		// console.log("drawing storm[" + i + "] w/ name = " + storm.name)
		drawHurricane(storm);
	})

	refreshed = true
}