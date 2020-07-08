/** Creates a map and adds it to the page. */
function createMap() {
    const map = new google.maps.Map(document.getElementById('map'),{center: {lat: 43.494, lng: 12.496}, zoom: 5});
    addLandmark(
        map, 48.857, 2.352, 'Paris',
        '<h4>Paris</h4>' +
        '<p>If you are lucky enough to have lived in Paris as a young man, then wherever you go for the rest of your life it stays with you, for Paris is a moveable feast.</p>')
    addLandmark(
        map, 37.984, 23.727, 'Athens',
        '<h4>Athens</h4>' +
        '<p>Athens is at the heart of Ancient Greece, a powerful civilization and empire. I really enjoyed its juicy fruits.</p>')
    addLandmark(
        map, 36.393, 25.461, 'Sentorini',
        '<h4>Sentorini</h4>' +
        '<p>Sentorini is a lovely island great for taking pictures. But I would not recommend staying there for more than one day.</p>')
    addLandmark(
        map, 35.338, 25.1442, 'Crete',
        '<h4>Crete</h4>' +
        '<p>You can find some of the oldest sites in Europe here. Highly recommend the beaches and seafood.')
    addLandmark(
        map, 41.385, 2.173, 'Barcelona',
        '<h4>Barcelona</h4>' +
        '<p>The architecture of Gaudi is beyond description. But the tickets are really expensive compared to other places I have visited.</p>')
    addLandmark(
        map, 40.416, -3.704, 'Madrid',
        '<h4>Madrid</h4>' +
        '<p>Madrid is similar to some cities in France or Italy. But Spanish dried meat is so good. I really love dried sausage and Jamón.</p>')
}

/** Adds a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description) {
    const marker = new google.maps.Marker({position: {lat: lat, lng: lng}, map: map, title: title});

    const infoWindow = new google.maps.InfoWindow({content: description});
    marker.addListener('click', () => {
        infoWindow.open(map, marker);
    });
}