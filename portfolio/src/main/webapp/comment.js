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
 * Fetches content from the server and adds it to the DOM.
 */
function getContentFunctions() {
    const num = document.querySelector('[name="number-comments"]').value;
    
    // Check that the input is between 1 and 5.
    if(num > 5 || num < 0) {
        document.getElementById('comment-container').innerHTML = '<h3>Number must be between 1 and 5!</h3>';
        return;
    }
    fetch(`/data?number=${num}`).then(response => response.text()).then((comments) => {
        comments = JSON.parse(comments);
        const statsListElement = document.getElementById('comment-container');
        statsListElement.innerHTML = '';

        for(let i = 0; i < comments.length; i++) {
            statsListElement.appendChild(createListElement(comments[i]));
        }
  });
}

/** Creates an <div> element containing text. */
function createListElement(text) {
    const divElement = document.createElement('div');
    divElement.classList.add('card');
    divElement.innerHTML = `<div class='card-body'>\
                                <h5 class='card-title'>${text.name}</h5>\
                                <p class='card-text'>${text.comment}</p>\
                                <p class='card-text'><small class='text-muted'>${text.commentTime}</small></p>\
                            </div>`;
    return divElement;
}