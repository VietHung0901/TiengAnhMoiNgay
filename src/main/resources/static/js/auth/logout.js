document.addEventListener("DOMContentLoaded", function () {
    const baseUrl = window.location.origin; // Lấy domain hiện tại (http://localhost:8080 hoặc domain production)
    const logoutBtn = document.getElementById("btnLogout");

    logoutBtn.addEventListener("click", function (e) {
        e.preventDefault(); // Ngăn chuyển trang

        fetch(`${baseUrl}/api/auth/logout`, {
            method: 'POST',
            headers: {
                'Authorization': localStorage.getItem("token")
            }
        })
            .then(res => res.json())
            .then(data => {
                if (data.status === 'success') {
                    // XÓA token
                    localStorage.removeItem("token");

                    // Chuyển hướng
                    setTimeout(() => {
                        window.location.href = "/view/auth/login";
                    }, 1000);
                } else {
                    alert("Đăng xuất thất bại!", 'error');
                }
            })
            .catch(err => {
                console.error("Logout error", err);
                alert("Có lỗi khi đăng xuất!", 'error');
            });
    });
});
