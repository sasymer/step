
/** Creates an <li> element containing text. Use this to show a list of comments for now. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Make an unordered list of all comments in the server, print list onto page. */
function makeCommentList() {
  console.log('Get comment function.');

  fetch('/data').then(response => response.json()).then((commentObj) => {
    const commentSpace = document.getElementById('comment-space');

    for (i in commentObj) {
      commentSpace.appendChild(createListElement(commentObj[i]));
    }
  });
}