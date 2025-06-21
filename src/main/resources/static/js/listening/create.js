const radioLink = document.getElementById("radioLink");
const radioAudio = document.getElementById("radioAudio");
document.addEventListener("DOMContentLoaded", function () {
    const baseUrl = window.location.origin; // Lấy domain hiện tại (http://localhost:8080 hoặc domain production)
    const createForm = document.querySelector("#createListeningForm"); // ví dụ đặt id rõ ràng

    createForm.addEventListener("submit", function (event) {
        event.preventDefault();  // Ngừng hành động mặc định của form (reload trang)

        // Hiển thị spinner khi bắt đầu gọi API
        document.getElementById('loadingSpinner').classList.remove('d-none');

        if (radioLink.checked) {
            // Lấy dữ liệu từ form
            const title = document.getElementById("title").value;
            const link = document.getElementById("link").value;
            const level = document.getElementById("level").value;
            console.log(level);
            // Tạo đối tượng dữ liệu cần gửi
            const Data = {
                title: title,
                youtubeUrl: link,
                levelId: level,
            };
            console.log(Data);

            // Gửi yêu cầu POST đến API
            fetch(`${baseUrl}/api/listening_lesson/create/link`, {
                method: "POST",
                headers: {
                    'Authorization': localStorage.getItem("token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(Data)
            })
                .then(response => handleApiResponse(response, (data) => {
                    showNotification(data.message || "Tạo bài học thành công.", "success");
                    setTimeout(() => {
                        window.location.href = "/view/admin/listening/list/1";
                    }, 1000);
                }))
                .catch(error => {
                    showNotification("Lỗi khi gửi yêu cầu: " + error.message, "error");
                });
        }
        if (radioAudio.checked) {

            // Hiển thị spinner khi bắt đầu gọi API
            document.getElementById('loadingSpinner').classList.remove('d-none');
            const fileInput = document.getElementById("audioFile");
            const file = fileInput.files[0];

            // Kiểm tra nếu người dùng chưa chọn file
            if (!file) {
                alert("Vui lòng chọn một file.");
                // Ẩn spinner khi nhận phản hồi
                document.getElementById('loadingSpinner').classList.add('d-none');
                event.preventDefault();
                return;
            }

            // Giới hạn kích thước file (5MB)
            const maxSizeInBytes = 5 * 1024 * 1024; // 5MB

            if (file.size > maxSizeInBytes) {
                alert("File quá lớn. Kích thước tối đa là 5MB.");
                document.getElementById('loadingSpinner').classList.add('d-none');
                event.preventDefault(); // Ngăn gửi form
                return;
            }

            // Lấy dữ liệu từ form
            const formData = new FormData();
            formData.append("title", document.getElementById("title").value);
            formData.append("levelId", document.getElementById("level").value);
            formData.append("audioFile", document.getElementById("audioFile").files[0]); // lấy file đầu tiên

            // Gửi yêu cầu POST đến API
            fetch(`${baseUrl}/api/listening_lesson/create/audio`, {
                method: "POST",
                headers: {
                    'Authorization': localStorage.getItem("token"),
                },
                body: formData
            })
                .then(response => handleApiResponse(response, (data) => {
                    showNotification(data.message || "Tạo bài học thành công.", "success");
                    setTimeout(() => {
                        window.location.href = "/view/admin/listening/list/1";
                    }, 1000);
                }))
                .catch(error => {
                    showNotification("Lỗi khi gửi yêu cầu: " + error.message, "error");
                });
        }
    });
});

// Chọn Audio Source
document.addEventListener("DOMContentLoaded", function () {
    const radioLink = document.getElementById("radioLink");
    const radioAudio = document.getElementById("radioAudio");
    const linkInput = document.getElementById("linkInput");
    const audioInput = document.getElementById("audioInput");

    function toggleInputs() {
        if (radioLink.checked) {
            linkInput.classList.remove("d-none");
            audioInput.classList.add("d-none");
        } else {
            linkInput.classList.add("d-none");
            audioInput.classList.remove("d-none");
        }
    }

    radioLink.addEventListener("change", toggleInputs);
    radioAudio.addEventListener("change", toggleInputs);

    // Reset form cũng cần ẩn/hiện đúng
    document.getElementById("createListeningForm").addEventListener("reset", function () {
        setTimeout(() => {
            radioLink.checked = true;
            toggleInputs();
        }, 0);
    });

    // Gọi ban đầu
    toggleInputs();
});

