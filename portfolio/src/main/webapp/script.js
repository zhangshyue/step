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
    fetch('/data').then(response => response.text()).then((comments) => {
        console.log(comments);
        comments = JSON.parse(comments);
        console.log(comments);
        const statsListElement = document.getElementById('comment-container');
        statsListElement.innerHTML = '';
        for(let i = 0; i < comments.length; i++) {
        let obj = comments[i];
        statsListElement.appendChild(
        createListElement(comments[i]));
        console.log(obj);
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text.name+": "+text.comment;
  return liElement;
}

/**
 * Add typing animation effect
 */
class TxtRotate {
    constructor(el, toRotate, period) {
        this.toRotate = toRotate;
        this.el = el;
        this.loopNum = 0;
        this.period = parseInt(period, 10) || 2000;
        this.txt = '';
        this.isDeleting = false;
        this.delta =300 - Math.random() * 100;
    }

    tick() {
        const fullTxt = this.toRotate[0];

        if (this.isDeleting) {
            this.txt = fullTxt.substring(0, this.txt.length - 1);
        } else {
            this.txt = fullTxt.substring(0, this.txt.length + 1);
        }

        this.el.innerHTML = '<span class="wrap">'+this.txt+'</span>';

        let delta = 300 - Math.random() * 100;

        if (this.isDeleting) { 
            delta /= 2; 
            }

        if (!this.isDeleting && this.txt === fullTxt) {
            delta = this.period;
            this.isDeleting = true;
        } else if (this.isDeleting && this.txt === '') {
            this.isDeleting = false;
            this.loopNum++;
            delta = 1000;
        }

        setTimeout(() => {
                    this.tick();
                    }, delta);
                }
}

window.onload = function() {
  const elements = document.getElementsByClassName('txt-rotate');
  for (let i=0; i<elements.length; i++) {
    const toRotate = elements[i].getAttribute('data-rotate');
    const period = elements[i].getAttribute('data-period');
    if (toRotate) {
      const rotate = new TxtRotate(elements[i], JSON.parse(toRotate), period);
      rotate.tick();
    }
  }
};


