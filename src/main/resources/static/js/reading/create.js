const baseUrl = window.location.origin; // Lấy domain hiện tại (http://localhost:8080 hoặc domain production)
document.getElementById('readingForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const fileInput = document.getElementById("readingContent");
    const file = fileInput.files[0];
    if (!file) return alert("Vui lòng chọn một file!");

    if (file.size > 5 * 1024 * 1024) return alert("File vượt quá 5MB!");

    const formData = new FormData();
    formData.append("title", document.getElementById("readingTitle").value);
    formData.append("levelId", document.getElementById("level").value);
    formData.append("file", file);

    // === Tổng hợp câu hỏi từ giao diện ===
    const questions = [];
    document.querySelectorAll('#questionsContainer .card').forEach((card, idx) => {
        const questionText = card.querySelector('input[name$="questionText"]').value;
        const options = [];
        const checkboxes = card.querySelectorAll('.correct-check');

        card.querySelectorAll('.option-items .input-group').forEach((group, i) => {
            const optionText = group.querySelector('input[type="text"]').value;
            const isTrue = checkboxes[i].checked;
            options.push({ optionText, isTrue });
        });

        questions.push({ questionText, options });
    });

    const questionData = {
        lessonId: 0, // sẽ cập nhật bên server
        questionText: "", // bỏ qua vì bạn dùng nhiều câu hỏi
        options: [],      // bỏ qua vì bạn dùng nhiều câu hỏi
        questions: questions
    };

    formData.append("questions", new Blob([JSON.stringify(questionData)], {
        type: "application/json"
    }));

    // Gửi API
    fetch(`${baseUrl}/api/reading_lesson/create`, {
        method: "POST",
        headers: {
            'Authorization': localStorage.getItem("token")
        },
        body: formData
    }).then(res => handleApiResponse(res, (data) => {
        showNotification(data.message, "success");
        setTimeout(() => {
            window.location.href = "/view/admin/reading/list/1";
        }, 1000);
    })).catch(err => {
        showNotification("Lỗi: " + err.message, "error");
    });
});
