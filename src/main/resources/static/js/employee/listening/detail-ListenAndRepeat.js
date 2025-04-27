import {lines} from './detail-Load.js';

const subtitlesList = document.getElementById("subtitles-list-voice");
const audioPlayer = document.getElementById("audio-player");

// Tải phụ đề vào danh sách
loadSubtitles(lines, audioPlayer, subtitlesList);

// Thiết lập cập nhật thời gian của audio
setupAudioTimeUpdate(lines, audioPlayer, subtitlesList);

// Hàm tải phụ đề
function loadSubtitles(lines, audioPlayer, subtitlesList) {
    lines.forEach(line => {
        const listItem = document.createElement("li");
        listItem.classList.add("list-group-item", "cursor-pointer");
        listItem.textContent = line.content;
        listItem.dataset.startTime = parseTime(line.startTime);

        // Thêm sự kiện click để nhảy đến thời gian cụ thể
        listItem.addEventListener("click", () => {
            audioPlayer.currentTime = listItem.dataset.startTime;
            audioPlayer.play();
        });

        subtitlesList.appendChild(listItem);
    });
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