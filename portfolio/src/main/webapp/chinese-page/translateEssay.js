function doTranslate() {
  console.log('translating');
  const text = document.getElementById('chinese-paragraph').textContent;
  console.log(text);

  const resultContainer = document.getElementById('english-paragraph');
  resultContainer.innerText = 'Loading...';

  const params = new URLSearchParams();
  params.append('text', text);

  fetch('/translate', {
    method: 'POST',
    body: params
  }).then(response => response.text())
  .then((translatedMessage) => {
    resultContainer.innerText = translatedMessage;
  });
}
