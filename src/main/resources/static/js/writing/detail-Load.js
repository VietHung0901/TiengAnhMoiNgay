export var lines = []; // phải để const
const baseUrl = window.location.origin;

document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('loadingSpinner').classList.remove('d-none');

    const detailBreadcrumb = document.getElementById("detail-breadcrumb");

    const pathParts = window.location.pathname.split('/');

    const role = pathParts[2]; // "admin" hoặc "user"

    const id = parseInt(pathParts[pathParts.length - 1]);

    // Update breadcrumb href for Detail link only
    if (detailBreadcrumb) {
        detailBreadcrumb.href = `/view/${role}/writing/detail/${id}`;
    }

    // Fetch lesson data from API
    fetchLessonData(id)
        .then(data => {
            document.querySelector(".card-title").textContent = data.title;
            const docxUrl = baseUrl + data.filePath;
            loadDocxFromUrl(docxUrl);

            // Ẩn spinner khi nhận phản hồi
            document.getElementById('loadingSpinner').classList.add('d-none');
        })
        .catch(error => {
            // Ẩn spinner khi nhận phản hồi
            document.getElementById('loadingSpinner').classList.add('d-none');
            subtitlesList.innerHTML = '<li class="list-group-item text-danger">Error loading content</li>';
            alert("Error loading lessons.");
        });
});

// Hàm fetch dữ liệu bài học
function fetchLessonData(id) {
    return fetch(`${baseUrl}/api/writing_lesson/details/${id}`, {
        method: 'GET', headers: {
            'Authorization': localStorage.getItem("token"), 'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(response => {
            if (response.status === "success") {
                return response.data;
            } else {
                throw new Error('API response status not success');
            }
        });
}

// Tải file word và load lên
async function loadDocxFromUrl(docxUrl) {
    try {
        const response = await fetch(docxUrl, {
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem("token")
            }
        });

        const arrayBuffer = await response.arrayBuffer();
        const result = await mammoth.convertToHtml({ arrayBuffer: arrayBuffer });

        // Tách plain text từ HTML
        const tempDiv = document.createElement("div");
        tempDiv.innerHTML = result.value;
        const plainText = tempDiv.innerText;

        // Tách từng câu
        lines = plainText
            .split(/(?<=[.?!])\s+(?=[A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯ])/)
            .map(s => s.trim())
            .filter(s => s.length > 0);

        // Sau khi lines xong → gán file JS vào
        const script = document.createElement("script");
        script.type = "module"; // nếu file là module
        script.src = "/js/writing/detail-writing.js"; // đường dẫn thật của file
        document.body.appendChild(script);

        renderInitialContent(); // gọi hàm hiển thị
    } catch (error) {
        document.getElementById("content").innerHTML = "<em>Error loading document: " + error.message + "</em>";
    }
}

// Tạo danh sách lines và in đậm câu đầu tiên
function renderInitialContent() {
    const html = lines.map((sentence, index) => {
        return `<p data-index="${index}">${sentence}</p>`;
    }).join("");
    document.getElementById("content").innerHTML = html;
}

// Chỉ cho gõ tiếng anh
document.getElementById("studentReply").addEventListener("input", function () {
    // Chỉ giữ lại ký tự tiếng Anh, số, dấu câu đơn giản
    this.value = this.value.replace(/[^a-zA-Z0-9 .,?!'"()\-:;\n]/g, '');
});