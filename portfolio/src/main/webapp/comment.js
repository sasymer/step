function createListElement(text) {
  const liElement = document.createElement('p');
  var nameCommentTime = getNameCommentTime(text);
  liElement.innerText = nameCommentTime[1];
  liElement.classList.add("commentList");
  return liElement;
}

function createComment(text) {
  const div = document.createElement('div');
  div.appendChild(createNameElement(text));
  div.appendChild(createTimeElement(text));
  div.appendChild(createListElement(text));
  div.appendChild(makeHrLine());

  return div;
}

/** Create 3 element array of name, comment, timestamp. */
function getNameCommentTime(text) {
  var nameCommentTime = text.split(": ");
  return nameCommentTime;
}

function createNameElement(text) {
  var nameCommentTime = getNameCommentTime(text);
  var name = nameCommentTime[0];
  const nameElement = document.createElement('p');
  nameElement.innerText = name;
  nameElement.classList.add("commentName");
  return nameElement;
}

function createTimeElement(text) {
  var nameCommentTime = getNameCommentTime(text);
  var time = nameCommentTime[2];
  var email = nameCommentTime[3];
  const timeElement = document.createElement('p');
  timeElement.innerText = email + " | " + makeTimeString(time);
  timeElement.classList.add("commentTime");
  return timeElement;
}

/** From time in minutes, formats a readable time string in either minutes,
hours, or days. */
function makeTimeString(time) {
  var timeString = '';
  if (time <= 120) {
    timeString = time + " mins ago";
  } else if (time > 120 && time < 2880) {
    time = time / 60;
    timeString = time.toFixed(0) + " hours ago";
  } else {
    time = time / 60 / 24;
    timeString = time.toFixed(0) + " days ago";
  }
  return timeString;
}

/** Make an unordered list of all comments in the server, print list onto page. */
function makeCommentList() {
  console.log('Get comment function.');

  // Make string representing the URL with the number of comments selected by user.
  const str = '/data?number-comments=' + document.getElementById('number-comments').value;
  console.log(str);

  listComments(str);
}

function deleteComments() {
  fetch('/delete-data', {method: 'POST'})
  .then(response => response.json())
  .then((comments) => {
    limitedComments();
    location.reload();
    return false;
  });
}

/** Print number of comments chosen by user, or 5 by default. */
function limitedComments() {
  fetch('/data')
  .then(response => response.json()) // Convert to json
  .then((comments) => {
    const commentSpace = document.getElementById('comment-space');
    let numberComments = document.getElementById('choose-num').value; 
    let order = document.getElementById('choose-order').value;

    let numberToList = getNumberToList(numberComments, comments);
    clearComments(commentSpace);

    if (order === 'newest') {
      for (let index = 0; index < numberToList; index++) {
        commentSpace.appendChild(createComment(comments[index]));
      }
    } else {
      for (let index = comments.length - 1; index >= comments.length - numberToList; index--) {
        commentSpace.appendChild(createComment(comments[index]));
      }
    }
  });
}

function getNumberToList(numberComments, comments) {
  let numberToList = 0;
  if (numberComments === 'all') {
    numberToList = comments.length;
  } else {
    numberToList = Math.min(numberComments, comments.length);
  }
  return numberToList;
}

function makeHrLine() {
  const hrLine = document.createElement('hr');
  hrLine.classList.add('hrLine');
  return hrLine;
}

/** Clear all comments from parameter space, an element in the document. */
function clearComments(space) {
  while (space.firstChild) {
    space.removeChild(space.firstChild);
  }
}

function fetchLogin() {
  fetch('/login')
  .then(response => response.json()) // Convert to json
  .then((status) => {
    // Logged in 
    if (status.login === 'true' && status.email != 'test@example.com') {
      document.getElementById('comment-form').classList.remove("invisible");
      document.getElementById('login-info').classList.add("invisible");
    } else { // Not logged in, redirect to login page
      document.getElementById('comment-form').classList.add("invisible");
      document.getElementById('login-info').classList.remove("invisible");
      location.replace(status.url);
      document.getElementById('comment-form').classList.remove("invisible");
    }
  });
}

function loginIfLoggedIn() {
  fetch('/login')
  .then(response => response.json()) // Convert to json
  .then((status) => {
    // Logged in 
    if (status.login === 'true' && status.email != 'test@example.com') {
      document.getElementById('comment-form').classList.remove("invisible");
      document.getElementById('login-info').classList.add("invisible");
    } 
  });
}

function onLoad() {
  loginIfLoggedIn();
  limitedComments();
}

/** Called when press logout (want to logout) */
function fetchLogout() {
  fetch('/login')
  .then(response => response.json()) // Convert to json
  .then((status) => {
    // Logged in 
    if (status.login === 'true') {
      document.getElementById('comment-form').classList.add("invisible");
      location.replace(status.url); //go to logout page
    } 
  });
}