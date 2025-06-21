import {lines} from './detail-Load.js';
const inputText = document.getElementById('studentReply');
const comment = document.getElementById("comment");
let isWaitingForAnswer = false;
let currentIndex = -1;

// Xử lý khi bấm nút "Accept"
document.getElementById("accept").addEventListener("click", function () {
    isWaitingForAnswer = true;
    comment.value = '';
    inputText.readOnly = false;

    // Câu hiện tại
    const oldP = document.querySelector(`#content p[data-index="${currentIndex}"]`);
    if (oldP) {
        oldP.innerHTML = inputText.value; // Cập nhật câu hiện tại thành answer
        inputText.value = '';
    }

    if (currentIndex < lines.length - 1) {
        currentIndex++;

        // Câu mới
        const newP = document.querySelector(`#content p[data-index="${currentIndex}"]`);
        if (newP) {
            newP.innerHTML = `<strong>${lines[currentIndex]}</strong>`;
        }
    } else {
        alert("The last sentence has come!");
    }
});

inputText.addEventListener('keypress', function (event){
    if (event.key === 'Enter' && isWaitingForAnswer) {
        event.preventDefault();

        const answer = inputText.value.trim();
        if (answer === "") {
            alert("Please enter a reply to receive comments!");
            return;
        }

        isWaitingForAnswer = false;
        inputText.readOnly = true;

        // xử lý nhận xét câu trả lời
        comment.value = 'Do you understand?';
    }
});