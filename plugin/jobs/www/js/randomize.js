const resultCard = document.getElementById("result-card");
const startButton = document.getElementById("start");
const approveButton = document.getElementById("approve-button");
const nextButton = document.getElementById("next");

function onClickCard(id) {}

function anim(id, cap) {
    const d = document.getElementById("card-container");
    d.style.transform = `translate(-${d.scrollWidth - d.offsetWidth}px)`;


    setTimeout(() => {
        if(getCurrentJobId() != id)
            return;

        d.style.display = "none";
        resultCard.style.display = "flex";

        startButton.hidden = false;
        startButton.innerText = "Start over";
        approveButton.innerText = "Approve";

        if(cap == 0) {
            setVisibleNextButton();
            return;
        }
 
        approveButton.hidden = false;
    }, 2250);
}

function start(id) {
    resultCard.style.display = "none";
    resultCard.innerHTML = "";

    startButton.hidden = true;
    approveButton.hidden = true;
    nextButton.hidden = true;

    const d = document.getElementById("card-container");

    d.style.transform = ``;
    d.style.display = "flex";

    sendPost("/api/attribution", '{"action": "random", "id": ' + id + ', "capability": 0}', (success, response) => {
        if(typeof(response) == "string") {
            return;
        }
        
        if(!success)
            return;
    
        const cap = response["result"]["capability"];
        for(var i = 0; i < cap; i++) {

            const card = document.getElementById(response["result"]["random"][i]["id"]).cloneNode(true);
            card.setAttribute("value", card.getAttribute("id"));

            card.setAttribute("id", card.getAttribute("id") + "-card");
            card.setAttribute("class", "card basic result");
            
            resultCard.appendChild(card);
        }

        if(cap <= 0) {
            const card = document.createElement("div");
            card.setAttribute("class", "card basic result");

            const imgForbidden = document.createElement("img");
            imgForbidden.setAttribute("class", "img-card");
            imgForbidden.src = "../images/job/forbidden.png";
            card.appendChild(imgForbidden);

            const testInfo = document.createElement("h3");
            testInfo.setAttribute("class", "error")
            testInfo.innerText = "No students available for this job !";
            card.appendChild(testInfo);

            resultCard.appendChild(card);

        }
       anim(id, cap);
    });
   
}

function launch() {
    start(document.getElementById("main-job").getAttribute("value"));
}


var first = true;
var index = 0;

function customCard(div) {
    div.setAttribute("class", div.getAttribute("class") + " job-random-card");
    div.setAttribute("onclick", "");

    div.setAttribute("value", div.getAttribute("id"));
    if(!first) {
        div.setAttribute("id", "");
        div.style.display = "none";
    }else {
        div.setAttribute("id", "main-job");
        first = false;
    }

    if(index == 1) {
        const buttonNext = document.createElement("button");
        buttonNext.setAttribute("class", "rand-button");
        buttonNext.setAttribute("onclick", "next(true)");
        buttonNext.innerText = "Next";
        document.getElementById("jobs-selector").appendChild(buttonNext);
    }
    index++;
}

var count = 0;

function approve() {
    var id = document.getElementById("main-job").getAttribute("value");
    const cont = document.getElementById("result-card");


    for(var i = 0; i < cont.childElementCount; i++) {
        sendPost("/api/attribution", '{"action": "add", "id": ' + id + ', "user_id": ' + cont.children[i].getAttribute("value") + '}', (success, response) => {
            if(!success) {
                approveButton.innerText = "Retry";

                return;
            }

            const car = document.getElementById(response["result"]["id"] + "-card");
            car.setAttribute("class", car.getAttribute("class") + " approve-card");

            count++;
            if(count == cont.childElementCount) {
                approveButton.hidden = true;
                approveButton.innerText = "Approve";
                startButton.hidden = true;
                setVisibleNextButton();
                count = 0;
            }
        });
    } 
}

function getCurrentJobId() {
    return document.getElementById("main-job").getAttribute("value");
}

function setVisibleNextButton() {
    if(document.getElementById("jobs-selector").childElementCount < 3) {
        nextButton.innerText = "Finish";
        nextButton.onclick = (e) => {
            window.location = "jobs.html";
        }
    }

    nextButton.hidden = false;

}

function next(fast) {
    if(!fast) {
        document.getElementById("card-container").innerHTML = "";

        load("/api/attribution", "students", "../images/student", "card-container", customCard0, '{"action": "list_random_students"}');
        load("/api/attribution", "students", "../images/student", "card-container", customCard0, '{"action": "list_random_students"}');
    }


    const jobsSelector = document.getElementById("jobs-selector");
    const mainJob = document.getElementById("main-job");

    if(jobsSelector.childElementCount == 3) {
        jobsSelector.children[1].hidden = true;
    }

    mainJob.setAttribute("value", jobsSelector.children[2].getAttribute("value"));
    mainJob.innerHTML = jobsSelector.children[2].innerHTML;

    jobsSelector.removeChild(jobsSelector.children[2]);

    startButton.innerText = "Start";
    approveButton.innerText = "Approve";

    nextButton.hidden = true;
    approveButton.hidden = true;
    startButton.hidden = false;

    resultCard.style.display = "none";

    const d = document.getElementById("card-container");

    d.style.transform = ``;
    d.style.display = "flex";

}