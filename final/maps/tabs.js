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
		applyFilter()
	},

	"Explore": refreshFilter,
}

function onTabChange(tabName) {
	// update data
	FILTERS[tabName]()
}