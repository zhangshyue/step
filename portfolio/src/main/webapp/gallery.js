/** Creates a map and adds it to the page. */
function createMap() {
    const map = new google.maps.Map(document.getElementById('map'),{center: {lat: 43.494, lng: 12.496}, zoom: 5});
    const paris = new google.maps.Marker({position: {lat: 48.857, lng: 2.352}, map: map});
    const athens = new google.maps.Marker({position: {lat: 37.984, lng: 23.727}, map: map});
    const sentorini = new google.maps.Marker({position: {lat: 36.393, lng: 25.461}, map: map});
    const crete = new google.maps.Marker({position: {lat: 35.338, lng: 25.1442}, map: map});
    const barcelona = new google.maps.Marker({position: {lat: 41.385, lng: 2.173}, map: map});
    const madrid = new google.maps.Marker({position: {lat: 40.416, lng: -3.704}, map: map});
}