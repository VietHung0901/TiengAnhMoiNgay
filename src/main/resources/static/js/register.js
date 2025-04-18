document.addEventListener("DOMContentLoaded", function () {
    const baseUrl = document.getElementById("baseUrl").value;
    const registerForm = document.querySelector("form");

    registerForm.addEventListener("submit", function (event) {
        event.preventDefault();  // Ngừng hành động mặc định của form (reload trang)

        var isChecked = document.getElementById("acceptTerms").checked;
        if(!isChecked)
            return;

        // Lấy dữ liệu từ form
        const name = document.getElementById("yourName").value;
        const email = document.getElementById("yourEmail").value;
        const phone = document.getElementById("yourPhone").value;
        const gender = document.getElementById("yourGender").value;
        const birthday = document.getElementById("yourBirthday").value;
        const password = document.getElementById("yourPassword").value;
        const confirmPassword = document.getElementById("yourConfirm-password").value;

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
            gender: gender, // male: 1, female: 0
            birthday: birthday
        };

        // Gửi yêu cầu POST đến API
        fetch(`${baseUrl}/api/auth/register`, {
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
                    setTimeout(() => {
                        window.location.href = "/view/auth/login";
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
                showNotification("Đã xảy ra lỗi khi tạo tài khoản.", "error");
            });
    });
});

