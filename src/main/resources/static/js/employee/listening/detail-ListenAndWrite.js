const subtitlesListAnswer = document.getElementById("subtitles-list-answer");

// Lắng nghe sự kiện nhấn Enter trong textarea
document.getElementById('inputText').addEventListener('keypress', function(event) {
    // Kiểm tra nếu phím nhấn là Enter (code 13)
    if (event.key === 'Enter') {
        // Ngừng hành động mặc định của phím Enter (tạo dòng mới)
        event.preventDefault();

        // Lấy nội dung văn bản nhập vào
        var text = document.getElementById('inputText').value;
        addAnswerToList(text);
        // Xóa nội dung textarea sau khi nhấn Enter
        document.getElementById('inputText').value = '';
    }
});

// Hàm thêm câu trả lời vào danh sách
function addAnswerToList(answer) {
    const listItem = document.createElement("li");
    listItem.classList.add("list-group-item", "user-answer");
    listItem.textContent = answer;

    // Thêm mục vào danh sách
    subtitlesListAnswer.appendChild(listItem);

    // Cuộn xuống dưới cùng để hiển thị câu trả lời mới nhất
    subtitlesListAnswer.scrollTop = subtitlesListAnswer.scrollHeight;
}

