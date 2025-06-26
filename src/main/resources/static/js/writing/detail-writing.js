import {lines} from './detail-Load.js';
const inputText = document.getElementById('studentReply');
const comment = document.getElementById("comment");
let isWaitingForAnswer = false;
let currentIndex = -1;
const baseUrl = window.location.origin;

// Xử lý khi bấm nút "Accept"
document.getElementById("accept").addEventListener("click", function () {

    const answer = inputText.value.trim();
    if (answer === "" || isWaitingForAnswer) {
        alert("Please enter a reply to receive comments!");
        return;
    }

    isWaitingForAnswer = true;
    comment.value = '';
    inputText.readOnly = false;

    // Câu hiện tại
    const oldP = document.querySelector(`#content p[data-index="${currentIndex}"]`);
    if (oldP) {
        oldP.innerHTML = inputText.value; // Cập nhật câu hiện tại thành answer
    }
    inputText.value = '';
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

const commentLoading = document.getElementById("comment-loading");

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

        const viSentence = lines[currentIndex]; // Đề bài hiện tại
        const requestBody = {
            viSentence: viSentence,
            userAnswer: answer
        };

        // Hiển thị spinner loading
        commentLoading.classList.remove("d-none");
        comment.value = "";

        fetch(`${baseUrl}/api/writing_lesson/feedback`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': localStorage.getItem("token")
            },
            body: JSON.stringify(requestBody)
        })
            .then(response => response.json())
            .then(data => {
                comment.value = data.comment || "Không có nhận xét từ AI.";
                commentLoading.classList.add("d-none"); // Ẩn spinner
            })
            .catch(error => {
                console.error("Lỗi khi gửi dữ liệu đến AI:", error);
                comment.value = "❌ Đã xảy ra lỗi khi nhận phản hồi.";
                commentLoading.classList.add("d-none"); // Ẩn spinner
            });
    }
});

