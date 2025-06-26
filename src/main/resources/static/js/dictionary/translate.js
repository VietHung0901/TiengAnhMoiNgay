document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const word = params.get("query");
    if (word) {
        fetchTranslate(word);
    }
});

function fetchTranslate(text) {
    const fromLang = document.getElementById("sourceLang").value;
    const toLang = document.getElementById("targetLang").value;

    const loading = document.getElementById("loading-indicator");
    if (loading) loading.style.display = "block"; // nếu có loading

    fetch(`/api/translate?text=${encodeURIComponent(text)}&from=${fromLang}&to=${toLang}`, {
        method: "GET",
        headers: {
            "Authorization": localStorage.getItem("token")
        }
    })
        .then(response => handleApiResponse(response, (data) => {
            if (loading) loading.style.display = "none";

            // Gán lại văn bản gốc và kết quả dịch
            document.getElementById("sourceText").value = text;
            document.getElementById("resultText").value = data.translatedText;
        }))
        .catch(error => {
            if (loading) loading.style.display = "none";
            showNotification("Lỗi khi gửi yêu cầu: " + error.message, "error");
        });
}

document.getElementById("swapLangs").addEventListener("click", function () {
    const fromSelect = document.getElementById("sourceLang");
    const toSelect = document.getElementById("targetLang");
    const sourceText = document.getElementById("sourceText");
    const resultText = document.getElementById("resultText");

    // 👉 Hoán đổi ngôn ngữ
    const tempLang = fromSelect.value;
    fromSelect.value = toSelect.value;
    toSelect.value = tempLang;

    // 👉 Hoán đổi nội dung
    const tempText = sourceText.value;
    sourceText.value = resultText.value;
    resultText.value = tempText;
});


document.getElementById("translateButton").addEventListener("click", function () {
    const text = document.getElementById("sourceText").value.trim();

    if (!text) {
        alert("Vui lòng nhập văn bản để dịch.");
        return;
    }

    // Hiện loading nếu bạn có
    const loading = document.getElementById("loading-indicator");
    if (loading) loading.style.display = "block";
    fetchTranslate(text);

});
