document.addEventListener("DOMContentLoaded", function () {
    const baseUrl = window.location.origin; // Lấy domain hiện tại (http://localhost:8080 hoặc domain production)
    const createForm = document.querySelector("#createListeningForm"); // ví dụ đặt id rõ ràng

    createForm.addEventListener("submit", function (event) {
        event.preventDefault();  // Ngừng hành động mặc định của form (reload trang)

        // Hiển thị spinner khi bắt đầu gọi API
        document.getElementById('loadingSpinner').classList.remove('d-none');

        // Lấy dữ liệu từ form
        const title = document.getElementById("title").value;
        const link = document.getElementById("link").value;

        // Tạo đối tượng dữ liệu cần gửi
        const userData = {
            title: title,
            youtubeUrl: link,
        };

        // Gửi yêu cầu POST đến API
        fetch(`${baseUrl}/api/listening_lesson/create`, {
            method: "POST",
            headers: {
                'Authorization': localStorage.getItem("token"),
                "Content-Type": "application/json",
            },
            body: JSON.stringify(userData)
        })
            .then(response => handleApiResponse(response, (data) => {
                showNotification(data.message || "Tạo bài học thành công.", "success");
            }))
            .catch(error => {
                showNotification("Lỗi khi gửi yêu cầu: " + error.message, "error");
            });
    });
});

