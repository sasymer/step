const homeCoords = {lat: 29.7604, lng: -95.3698};
const dukeCoords = {lat: 36, lng: -78.9367};
const nemcCoords = {lat: 44.4955, lng: -69.7697};

function createMap() {
  const map = new google.maps.Map(
    document.getElementById('map'),
    {center: {lat: 37.422, lng: -122.084}, zoom: 16}
  );
}

function showLocation(coords, zoomNum) {
  const map = new google.maps.Map(
    document.getElementById('map'),
    {center: coords, zoom: zoomNum}
  );

  var marker = new google.maps.Marker({
    position: coords,
    map: map
  });
}

function showHome() {
  showLocation(homeCoords, 14);
}

function showDuke() {
  showLocation(dukeCoords, 17);
}

function showFavorite() {
  showLocation(nemcCoords, 17);
}