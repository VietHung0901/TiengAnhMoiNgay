function handleApiResponse(response, onSuccess) {
    const statusCode = response.status;

    response.json().then(data => {
        const status = data.status;
        const message = data.message;

        // Ẩn spinner khi nhận phản hồi
        document.getElementById('loadingSpinner').classList.add('d-none');

        if (statusCode === 200) {
            if (status === "success") {
                // Gọi callback nếu thành công
                onSuccess(data);
            } else {
                showNotification(message, status);
            }
        } else {
            if (statusCode === 401) {
                alert("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                // XÓA token
                localStorage.removeItem("token");
                window.location.href = "/view/auth/login";
            } else if (statusCode === 403) {
                window.location.href = "/view/error/403";
            } else {
                // Kiểm tra nếu có `errors` dạng object
                if (data.errors) {
                    showNotification(data.errors, "error");
                } else {
                    showNotification(data.message, "error");
                }
            }
        }
    }).catch(error => {
        // Ẩn spinner khi có lỗi
        document.getElementById('loadingSpinner').classList.add('d-none');
        alert("Lỗi khi đọc phản hồi từ server: " + error);
    });
}
