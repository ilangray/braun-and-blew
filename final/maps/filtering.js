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

/// SLIDERS

var Sliders = {
	// returns the range of years from the current values of the year slider
	getYearRange: function getYearRange() {
		var sliderRange = $yearSlider.getValue();

		var minDate = new Date(+sliderRange[0], 0),
			maxDate = new Date(+sliderRange[1], 11, 30);

		console.log("min date = ", minDate);
		console.log("max date = ", maxDate);

		return [minDate, maxDate];
	},

	// returns the range of speeds from the current values of the speeds slider
	getSpeedRange: function getSpeedRange() {
		return $speedSlider.getValue();
	}
}

/// PREDICATES

var inRange = function(val, min, max) {
	return val >= min && val <= max;
}

var generateRangePredicate = function (property, range, fn) {
	if (!fn) {
		fn = _.identity
	}

	console.log("range = ", range)

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

var generateYearPredicate = function (yearRange) {
	return generateRangePredicate("date", yearRange, function (dateString) {
		return new Date(dateString);
	})
}

var generateSpeedPredicate = function (speedRange) {
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

var applyFilter = function (properties) {
	var windRange = properties.wind;
	var yearRange = properties.years;

	console.assert(windRange, "Need a range of wind speeds");
	console.assert(yearRange, "Need a range of years");

	var windPredicate = generateSpeedPredicate(windRange);
	var yearPredicate = generateYearPredicate(yearRange);

	return _.filter(DATA, function (storm) {
		// do both predicates like the storm?
		return yearPredicate(storm) && windPredicate(storm);
	});
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

	// filter
	var storms = applyFilter({
		wind: Sliders.getSpeedRange(),
		years: Sliders.getYearRange()
	});
	console.log("storms.length = " + storms.length);

	// redraw
	Map.render(mode, storms);
}