document.addEventListener("DOMContentLoaded", function () {
    fetch("/api/dashboard/admin", {
        method: "GET",
        headers: {
            "Authorization": localStorage.getItem("token")
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.status !== "success") {
                alert("Không thể tải dashboard");
                return;
            }

            // Cập nhật số liệu
            document.getElementById("userCount").innerText = data.userCount ?? 0;
            document.getElementById("listeningCount").innerText = data.listeningCount ?? 0;
            document.getElementById("writingCount").innerText = data.writingCount ?? 0;
            document.getElementById("readingCount").innerText = data.readingCount ?? 0; // fallback nếu chưa có

            // Cập nhật danh sách người dùng mới
            const newUserList = document.querySelector(".list-group");
            newUserList.innerHTML = ""; // xóa mẫu

            data.latestUsers.forEach(user => {
                const li = document.createElement("li");
                li.className = "list-group-item d-flex justify-content-between align-items-center";
                li.innerHTML = `
                <span>${user.name}</span>
                <span class="badge bg-primary">${formatDate(user.createdAt)}</span>
            `;
                newUserList.appendChild(li);
            });

            // Cập nhật nội dung gần đây
            const latestTable = document.querySelector("#latest tbody");
            latestTable.innerHTML = ""; // Xóa mẫu

            let index = 1;
            const mergeContents = [...data.latestWritings, ...data.latestListenings, ...data.latestReadings];

            // 🟡 Sắp xếp theo thời gian giảm dần (mới nhất trước)
            mergeContents.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

            mergeContents.forEach(content => {
                const tr = document.createElement("tr");
                tr.style.cursor = "pointer";
                tr.addEventListener("click", () => {
                    const role = "admin"; // nếu có nhiều vai trò thì bạn có thể truyền động
                    const type = content.category.toLowerCase(); // listening hoặc writing
                    window.location.href = `/view/${role}/${type}/detail/${content.id}`;
                });

                tr.innerHTML = `
                    <td>${index++}</td>
                    <td>${content.title}</td>
                    <td>${content.category}</td>
                    <td>${formatDate(content.createdAt)}</td>
                `;
                latestTable.appendChild(tr);
            });

        })
        .catch(error => {
            console.error("Lỗi khi gọi API dashboard:", error);
            alert("Không thể tải dữ liệu dashboard.");
        });

    function formatDate(dateStr) {
        const d = new Date(dateStr);
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        return `${day}/${month}/${year}`;
    }
});