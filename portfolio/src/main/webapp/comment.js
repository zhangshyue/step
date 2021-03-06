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
 * Calls functions when page is loaded.
 */
function onload() {
    getContentFunctions();
    checkLogin();
    fetchBlobstoreUrlAndShowForm();
    addRateWebsite();
}

/**
 * Fetches content from the server and adds it to the DOM.
 */
function getContentFunctions() {
    const num = document.querySelector('[name="number-comments"]').value;
    
    // Check that the input is between 1 and 5.
    if (num > 5 || num < 0) {
        document.getElementById('comment-container').innerHTML = '<h3>Number must be between 1 and 5!</h3>';
        return;
    }
    fetch(`/data?number=${num}`).then(response => response.text()).then((comments) => {
        comments = JSON.parse(comments);
        const statsListElement = document.getElementById('comment-container');
        statsListElement.innerHTML = '';

        for(let i = 0; i < comments.length; i++) {
            statsListElement.appendChild(createListElement(comments[i], i));
        }
  });
}

/** Creates an <div> element containing comment contents. */
function createListElement(text, num) {
    const divElement = document.createElement('div');
    divElement.classList.add('card');

    const cardBodyElement = document.createElement('div');
    cardBodyElement.classList.add('card-body');
    if (text.imgUrl) {
        cardBodyElement.innerHTML = `<h5 class='card-title'>${text.name}</h5>\
                                    <div class='row'>\
                                        <p class='card-text text-muted col-4'>${text.commentTime}</p>\
                                        <div class='show-rating  col-8'></div>\
                                    </div>\
                                    <div>\
                                        <img class='card-img-top rounded-0' src='/blobstore?blob-key=${text.imgUrl}'>\
                                    </div>\
                                    <p class='card-text'>${text.comment}</p>`;
    } else {
        cardBodyElement.innerHTML = `<h5 class='card-title'>${text.name}</h5>\
                                    <div class='row'>\
                                        <p class='card-text text-muted col-4'>${text.commentTime}</p>\
                                        <div class='show-rating  col-8'></div>\
                                    </div>\
                                    <p class='card-text'>${text.comment}</p>`;
    }

    // Modify rating stars color according to each comment's rating. 
    if (text.rating  != 0) {
        const showRatingElement = cardBodyElement.getElementsByClassName('show-rating')[0];
        for (let i = 0; i < 5; i++) {
            const starElement = document.createElement('span');
            if (i < text.rating) {
                starElement.classList.add('fa', 'fa-star', 'checked');
            } else {
                starElement.classList.add('fa', 'fa-star');
            }
            showRatingElement.appendChild(starElement);
        }
    }

    const upvoteElement = document.createElement('p');
    upvoteElement.classList.add('card-text');
    upvoteElement.innerHTML=`<span class='num-upvote ${num}'>${text.upvote}</span>`;

    const upvoteButtonElement = document.createElement('button');
    upvoteButtonElement.classList.add('mx-3','btn');
    upvoteButtonElement.innerText = 'Upvote';
    upvoteButtonElement.addEventListener('click', () => {
        updateUpvote(text, num);
    });

    upvoteElement.appendChild(upvoteButtonElement);
    cardBodyElement.appendChild(upvoteElement);
    divElement.appendChild(cardBodyElement);

    return divElement;
}

/** Update the number of upvotes of the comment that is been upvoted. */
function updateUpvote(text, num) {
    const params = new URLSearchParams();
    params.append('id', text.id);
    fetch('/update-upvote', {method: 'POST', body: params}).then(() => {
        const currentText = document.getElementsByClassName(num)[0].innerText;
        const nextText = parseInt(currentText) + 1;
        document.getElementsByClassName(num)[0].innerText = nextText;
    });
}

function checkLogin() {
    fetch(`/account`).then(response => response.text()).then((account) => {
        account = JSON.parse(account);
        const accountElement = document.getElementsByClassName('account')[0];
        const optionElement = document.getElementsByClassName('btn-group')[0];
        if (account.status === "Login") {
            accountElement.innerText = `Welcome ${account.name}!`;
            optionElement.innerHTML = `<button type='button' class='btn btn-secondary' onclick='location.href="${account.url}"'>Logout</a>\
                                        <button type='button' class='btn btn-secondary' onclick='location.href="/username"'>Set Username</a>`;
        } else {
            accountElement.innerText = 'Please login to comment!';
            optionElement.innerHTML = `<button type='button' class='btn btn-secondary' onclick='location.href="${account.url}"'>Login</a>`;
        } 
  });
}

function fetchBlobstoreUrlAndShowForm() {
    fetch('/blobstore-upload-url').then((response) => {
        return response.text();
    }).then((imageUploadUrl) => {
        const commentForm = document.getElementById('comment-form');
        commentForm.action = imageUploadUrl;
        commentForm.classList.remove('d-none');
    });
}

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

/** Creates a bar chart that shows the rating result and adds it to the page. */
function drawChart() {
    fetch('/rating').then(response => response.text()).then((ratings) => {
        ratings = JSON.parse(ratings);
        let totalNum = 0;
        let totalRating = 0;
        for(let i = 0; i < ratings.length; i++) {
            totalNum += ratings[i];
            totalRating += ratings[i] * (i + 1);
        }
        let averageRating = 0;
        if (totalNum != 0) {
            averageRating = (totalRating / totalNum).toFixed(1);
        }

        const resultElements = document.getElementById('rating-result').getElementsByClassName('display-3')[0];
        resultElements.innerText = averageRating;
        const numReviewsElements = document.getElementById('rating-result').getElementsByClassName('num-reviews')[0];
        numReviewsElements.innerText = totalNum + ' Reviews';
        
        let data = google.visualization.arrayToDataTable([
            ['Rating', 'Total Rating', {role: 'style'}],
            ['5', ratings[4], 'color: #666'],
            ['4', ratings[3], 'color: #666'],
            ['3', ratings[2], 'color: #666'],
            ['2', ratings[1], 'color: #666'],
            ['1', ratings[0], 'color: #666']
        ]);
        let options;
        if (averageRating === 0) {
            options = {
                title: 'Ratings',
                width: 500,
                height: 200,
                legend: {position: 'none'},
                bars: 'horizontal',
                hAxis: {
                    viewWindowMode: 'explicit',
                    viewWindow: {
                        max: 5,              
                        min: 0
                    }
                }
            };
        } else {
            options = {
                title: 'Ratings',
                width: 500,
                height: 200,
                legend: {position: 'none'},
                bars: 'horizontal'
            };
        }
        
        const chart = new google.visualization.BarChart(document.getElementById('chart-container'));
        chart.draw(data, options);
    });
}

/** Make rating stars clickable. */
function addRateWebsite() {
    const starElements = document.getElementById('rating').getElementsByClassName('fa-star');
    for (let i = 0; i < starElements.length; i++) {
        starElements[i].addEventListener('click', () => {
            rateWebsite(i + 1);
        });
    }
}

/** Modify values of rating based on the star that the user clicks. */
function rateWebsite(rating) {
    const ratingElement = document.getElementById('rating-value');
    ratingElement.value = rating;
    const starElements = document.getElementById('rating').getElementsByClassName('fa-star');
    for (let i = 0; i < 5; i++) {
        if (i < rating) {
            starElements[i].classList.add('checked');
        } else {
            starElements[i].classList.remove('checked');
        }
    }
}

