function showNotification(message, type = "success", duration = 8000) {
    const toastEl = document.getElementById("toastNotification");
    const toastMessage = document.getElementById("toastMessage");

    // Gán nội dung
    if (typeof message === "string") {
        toastMessage.innerHTML = message;
    } else if (typeof message === "object") {
        toastMessage.innerHTML = Object.entries(message)
            .map(([field, msg]) => `<div><strong>${field}:</strong> ${msg}</div>`)
            .join("");
    }

    // Đặt màu nền
    toastEl.classList.remove("bg-success", "bg-danger");
    toastEl.classList.add(type === "success" ? "bg-success" : "bg-danger");

    // Hiển thị toast với thời gian tuỳ chỉnh
    const toast = new bootstrap.Toast(toastEl, {
        delay: duration,
        autohide: true
    });
    toast.show();
}