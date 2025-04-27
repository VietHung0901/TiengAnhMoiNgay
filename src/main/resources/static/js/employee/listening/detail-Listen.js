import {lines} from './detail-Load.js';

const subtitlesList = document.getElementById("subtitles-list");
const audioPlayer = document.getElementById("audio-player");

// Tải phụ đề vào danh sách
loadSubtitles(lines, audioPlayer, subtitlesList);

// Thiết lập cập nhật thời gian của audio
setupAudioTimeUpdate(lines, audioPlayer, subtitlesList);

// Hàm tải phụ đề
function loadSubtitles(lines, audioPlayer, subtitlesList) {
    // Thêm CSS cho hiệu ứng và căn chỉnh
    const style = document.createElement("style");
    style.textContent = `
        .recording {
            animation: pulse 1s infinite;
        }
        .playing {
            animation: wave 0.5s infinite;
        }
        @keyframes pulse {
            0% { transform: scale(1); opacity: 1; }
            50% { transform: scale(1.1); opacity: 0.7; }
            100% { transform: scale(1); opacity: 1; }
        }
        @keyframes wave {
            0% { transform: translateY(0); }
            50% { transform: translateY(-3px); }
            100% { transform: translateY(0); }
        }
        .button-container {
            display: flex;
            gap: 5px; /* Khoảng cách giữa các nút */
        }
    `;
    document.head.appendChild(style);

    lines.forEach(line => {
        const listItem = document.createElement("li");
        listItem.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center", "cursor-pointer");

        // Phần nội dung phụ đề
        const textSpan = document.createElement("span");
        textSpan.textContent = line.content;
        textSpan.dataset.startTime = parseTime(line.startTime);

        // Khi click vào nội dung thì phát audio
        textSpan.addEventListener("click", () => {
            audioPlayer.currentTime = textSpan.dataset.startTime;
            audioPlayer.play();
        });

        // Container cho các nút (mic và replay) để đặt ở cuối dòng
        const buttonContainer = document.createElement("div");
        buttonContainer.classList.add("button-container");

        // Nút Mic
        const micButton = document.createElement("button");
        micButton.classList.add("btn", "btn-sm", "btn-outline-primary", "mic-button");
        micButton.innerHTML = `<i class="bi bi-mic"></i>`; // dùng icon Bootstrap mic
        micButton.dataset.startTime = parseTime(line.startTime);

        // Bắt sự kiện click Mic ở đây
        micButton.addEventListener("click", async (e) => {
            e.stopPropagation(); // Ngăn việc click vào span bên ngoài
            audioPlayer.pause();
            // Gọi hàm truy cập micro và ghi âm
            accessMicrophoneAndRecord(micButton);
        });

        // Thêm nút mic vào container
        buttonContainer.appendChild(micButton);

        // Thêm textSpan và container vào listItem
        listItem.appendChild(textSpan);
        listItem.appendChild(buttonContainer);
        subtitlesList.appendChild(listItem);
    });
}

// Hàm để truy cập micro và ghi âm
async function accessMicrophoneAndRecord(micButton) {
    try {
        if (!micButton.dataset.isRecording) {
            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            const mediaRecorder = new MediaRecorder(stream);
            const audioChunks = [];

            mediaRecorder.addEventListener("dataavailable", event => {
                audioChunks.push(event.data);
            });

            micButton.classList.add("recording");
            micButton.dataset.isRecording = "true";

            let stopTimeout;

            mediaRecorder.addEventListener("stop", () => {
                micButton.classList.remove("recording");
                micButton.dataset.isRecording = "";
                clearTimeout(stopTimeout);

                const audioBlob = new Blob(audioChunks);
                const audioUrl = URL.createObjectURL(audioBlob);
                const recordedAudio = new Audio(audioUrl);

                // Gửi voice lên API và cập nhật kết quả vào giao diện
                uploadVoice(audioBlob, micButton);

                // Xử lý nút phát lại
                let replayButton = micButton.parentElement.querySelector(".replay-button");
                if (replayButton) {
                    replayButton.remove();
                }

                replayButton = document.createElement("button");
                replayButton.classList.add("btn", "btn-sm", "btn-outline-success", "replay-button");
                replayButton.innerHTML = `<i class="bi bi-play-circle"></i>`;

                replayButton.addEventListener("click", () => {
                    replayButton.classList.add("playing");
                    recordedAudio.play();
                    recordedAudio.onended = () => {
                        replayButton.classList.remove("playing");
                    };
                });

                micButton.parentElement.appendChild(replayButton);
            });

            mediaRecorder.start();

            stopTimeout = setTimeout(() => {
                mediaRecorder.stop();
            }, 10000);

            const stopRecording = (e) => {
                e.stopPropagation();
                mediaRecorder.stop();
                micButton.removeEventListener("click", stopRecording);
            };
            micButton.addEventListener("click", stopRecording);
        }
    } catch (err) {
        console.error("Error accessing microphone:", err);
        alert("Không thể truy cập Microphone. Hãy kiểm tra quyền truy cập!");
    }
}

// Hàm gửi voice lên API và cập nhật giao diện
async function uploadVoice(audioBlob, micButton) {
    const baseUrl = window.location.origin;
    const formData = new FormData();
    formData.append("voice", audioBlob, "recording.wav");

    // Tạo hoặc lấy thẻ hiển thị kết quả
    let resultSpan = micButton.parentElement.querySelector(".result-span");
    if (!resultSpan) {
        resultSpan = document.createElement("span");
        resultSpan.classList.add("ms-2", "small", "text-muted", "result-span"); // căn lề trái nhỏ
        micButton.parentElement.appendChild(resultSpan);
    }

    // Bắt đầu loading
    resultSpan.textContent = "Đang chấm điểm... ⏳";

    try {
        const response = await fetch(`${baseUrl}/api/listening_lesson/check-voice`, {
            method: "POST",
            headers: {
                'Authorization': localStorage.getItem("token")
            },
            body: formData
        });

        const result = await response.json();
        if (response.ok) {
            // Hiển thị kết quả
            resultSpan.textContent = `${result.data}`;
        } else {
            resultSpan.textContent = `${result.message}`;
        }
    } catch (error) {
        console.error("Error uploading voice:", error);
        resultSpan.textContent = "❌ Lỗi gửi ghi âm.";
    }
}

// Hàm so sánh đơn giản, bạn có thể nâng cấp thêm
function compareAnswers(voiceText, correct) {
    return user.toLowerCase() === correct.toLowerCase();
}

// Hàm thiết lập cập nhật thời gian audio
function setupAudioTimeUpdate(lines, audioPlayer, subtitlesList) {
    audioPlayer.ontimeupdate = function () {
        const currentTime = audioPlayer.currentTime;

        // Đánh dấu phụ đề hiện tại
        lines.forEach((line, index) => {
            const startTime = parseTime(line.startTime);
            const endTime = parseTime(line.endTime);

            if (currentTime >= startTime && currentTime <= endTime) {
                subtitlesList.children[index].classList.add("bg-primary", "text-white");
                subtitlesList.children[index].scrollIntoView({
                    behavior: 'smooth', block: 'center'
                });
            } else {
                subtitlesList.children[index].classList.remove("bg-primary", "text-white");
            }
        });
    };
}

// Hàm trợ giúp để phân tích thời gian từ "hh:mm:ss,SSS" sang giây
function parseTime(timeStr) {
    const [time, milliseconds] = timeStr.split(',');
    const [hours, minutes, seconds] = time.split(':').map(Number);
    const ms = parseInt(milliseconds) / 1000;
    return hours * 3600 + minutes * 60 + seconds + ms;
}