
/** Creates an <li> element containing text. Use this to show a list of comments for now. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/** Make an unordered list of all comments in the server, print list onto page. */
function makeCommentList() {
  console.log('Get comment function.');

  const str = '/data?number-comments=' + document.getElementById('number-comments').value;
  console.log(str);

  //todo replace the 6 with the value of the html element
  fetch(str).then(response => response.json()).then((commentObj) => {
    const commentSpace = document.getElementById('comment-space');
    
    while (commentSpace.firstChild) {
      commentSpace.removeChild(commentSpace.firstChild);
    }

    for (i in commentObj) {
      commentSpace.appendChild(createListElement(commentObj[i]));
    }
  });
}