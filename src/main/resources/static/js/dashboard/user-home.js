document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/dashboard/home", {
        headers: {
            'Authorization': localStorage.getItem("token")
        }
    })
        .then(res => res.json())
        .then(res => {
            if (res.status === "success") {
                const lessons = res.data;
                const list = document.getElementById("recentLessons");
                list.innerHTML = ""; // Clear danh sách cũ nếu có

                lessons.forEach(lesson => {
                    const li = document.createElement("li");
                    li.className = "list-group-item d-flex justify-content-between align-items-center";

                    // Title + lessonType (cùng 1 dòng)
                    const titleWithType = document.createElement("span");
                    titleWithType.innerHTML = `${lesson.title} <span class="badge bg-secondary ms-2">${lesson.lessonType}</span>`;

                    // Nút tiếp tục
                    const link = document.createElement("a");
                    const isListening = lesson.lessonType === "Listening";
                    link.href = isListening
                        ? `/view/user/listening/detail/${lesson.lessonId}`
                        : `/view/user/writing/detail/${lesson.lessonId}`;
                    link.textContent = "Tiếp tục";
                    link.className = `btn btn-sm ${isListening ? "btn-outline-primary" : "btn-outline-success"}`;

                    li.appendChild(titleWithType);
                    li.appendChild(link);
                    list.appendChild(li);
                });
            }
        })
        .catch(error => {
            console.error("Lỗi khi load bài học gần đây:", error);
        });
});
