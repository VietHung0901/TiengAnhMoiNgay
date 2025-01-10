document.addEventListener("DOMContentLoaded", function () {
    const registerForm = document.querySelector("form");

    registerForm.addEventListener("submit", function (event) {
        event.preventDefault();  // Ngừng hành động mặc định của form (reload trang)

        // Lấy dữ liệu từ form
        const name = document.getElementById("name").value;
        const email = document.getElementById("email").value;
        const phone = document.getElementById("phone").value;
        const gender = document.getElementById("gender").value;
        const birthday = document.getElementById("birthday").value;
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirm-password").value;

        // Kiểm tra nếu mật khẩu và xác nhận mật khẩu khớp
        if (password !== confirmPassword) {
            alert("Mật khẩu và xác nhận mật khẩu không khớp.");
            return;
        }

        // Tạo đối tượng dữ liệu cần gửi
        const userData = {
            name: name,
            username: email, // Giả sử "email" là "username"
            phone: phone,
            password: password,
            gender: gender === "male" ? 1 : (gender === "female" ? 2 : 3), // Chuyển đổi thành 1/2/3
            birthday: birthday
        };

        // Gửi yêu cầu POST đến API
        fetch(`${baseUrl}/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        })
            .then(response => response.json()) // Xử lý phản hồi từ API
            .then(data => {
                if (data.status === "success") {
                    alert("Tạo tài khoản thành công!");
                    window.location.href = "/auth/login"; // Điều hướng tới trang login sau khi đăng ký thành công
                } else {
                    alert("Tạo tài khoản thất bại: " + data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("Đã xảy ra lỗi khi tạo tài khoản.");
            });
    });
});
