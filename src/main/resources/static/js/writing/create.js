document.addEventListener("DOMContentLoaded", function () {
    const baseUrl = window.location.origin; // Lấy domain hiện tại (http://localhost:8080 hoặc domain production)
    const createForm = document.querySelector("#createWritingForm");

    createForm.addEventListener("submit", function (event) {
        event.preventDefault();  // Ngừng hành động mặc định của form (reload trang)

        const fileInput = document.getElementById("file");
        const file = fileInput.files[0];

        // Kiểm tra nếu người dùng chưa chọn file
        if (!file) {
            alert("Vui lòng chọn một file.");
            event.preventDefault();
            return;
        }

        // Giới hạn kích thước file (5MB)
        const maxSizeInBytes = 5 * 1024 * 1024; // 5MB

        if (file.size > maxSizeInBytes) {
            alert("File quá lớn. Kích thước tối đa là 5MB.");
            event.preventDefault(); // Ngăn gửi form
            return;
        }

        // Hiển thị spinner khi bắt đầu gọi API
        document.getElementById('loadingSpinner').classList.remove('d-none');

        // Lấy dữ liệu từ form
        const formData = new FormData();
        formData.append("title", document.getElementById("title").value);
        formData.append("file", document.getElementById("file").files[0]); // lấy file đầu tiên
        formData.append("levelId", document.getElementById("level").value);

        // Gửi yêu cầu POST đến API
        fetch(`${baseUrl}/api/writing_lesson/create`, {
            method: "POST",
            headers: {
                'Authorization': localStorage.getItem("token"),
            },
            body: formData
        })
            .then(response => handleApiResponse(response, (data) => {
                showNotification(data.message || "Tạo bài học thành công.", "success");
            }))
            .catch(error => {
                showNotification("Lỗi khi gửi yêu cầu: " + error.message, "error");
            });
    });
});

