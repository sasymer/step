// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Adds a random fact about self to the page.
 */
 function addRandomFact() {
   const facts = 
     ["I'm 20 years old", "I've lived in 4 states", 
     "I speak Chinese!", "I love to draw","I am vegetarian", 
     "Some of my favorite shows are Black Mirror, Jane the Virgin, and Killing Eve"];

   // Pick a random fact.
   const fact = facts[Math.floor(Math.random() * facts.length)];

   // Add it to the page.
   const factContainer = document.getElementById('fact-container');
   factContainer.innerText = fact;
 }

/*
* Extends a collapsible element.
*/
function extend() {
  var collapsibleElements = document.getElementsByClassName("collapsible");
  var index;

  //Add click listener to each collapsible element.
  for (index = 0; index < collapsibleElements.length; index++) {
    collapsibleElements[index].addEventListener("click", function() {
      this.classList.toggle("active");
      var content = this.nextElementSibling;

      if (content.style.display === "block") {
        content.style.display = "none"; //Collapse
      } else {
         content.style.display = "block"; //Extend
      }
    });
  }
}

// Get message from server as a json string and add to page.
function getMessage() {
  fetch('/data').then(response => response.json()).then((obj) => {
    console.log(obj.one);
    console.log(obj.two);
    console.log(obj.three);

    const string = obj.one + obj.two + obj.three;
    document.getElementById('greeting-container').innerText = string;
  });
}
