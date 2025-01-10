document.addEventListener("DOMContentLoaded", function () {
    const registerForm = document.querySelector("form");

    registerForm.addEventListener("submit", function (event) {
        event.preventDefault();  // Ngừng hành động mặc định của form (reload trang)

        // Lấy dữ liệu từ form
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        // Tạo đối tượng dữ liệu cần gửi
        const userData = {
            username: email, // Giả sử "email" là "username"
            password: password,
        };

        // Gửi yêu cầu POST đến API
        fetch(`${baseUrl}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        })
            .then(response => response.json()) // Xử lý phản hồi từ API
            .then(data => {
                if (data.status === "success") {
                    alert("Đăng nhập thành công.");
                    window.location.href = "/index"; // Điều hướng tới trang login sau khi đăng ký thành công
                } else {
                    alert("Đăng nhập thất bại! Lỗi: " + data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("Đã xảy ra lỗi khi đăng nhập.");
            });
    });
});
