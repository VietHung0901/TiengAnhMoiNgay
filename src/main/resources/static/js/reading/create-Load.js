let questionCount = 0;

// Thêm câu hỏi mới
document.getElementById('addQuestionBtn').addEventListener('click', addQuestion);

function addQuestion() {
    questionCount++;
    const qId = `q${questionCount}`;

    const container = document.createElement('div');
    container.className = 'card mb-4';
    container.id = qId;
    container.innerHTML = `
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="card-title mb-0">Câu hỏi ${questionCount}</h5>
        <button type="button" class="btn btn-sm btn-danger" onclick="removeQuestion('${qId}')">Xóa</button>
      </div>

      <div class="mb-3">
        <label class="form-label">Nội dung câu hỏi</label>
        <input type="text" name="questions[${questionCount}].questionText"
               class="form-control" placeholder="Nhập câu hỏi..." required>
      </div>

      <div class="mb-3">
        <label class="form-label">Số đáp án đúng</label>
        <input type="number" name="questions[${questionCount}].correctCount"
               class="form-control" min="1" value="1"
               onchange="updateCorrectLimit('${qId}', this.value)" required>
      </div>

      <div class="options-container mb-3">
        <div class="option-items"></div>
        <button type="button" class="btn btn-outline-secondary btn-sm"
                onclick="addOption('${qId}')">+ Thêm đáp án</button>
      </div>
    </div>
  `;
    document.getElementById('questionsContainer').appendChild(container);

    // gán hạn mức mặc định = 1
    container.dataset.correctLimit = 1;
    // khởi tạo 2 đáp án mặc định
    addOption(qId);
    addOption(qId);
}

// Xóa cả câu hỏi
function removeQuestion(qId) {
    const deletedCard = document.getElementById(qId);
    const parent = deletedCard.parentElement;
    const cards = Array.from(parent.querySelectorAll(".card"));

    // Xác định vị trí của thẻ bị xóa
    const deletedIndex = cards.indexOf(deletedCard);

    // Xóa khỏi DOM
    deletedCard.remove();

    // Cập nhật lại tiêu đề các câu hỏi sau đó
    for (let i = deletedIndex; i < cards.length - 1; i++) {
        const card = cards[i + 1];
        const title = card.querySelector("h5.card-title");
        title.textContent = `Câu hỏi ${i + 1}`;
    }

    // Cập nhật lại biến đếm
    questionCount = document.querySelectorAll("#questionsContainer .card").length;
}

// Cập nhật giới hạn chọn đúng
function updateCorrectLimit(qId, value) {
    const container = document.getElementById(qId);
    const limit = Math.max(1, parseInt(value) || 1);
    container.dataset.correctLimit = limit;
    // nếu đã đánh dấu quá, bỏ chọn phần thừa
    const checks = container.querySelectorAll('input.correct-check');
    let count = 0;
    checks.forEach(cb => {
        if (cb.checked) {
            count++;
            if (count > limit) {
                cb.checked = false;
                count--;
            }
        }
    });
}

function addOption(qId) {
    const container = document.getElementById(qId);
    const list = container.querySelector('.option-items');
    const idx = list.children.length; // 0-based
    const letter = String.fromCharCode(65 + idx); // A,...

    const group = document.createElement('div');
    group.className = 'input-group mb-2';

    group.innerHTML = `
      <span class="input-group-text">${letter}</span>
      <input type="text"
             name="questions[${qId}].options[${idx}].optionText"
             class="form-control"
             placeholder="Nhập đáp án ${letter}"
             required>
      <span class="input-group-text checkbox-wrapper">
        <input type="checkbox"
               class="correct-check form-check-input"
               name="questions[${qId}].correctAnswers"
               value="${letter}"
               onclick="onCheck('${qId}')">
      </span>
      <button class="btn btn-outline-danger"
              type="button"
              onclick="removeOption(this, '${qId}')">✕</button>
    `;
    list.appendChild(group);
}


// Xử lý click checkbox: không cho vượt quá limit
function onCheck(qId) {
    const container = document.getElementById(qId);
    const limit = parseInt(container.dataset.correctLimit);
    const checks = container.querySelectorAll('input.correct-check');
    const checked = Array.from(checks).filter(cb => cb.checked);
    if (checked.length > limit) {
        // bỏ chọn checkbox vừa click
        checked[checked.length - 1].checked = false;
        alert(`Chỉ được chọn tối đa ${limit} đáp án đúng`);
    }
}

// Xóa 1 đáp án
function removeOption(btn, qId) {
    const group = btn.closest('.input-group');
    group.remove();
    // rebalance ký tự A,B,...
    const container = document.getElementById(qId);
    const items = container.querySelectorAll('.option-items .input-group');
    items.forEach((g, idx) => {
        const letter = String.fromCharCode(65 + idx);
        g.querySelector('.input-group-text').textContent = letter;
        g.querySelector('input[type="text"]').name =
            g.querySelector('input[type="text"]').name.replace(/\[\d+\]/, `[${idx}]`);
        const cb = g.querySelector('input.correct-check');
        cb.value = letter;
    });
}

// Preview word
document.getElementById("readingContent").addEventListener("change", function (event) {
    const file = event.target.files[0];
    if (!file || !file.name.endsWith(".docx")) {
        document.getElementById("preview").innerHTML = "<em>Only .docx files are supported for preview.</em>";
        return;
    }

    const reader = new FileReader();

    reader.onload = function (event) {
        const arrayBuffer = reader.result;
        mammoth.convertToHtml({ arrayBuffer: arrayBuffer })
            .then(function (result) {
                document.getElementById("preview").innerHTML = result.value;
            })
            .catch(function (err) {
                document.getElementById("preview").innerHTML = "Error reading file: " + err.message;
            });
    };

    reader.readAsArrayBuffer(file);
});