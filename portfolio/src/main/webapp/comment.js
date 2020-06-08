/** Creates an <p> element containing text. Use this to show a list of comments for now. */
function createListElement(text) {
  const liElement = document.createElement('p');
  console.log('comment text ' + text);
  var nameComment = makeNameCommentArray(text);
  liElement.innerText = nameComment[1];
  liElement.classList.add("commentList");
  return liElement;
}

/** Create 2 element array of name and corresponding comment. */
function makeNameCommentArray(text) {
  var nameComment = text.split(": ");
  return nameComment;
}

function createNameElement(text) {
  var nameComment = makeNameCommentArray(text);
  var name = nameComment[0];
  const nameElement = document.createElement('p');
  nameElement.innerText = name;
  nameElement.classList.add("commentName");
  return nameElement;
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

    let numberToList = getNumberToList(numberComments, comments);
    clearComments(commentSpace);

    for (let index = 0; index < numberToList; index++) {
      commentSpace.appendChild(createNameElement(comments[index]));
      commentSpace.appendChild(createListElement(comments[index]));
      commentSpace.appendChild(makeHrLine());
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