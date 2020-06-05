
/** Creates an <p> element containing text. Use this to show a list of comments for now. */
function createListElement(text) {
  const liElement = document.createElement('p');
  liElement.innerText = text;
  liElement.style.paddingLeft = "15px";
  liElement.style.fontSize = "18px";
  return liElement;
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
    let numToList = Math.min(numberComments, commentObj.length);
    console.log('num to list ' + numToList);
    // Clear out comments from element.
    clearComments(commentSpace);

    for (let i = 0; i < numToList; i++) {
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