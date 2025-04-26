document.addEventListener("DOMContentLoaded", function () {
    const baseUrl = window.location.origin; // Lấy domain hiện tại (http://localhost:8080 hoặc domain production)
    const registerForm = document.querySelector("form");

    registerForm.addEventListener("submit", function (event) {
        event.preventDefault();  // Ngừng hành động mặc định của form (reload trang)

        // Lấy dữ liệu từ form
        const email = document.getElementById("yourEmail").value;
        const password = document.getElementById("yourPassword").value;

        // Tạo đối tượng dữ liệu cần gửi
        const userData = {
            username: email, // Giả sử "email" là "username"
            password: password,
        };

        // Gửi yêu cầu POST đến API
        fetch(`${baseUrl}/api/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        })
            .then(response => response.json()) // Xử lý phản hồi từ API
            .then(data => {
                if (data.status === "success") {
                    showNotification(data.message, "success");
                    localStorage.setItem("token", data.data.token);
                    setTimeout(() => {
                        window.location.href = "/view/admin/dashboard";
                    }, 1000);
                } else {
                    // Kiểm tra nếu có `errors` dạng object
                    if (data.errors) {
                        showNotification(data.errors, "error");
                    } else {
                        showNotification(data.message, "error");
                    }
                }
            })
            .catch(error => {
                console.error("Error:", error);
                showNotification("Đã xảy ra lỗi khi đăng nhập.", "error");
            });
    });
});
