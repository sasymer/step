function createMap() {
  const map = new google.maps.Map(
    document.getElementById('map'),
    {center: {lat: 37.422, lng: -122.084}, zoom: 16}
  );
}

function showHome() {
  const homeCoords = {lat: 29.7604, lng: -95.3698};
  const map = new google.maps.Map(
    document.getElementById('map'),
    {center: homeCoords, zoom: 14}
  );
}