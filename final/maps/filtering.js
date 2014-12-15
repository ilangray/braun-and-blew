// make the sliders
$yearSlider = new Slider('#year-slider', {});
$speedSlider = new Slider('#speed-slider', {});

console.log("year slider = ", $yearSlider)

// when sliders change, refresh filter
$yearSlider.on("slideStop", refreshFilter)
$speedSlider.on("slideStop", refreshFilter)

// when mode changes, refresh filter after a delay,
// which gives the buttons time to update
$("#mode-btn-group").on('click', function (e) {
	setTimeout(function () {
		refreshFilter();
	}, 5);
});

/// PREDICATES

var inRange = function(val, min, max) {
	return val >= min && val <= max;
}

var generateRangePredicate = function (property, range, fn) {
	if (!fn) {
		fn = _.identity
	}

	var isDataPointInRange = function (obj) {
		var val = fn(obj[property]),
			min = range[0],
			max = range[1];

		return inRange(val, min, max);
	}

	return function (storm) {
		return _.any(storm.data, isDataPointInRange);
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

	// computes the max windspeed of a storm
	var getMaxWindSpeed = function (storm) {
		return _.max(storm.data, "maxWind").maxWind;
	}

	return function (storm) {
		var maxWind = getMaxWindSpeed(storm);
		// console.log("storm = " + storm.name + ", max wind speed = " + maxWind);
		return inRange(maxWind, speedRange[0], speedRange[1]);
	}
}

/// FILTERING

var applyFilter = function (p1, p2) {
	return _.filter(DATA, function (storm) {
		// do both predicates like the storm?
		return p1(storm) && p2(storm);
	});
}

// returns the current dataset, which is the result
// of the current filters applies to DATA
var getCurrentDataset = function () {
	// make predicates
	var yearPredicate = generateYearPredicate();
	var speedPredicate = generateSpeedPredicate();

	// filter the dataset
	return applyFilter(yearPredicate, speedPredicate);
}

/// MODE

var getCurrentMode = function() {
	// get refs to both buttons
	var $heatMode = $('#heat-mode').attr('id');
	var $pathMode = $('#path-mode').attr('id');

	// find current selected button
	var $selected = $('#mode-btn-group .active').attr('id');

	// figure out which one the selected is
	if ($selected === $heatMode) {
		return Map.Modes.Heat;
	} else {
		return Map.Modes.Path;
	}
}

/// REFRESHING / REDRAWING

// redraws everything according to the current filter values
function refreshFilter() { 
	console.log("refreshing filter");

	// get mode, which determines how we render
	var mode = getCurrentMode();
	console.log("mode = ", mode);

	// get storms
	var storms = getCurrentDataset();
	console.log("storms.length = " + storms.length);

	// redraw
	Map.drawStorms(mode, storms);
}