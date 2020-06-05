
/** Creates an <p> element containing text. Use this to show a list of comments for now. */
function createListElement(text) {
  const liElement = document.createElement('p');
  console.log('comment text ' + text);
  var nameComment = makeNameCommentArray(text);
  liElement.innerText = nameComment[1];
  liElement.style.paddingLeft = "15px";
  liElement.style.fontSize = "15px";
  return liElement;
}

function makeNameCommentArray(text) {
  var nameComment = text.split(": ");
  return nameComment;
}

function createNameElement(text) {
  var nameComment = makeNameCommentArray(text);
  var name = nameComment[0];
  console.log('NAME ' + name);
  const nameElement = document.createElement('p');
  nameElement.innerText = name;
  nameElement.style.paddingLeft = "15px";
  nameElement.style.fontSize = "18px";
  nameElement.style.fontWeight = "bold";
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

function listComments(address) {
  fetch(address)
  .then(response => response.json()) // Convert to json
  .then((commentObj) => {
    const commentSpace = document.getElementById('comment-space');
    
    // Clear out comments from element.
    while (commentSpace.firstChild) {
      commentSpace.removeChild(commentSpace.firstChild);
    }

    for (i in commentObj) {
      commentSpace.appendChild(createListElement(commentObj[i]));
      const hrLine = document.createElement('hr');
      hrLine.style.borderTop = "1px solid turquoise";
      commentSpace.appendChild(hrLine);
    }
  });
}

function deleteComments() {
  fetch('/delete-data', {method: 'POST'})
  .then(response => response.json())
  .then((commentObj) => {
    allComments();
    location.reload();
    return false;
  });
}

function allComments() {
  listComments('/data');
}


function limitedComments() {
  fetch('/data')
  .then(response => response.json()) // Convert to json
  .then((commentObj) => {
    const commentSpace = document.getElementById('comment-space');
    let numberComments = document.getElementById('choose-num').value; 
    console.log(numberComments); 
    let numToList = 0;
    if (numberComments === 'all') {
      numToList = commentObj.length;
    } else {
      numToList = Math.min(numberComments, commentObj.length);
    }
    console.log('num to list ' + numToList);
    // Clear out comments from element.
    clearComments(commentSpace);

    for (let i = 0; i < numToList; i++) {
      commentSpace.appendChild(createNameElement(commentObj[i]));
      commentSpace.appendChild(createListElement(commentObj[i]));
      const hrLine = document.createElement('hr');
      hrLine.style.borderTop = "1px solid turquoise";
      commentSpace.appendChild(hrLine);
    }
  });
}

function makeHr() {
  const hrLine = document.createElement('hr');
  hrLine.style.borderTop = "1px solid turquoise";
  return hrLine;
}

function clearComments(space) {
  while (space.firstChild) {
    space.removeChild(space.firstChild);
  }
}