import {lines} from './detail-Load.js';

const subtitlesListAnswer = document.getElementById("subtitles-list-answer");
const audioPlayer = document.getElementById("audio-player");
const inputText = document.getElementById('inputText');
const replayButton = document.getElementById('replayButton');
const nextButton = document.getElementById('nextButton');
const startButton = document.getElementById('startButton');
const subtitlesContainerAnswer = document.getElementById("subtitles-container-answer");

let currentLineIndex = 0;
let isWaitingForAnswer = false;

function playCurrentLine() {
    const line = lines[currentLineIndex];

    if (!line) {
        alert("Đã hoàn thành tất cả các câu!");
        return;
    }

    // Đặt audio player bắt đầu từ thời gian của câu hiện tại
    const startSeconds = timeStringToSeconds(line.startTime);
    const endSeconds = timeStringToSeconds(line.endTime);

    audioPlayer.currentTime = startSeconds;
    audioPlayer.play();

    // Theo dõi audio để dừng khi hết câu
    const interval = setInterval(() => {
        if (audioPlayer.currentTime >= endSeconds) {
            audioPlayer.pause();
            clearInterval(interval);
            isWaitingForAnswer = true;
            inputText.focus();
        }
    }, 100);
}

// Convert "00:00:04,000" => seconds
function timeStringToSeconds(timeString) {
    const [hms, ms] = timeString.split(',');
    const [hours, minutes, seconds] = hms.split(':').map(Number);
    return hours * 3600 + minutes * 60 + seconds + (ms ? parseInt(ms) / 1000 : 0);
}

// Khi người dùng nhấn Enter trong input
inputText.addEventListener('keypress', function (event) {
    if (event.key === 'Enter' && isWaitingForAnswer) {
        event.preventDefault();

        const userAnswer = inputText.value.trim();
        const correctAnswer = lines[currentLineIndex].content.trim();

        addAnswerToList(userAnswer, correctAnswer);
        inputText.value = '';
    }
});


// Thêm câu trả lời và đánh giá đúng/sai
function addAnswerToList(userAnswer, correctAnswer) {
    const listItem = document.createElement("li");
    listItem.classList.add("list-group-item", "user-answer");

    // So sánh từ
    const isCorrect = compareAnswers(userAnswer, correctAnswer);
    listItem.innerHTML = `
        <b>Your Answer:</b> ${userAnswer} <br/>
        <b>Correct:</b> ${correctAnswer} <br/>
        <b>Result:</b> ${isCorrect ? '<span style="color:green;">Correct</span>' : '<span style="color:red;">Incorrect</span>'}
    `;

    subtitlesListAnswer.appendChild(listItem);
    console.log(subtitlesListAnswer);
    subtitlesContainerAnswer.scrollTop = subtitlesContainerAnswer.scrollHeight;
}

// Hàm so sánh đơn giản, bạn có thể nâng cấp thêm
function compareAnswers(user, correct) {
    return user.toLowerCase() === correct.toLowerCase();
}

// Xử lý nút "Nghe lại"
replayButton.addEventListener('click', () => {
    if (!lines[currentLineIndex]) return;

    const startSeconds = timeStringToSeconds(lines[currentLineIndex].startTime);
    const endSeconds = timeStringToSeconds(lines[currentLineIndex].endTime);

    audioPlayer.currentTime = startSeconds;
    audioPlayer.play();

    const interval = setInterval(() => {
        if (audioPlayer.currentTime >= endSeconds) {
            audioPlayer.pause();
            clearInterval(interval);
        }
    }, 100);
});

// Xử lý nút "Nghe lại"
nextButton.addEventListener('click', () => {
    // Chuẩn bị cho câu kế tiếp
    currentLineIndex++;
    isWaitingForAnswer = false;
    setTimeout(() => {
        playCurrentLine();
    }, 1000); // Delay 1s trước khi phát câu tiếp theo
});

startButton.addEventListener('click', () => {
    currentLineIndex = 0;
    // Khởi động lần đầu
    playCurrentLine();
});
