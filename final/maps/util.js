// returns a YYYY string representing the storm's year
function getYear(storm) {
    var date = new Date(storm.data[0].date);
    return date.getFullYear();
}