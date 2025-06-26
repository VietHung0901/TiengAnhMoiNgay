document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const word = params.get("query");
    if (word) {
        const wordCount = word.trim().split(/\s+/).length;

        if (wordCount === 1) {
            fetchVocabulary(word); // Gọi API tra từ
        } else {
            const pathParts = window.location.pathname.split('/');
            const role = pathParts[2]; // "admin" hoặc "user"
            window.location.href = "/view/" + role + "/dictionary/translate?query=" + encodeURIComponent(word);
        }

    }
});

function fetchVocabulary(word) {
    const loading = document.getElementById("loading-indicator");
    loading.style.display = "block"; // 👉 Hiện loading

    fetch(`https://api.dictionaryapi.dev/api/v2/entries/en/${word}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Từ không tồn tại hoặc API lỗi");
            }
            return response.json();
        })
        .then(data => {
            loading.style.display = "none"; // 👉 Ẩn loading sau khi fetch xong

            const wordData = data[0];

            // 1. Chèn từ
            document.getElementById("vocab-word").innerText = wordData.word;

            // 2. Chèn phiên âm & phát âm
            const phonetic = document.getElementById("phonetic");
            phonetic.innerHTML = "";

            const firstPhoneticWithText = wordData.phonetics.find(p => p.text);
            if (firstPhoneticWithText) {
                phonetic.innerHTML = `<span>${firstPhoneticWithText.text}</span>`;
            }

            const phoneticList = document.getElementById("phonetic-list");
            phoneticList.innerHTML = "";

            const audios = wordData.phonetics.filter(p => p.audio);
            audios.forEach((p) => {
                const li = document.createElement("li");

                // Label cho accent
                let label = "Audio";
                if (p.sourceUrl?.includes("uk") || p.audio.includes("-uk")) label = "UK";
                else if (p.sourceUrl?.includes("us") || p.audio.includes("-us")) label = "US";
                else if (p.sourceUrl?.includes("au") || p.audio.includes("-au")) label = "AU";

                // Phiên âm tương ứng (nếu có)
                const phoneticText = p.text ? ` - ${p.text}` : "";

                li.innerHTML = `
                    <span><strong>${label}:</strong></span>
                    <span class="speaker-icon" data-audio="${p.audio}" style="cursor: pointer; margin: 0 10px;">
                        🔊
                    </span>
                    <span>${phoneticText}</span>
                `;
                phoneticList.appendChild(li);
            });

            const meaningsContainer = document.getElementById("meanings");
            meaningsContainer.innerHTML = "";

            wordData.meanings.forEach(m => {
                const div = document.createElement("div");
                div.classList.add("meaning");
                div.innerHTML = `
                    <strong class="text-primary">${m.partOfSpeech}</strong>
                    <ul>
                        ${m.definitions.map(d => `
                            <li>
                                ${d.definition}
                                ${d.example ? `<br><em>Example: ${d.example}</em>` : ""}
                            </li>
                        `).join("")}
                    </ul>
                    ${m.synonyms?.length ? `<p><strong>Synonyms:</strong> ${m.synonyms.join(", ")}</p>` : ""}
                    ${m.antonyms?.length ? `<p><strong>Antonyms:</strong> ${m.antonyms.join(", ")}</p>` : ""}
                `;
                meaningsContainer.appendChild(div);
            });

            document.getElementById("source-link").href = wordData.sourceUrls[0];
            document.getElementById("source-link").innerText = wordData.sourceUrls[0];
        })
        .catch(error => {
            const pathParts = window.location.pathname.split('/');
            const role = pathParts[2]; // "admin" hoặc "user"
            window.location.href = "/view/" + role + "/dictionary/translate?query=" + encodeURIComponent(word);
        });
}

document.addEventListener("click", function (e) {
    if (e.target.classList.contains("speaker-icon")) {
        const audioSrc = e.target.getAttribute("data-audio");
        if (audioSrc) {
            const audio = new Audio(audioSrc);
            audio.play();
        }
    }
});