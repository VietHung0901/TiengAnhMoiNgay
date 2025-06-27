let originalQuestions = [];
const baseUrl = window.location.origin;

document.addEventListener("DOMContentLoaded", function () {
    document.getElementById('loadingSpinner').classList.remove('d-none');

    const detailBreadcrumb = document.getElementById("detail-breadcrumb");
    const pathParts = window.location.pathname.split('/');
    const role = pathParts[2]; // "admin" hoặc "user"
    const id = parseInt(pathParts[pathParts.length - 1]);

    // Cập nhật breadcrumb cho đúng
    if (detailBreadcrumb) {
        detailBreadcrumb.href = `/view/${role}/reading/detail/${id}`;
    }

    // Gọi API để lấy bài học Reading
    fetchLessonData(id)
        .then(data => {
            console.log(data);
            document.getElementById('title').textContent = data.title;

            const docxUrl = baseUrl + data.filePath;
            loadDocxFromUrl(docxUrl); // Preview file
            originalQuestions = data.questions;
            renderQuestions(data.questions);
            document.getElementById('loadingSpinner').classList.add('d-none');
        })
        .catch(error => {
            document.getElementById('loadingSpinner').classList.add('d-none');
            document.getElementById("preview").innerHTML = '<p class="text-danger">Không thể tải nội dung bài đọc.</p>';
            alert("Lỗi khi tải bài đọc.");
        });
});

// 🟨 Gọi API lấy bài học Reading
function fetchLessonData(id) {
    return fetch(`${baseUrl}/api/reading_lesson/details/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': localStorage.getItem("token"),
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) throw new Error('Không thể kết nối API');
            return response.json();
        })
        .then(response => {
            if (response.status === "success") return response.data;
            else throw new Error(response.message);
        });
}

async function loadDocxFromUrl(docxUrl) {
    try {
        const response = await fetch(docxUrl, {
            method: 'GET',
            headers: {
                'Authorization': localStorage.getItem("token")
            }
        });

        const arrayBuffer = await response.arrayBuffer();
        const result = await mammoth.convertToHtml({ arrayBuffer });

        document.getElementById("preview").innerHTML = result.value;

    } catch (error) {
        document.getElementById("preview").innerHTML = `<em class="text-danger">Lỗi tải tài liệu: ${error.message}</em>`;
    }
}

function renderQuestions(questions) {
    const container = document.getElementById("question-container");
    container.innerHTML = ""; // clear cũ nếu có

    questions.forEach((q, index) => {
        const questionDiv = document.createElement("div");
        questionDiv.className = "question mb-4 pb-3 border-bottom";

        const questionTitle = document.createElement("h6");
        questionTitle.className = "questionText fw-medium";
        questionTitle.textContent = `${index + 1}. ${q.questionText}`;
        questionDiv.appendChild(questionTitle);

        // Kiểm tra số đáp án đúng
        const trueCount = q.options.filter(opt => opt.true).length;
        const inputType = trueCount > 1 ? "checkbox" : "radio";

        q.options.forEach((opt, optIdx) => {
            const optionId = `q${q.id}_opt${optIdx}`;
            const optionWrapper = document.createElement("div");
            optionWrapper.className = "form-check mt-2";

            const input = document.createElement("input");
            input.className = "form-check-input";
            input.type = inputType;
            input.id = optionId;
            input.value = opt.id;
            input.setAttribute("data-option-id", opt.id); // dùng để chấm bài

            // name attribute: nếu radio thì name giống nhau → chỉ chọn 1
            // nếu checkbox thì name là mảng
            input.name = inputType === "radio" ? `q${q.id}` : `q${q.id}[]`;

            const label = document.createElement("label");
            label.className = "form-check-label";
            label.setAttribute("for", optionId);
            label.textContent = opt.optionText;

            optionWrapper.appendChild(input);
            optionWrapper.appendChild(label);
            questionDiv.appendChild(optionWrapper);
        });

        container.appendChild(questionDiv);
    });
}

document.getElementById("reading-quiz").addEventListener("submit", function (e) {
    e.preventDefault(); // không reload

    originalQuestions.forEach((q) => {
        q.options.forEach((opt) => {
            const inputEl = document.querySelector(`input[data-option-id='${opt.id}']`);
            const parent = inputEl.closest('.form-check');

            // Loại bỏ icon cũ nếu có
            const oldIcon = parent.querySelector(".result-icon");
            if (oldIcon) oldIcon.remove();

            // Tạo icon mới
            const icon = document.createElement("span");
            icon.classList.add("result-icon", "ms-2");

            if (opt.true) {
                icon.innerHTML = "✅";
                icon.classList.add("text-success");
            } else{
                icon.innerHTML = "❌";
                icon.classList.add("text-danger");
            }

            parent.appendChild(icon);
        });
    });
});
