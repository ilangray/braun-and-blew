/*
	Manages switching between tabs and their associated filters/functionality.
*/

// listen for events, so we can change the map content based on the current tab
$('#tabs a').click(function (e) {
	// show the tab
  	e.preventDefault()
  	$(this).tab('show')

  	// update the filter
  	var name = $(this).html();
  	onTabChange(name)
})

// maps tab names to a function that tells the map
// to render the date appropriate for that tab
var FILTERS = {
	"Welcome": function () {
		var katrinas = _.filter(DATA, function (s) {
			return s.name === "KATRINA";
		});

		console.log("welcome page is drawing " + katrinas.length + " katrina storms");

		Map.render(Map.Modes.Path, katrinas)
	},

	"Birth": function () {
		var storms = applyFilter({
			wind: [111, 160],
			years: [new Date(2000,0), new Date(2008,0)]
		})
		Map.drawStorms(Map.Modes.Path, storms)
	},

	"Movement": function () {
		var storms = applyFilter({
			wind: [118, 158],
			years: [new Date(1993,0), new Date(2000,0)]
		})
		Map.drawStorms(Map.Modes.Path, storms)
	},

	"Death": function () {

		var toInclude = [
						{name: "KARL", year: 2004}
						{name: "OPHELIA", year: 2005}
						{name: "ALBERTO", year: 2000}
						{name: "CHARLEY", year: 1986}
						{name: "KATIA", year: 2011}
						{name: "LILI", year: 1996}
						{name: "ISABEL", year: 2003}
						{name: "GLIBERT", year: 1988}
						{name: "IKE", year: 2008}
						{name: "ISAAC", year: 2012}
						{name: "LILI", year: 2002}
						{name: "FRANCES", year: 2004}
						];



		Map.drawStorms(Map.Modes.Heat, storms)
	},

	"Strength": function () {
		var storms = applyFilter({
			wind: [125, 160],
			years: [new Date(1985,0), new Date(2000,0)]
		})
		Map.drawStorms(Map.Modes.Heat, storms)
	},

	"Explore": refreshFilter,
}

function onTabChange(tabName) {
	// update data
	FILTERS[tabName]()
}